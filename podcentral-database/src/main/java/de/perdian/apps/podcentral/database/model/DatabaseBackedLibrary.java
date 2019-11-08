/*
 * Copyright 2019 Christian Seifert
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.perdian.apps.podcentral.database.model;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.perdian.apps.podcentral.database.entities.EpisodeEntity;
import de.perdian.apps.podcentral.database.entities.FeedEntity;
import de.perdian.apps.podcentral.model.Episode;
import de.perdian.apps.podcentral.model.EpisodeData;
import de.perdian.apps.podcentral.model.Feed;
import de.perdian.apps.podcentral.model.FeedInput;
import de.perdian.apps.podcentral.model.Library;
import de.perdian.apps.podcentral.model.LibraryListener;
import de.perdian.apps.podcentral.storage.Storage;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

class DatabaseBackedLibrary implements Library, AutoCloseable {

    private static final Logger log = LoggerFactory.getLogger(DatabaseBackedLibrary.class);

    private SessionFactory sessionFactory = null;
    private Storage storage = null;
    private Map<String, DatabaseBackedFeed> feedsByFeedUrl = null;
    private ObservableList<Feed> feeds = null;
    private List<LibraryListener> listeners = null;

    DatabaseBackedLibrary(SessionFactory sessionFactory, Storage storage) {
        this.setSessionFactory(sessionFactory);
        this.setStorage(storage);
        this.setFeedsByFeedUrl(new HashMap<>());
        this.setFeeds(FXCollections.observableArrayList());
        this.getFeeds().addListener(this::onFeedListChange);
        this.setListeners(new CopyOnWriteArrayList<>());
        this.loadInitialFeeds();
    }

    @Override
    public void close() {
        if (this.getSessionFactory() != null) {
            try {
                this.getSessionFactory().close();
            } finally {
                this.setSessionFactory(null);
            }
        }
    }

    private void loadInitialFeeds() {
        log.info("Loading initial feeds from database");
        try (Session session = this.getSessionFactory().openSession()) {

            List<EpisodeEntity> episodeEntities = session.createQuery("from EpisodeEntity where (deleted is null or deleted = false) order by publicationDate desc").list();
            Map<FeedEntity, List<EpisodeEntity>> episodeEntitiesByFeed = new LinkedHashMap<>();
            for (EpisodeEntity episodeEntity : episodeEntities) {
                episodeEntitiesByFeed.compute(episodeEntity.getFeed(), (k, v) -> v == null ? new ArrayList<>() : v).add(episodeEntity);
            }

            List<FeedEntity> feedEntities = session.createQuery("from FeedEntity order by data.title asc").list();
            for (FeedEntity feedEntity : feedEntities) {
                log.debug("Loading initial feed from database: {}", ToStringBuilder.reflectionToString(feedEntity, ToStringStyle.NO_CLASS_NAME_STYLE));
                List<EpisodeEntity> episodeEntitiesForFeed = episodeEntitiesByFeed.get(feedEntity);
                DatabaseBackedFeed feedImpl = new DatabaseBackedFeed(feedEntity, episodeEntitiesForFeed, this.getSessionFactory(), this.getStorage());
                this.getFeeds().add(feedImpl);
                this.getFeedsByFeedUrl().put(feedImpl.getUrl().getValue(), feedImpl);
            }
            log.info("Loaded {} initial feeds from database", feedEntities.size());

        }
    }

    @Override
    public Feed addFeed(FeedInput feedInput) {
        DatabaseBackedFeed feedImpl = this.getFeedsByFeedUrl().get(feedInput.getData().getUrl());
        if (feedImpl != null) {
            feedImpl.refresh(feedInput);
        } else {
            try (Session session = this.getSessionFactory().openSession()) {
                Transaction transaction = session.beginTransaction();
                FeedEntity feedEntity = new FeedEntity();
                feedEntity.setData(feedInput.getData());
                session.save(feedEntity);
                List<EpisodeEntity> episodeEntities = new ArrayList<>();
                for (EpisodeData episodeData : feedInput.getEpisodes()) {
                    EpisodeEntity episodeEntity = new EpisodeEntity();
                    episodeEntity.setFeed(feedEntity);
                    episodeEntity.setData(episodeData);
                    session.save(episodeEntity);
                    episodeEntities.add(episodeEntity);
                }
                transaction.commit();
                feedImpl = new DatabaseBackedFeed(feedEntity, episodeEntities, this.getSessionFactory(), this.getStorage());
                this.getFeedsByFeedUrl().put(feedInput.getData().getUrl(), feedImpl);
                this.getFeeds().add(feedImpl);
                FXCollections.sort(this.getFeeds(), Comparator.comparing(feed -> feed.getTitle().getValue()));
            }
        }
        return feedImpl;
    }

    private void onFeedListChange(ListChangeListener.Change<? extends Feed> change) {
        while (change.next()) {
            change.getAddedSubList().forEach(feed -> feed.getEpisodes().addListener(this::onFeedEpisodesListChange));
            change.getAddedSubList().forEach(feed -> this.getListeners().forEach(listener -> listener.onFeedAdded(feed)));
            change.getRemoved().forEach(feed -> this.onFeedDelete((DatabaseBackedFeed)feed));
            change.getRemoved().forEach(feed -> this.getListeners().forEach(listener -> listener.onFeedDeleted(feed)));
        }
    }

    private void onFeedEpisodesListChange(ListChangeListener.Change<? extends Episode> change) {
        while (change.next()) {
            change.getAddedSubList().forEach(episode -> this.getListeners().forEach(listener -> listener.onEpisodeAdded(((DatabaseBackedEpisode)episode).getFeed(), episode)));
            change.getRemoved().forEach(episode -> this.onFeedEpisodeDelete((DatabaseBackedEpisode)episode));
            change.getRemoved().forEach(episode -> this.getListeners().forEach(listener -> listener.onEpisodeDeleted(((DatabaseBackedEpisode)episode).getFeed(), episode)));
        }
    }

    private void onFeedDelete(DatabaseBackedFeed feed) {
        this.getFeedsByFeedUrl().remove(feed.getUrl().getValue());
        feed.deleteFromDatabase();
        log.error("Feed Delete not implemented yet!");
    }

    private void onFeedEpisodeDelete(DatabaseBackedEpisode episode) {
        try (Session session = this.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            episode.getEntity().setDeleted(Boolean.TRUE);
            session.update(episode.getEntity());
            transaction.commit();
        }
        log.error("Episode Delete not implemented completely yet! (Connect to Storage)");
    }

    private SessionFactory getSessionFactory() {
        return this.sessionFactory;
    }
    private void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    private Storage getStorage() {
        return this.storage;
    }
    private void setStorage(Storage storage) {
        this.storage = storage;
    }

    private Map<String, DatabaseBackedFeed> getFeedsByFeedUrl() {
        return this.feedsByFeedUrl;
    }
    private void setFeedsByFeedUrl(Map<String, DatabaseBackedFeed> feedsByFeedUrl) {
        this.feedsByFeedUrl = feedsByFeedUrl;
    }

    @Override
    public ObservableList<Feed> getFeeds() {
        return this.feeds;
    }
    private void setFeeds(ObservableList<Feed> feeds) {
        this.feeds = feeds;
    }

    @Override
    public boolean addListener(LibraryListener listener) {
        return this.getListeners().add(listener);
    }
    @Override
    public boolean removeListener(LibraryListener listener) {
        return this.getListeners().remove(listener);
    }
    private List<LibraryListener> getListeners() {
        return this.listeners;
    }
    private void setListeners(List<LibraryListener> listeners) {
        this.listeners = listeners;
    }

}
