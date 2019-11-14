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
package de.perdian.apps.podcastcentral.database.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.persistence.criteria.CriteriaDelete;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.perdian.apps.podcastcentral.database.entities.EpisodeEntity;
import de.perdian.apps.podcastcentral.database.entities.FeedEntity;
import de.perdian.apps.podcastcentral.model.EpisodeData;
import de.perdian.apps.podcastcentral.model.Feed;
import de.perdian.apps.podcastcentral.model.FeedCollection;
import de.perdian.apps.podcastcentral.model.FeedInput;
import de.perdian.apps.podcastcentral.model.Library;
import de.perdian.apps.podcastcentral.storage.Storage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

class DatabaseBackedLibrary implements Library, AutoCloseable {

    private static final Logger log = LoggerFactory.getLogger(DatabaseBackedLibrary.class);

    private SessionFactory sessionFactory = null;
    private Storage storage = null;
    private Map<String, DatabaseBackedFeed> feedsByFeedUrl = null;
    private ObservableList<Feed> feeds = null;

    DatabaseBackedLibrary(SessionFactory sessionFactory, Storage storage) {
        this.setSessionFactory(sessionFactory);
        this.setStorage(storage);
        this.setFeedsByFeedUrl(new HashMap<>());
        this.setFeeds(FXCollections.observableArrayList());
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
                DatabaseBackedFeed feedImpl = new DatabaseBackedFeed(feedEntity, episodeEntitiesForFeed, this.getSessionFactory(), this.getStorage().resolveDirectory(feedEntity.getData().getTitle()));
                this.getFeeds().add(feedImpl);
                this.getFeedsByFeedUrl().put(feedImpl.getUrl().getValue(), feedImpl);
            }
            log.info("Loaded {} initial feeds from database", feedEntities.size());

        }
    }

    @Override
    public Feed addFeed(FeedInput feedInput, Feed.RefreshOption... refreshOptions) {
        DatabaseBackedFeed feedImpl = this.getFeedsByFeedUrl().get(feedInput.getData().getUrl());
        if (feedImpl != null) {
            feedImpl.refresh(feedInput, refreshOptions);
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
                feedImpl = new DatabaseBackedFeed(feedEntity, episodeEntities, this.getSessionFactory(), this.getStorage().resolveDirectory(feedEntity.getData().getTitle()));
                this.getFeedsByFeedUrl().put(feedInput.getData().getUrl(), feedImpl);
                this.getFeeds().add(feedImpl);
                FXCollections.sort(this.getFeeds(), Comparator.comparing(feed -> feed.getTitle().getValue()));
            }
        }
        return feedImpl;
    }

    @Override
    public synchronized void deleteFeeds(Collection<Feed> feeds) {
        if (!feeds.isEmpty()) {
            try (Session session = this.getSessionFactory().openSession()) {
                for (Feed feed : feeds) {
                    DatabaseBackedFeed feedImpl = (DatabaseBackedFeed)feed;
                    if (this.getFeeds().remove(feed)) {

                        feedImpl.getStorageDirectory().delete();

                        Transaction transaction = session.beginTransaction();
                        CriteriaDelete<EpisodeEntity> episodeEntityDelete = session.getCriteriaBuilder().createCriteriaDelete(EpisodeEntity.class);
                        episodeEntityDelete.where(session.getCriteriaBuilder().equal(episodeEntityDelete.from(EpisodeEntity.class).get("feed"), feedImpl.getEntity()));
                        session.createQuery(episodeEntityDelete).executeUpdate();
                        session.delete(feedImpl.getEntity());
                        transaction.commit();

                    }
                }
            }
        }
    }

    @Override
    public FeedCollection toFeedCollection() {
        return new DatabaseBackedFeedCollection(this.getFeeds().stream().map(feed -> new DatabaseBackedFeedCollectionItem((DatabaseBackedFeed)feed)).collect(Collectors.toList()));
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

}
