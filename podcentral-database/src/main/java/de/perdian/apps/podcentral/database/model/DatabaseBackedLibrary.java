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

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

import javax.persistence.criteria.CriteriaQuery;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import de.perdian.apps.podcentral.core.impl.AbstractLibrary;
import de.perdian.apps.podcentral.core.model.EpisodeData;
import de.perdian.apps.podcentral.core.model.EpisodeDownloadState;
import de.perdian.apps.podcentral.core.model.Feed;
import de.perdian.apps.podcentral.core.model.FeedInput;
import de.perdian.apps.podcentral.database.entities.EpisodeEntity;
import de.perdian.apps.podcentral.database.entities.FeedEntity;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

class DatabaseBackedLibrary extends AbstractLibrary implements AutoCloseable {

    private SessionFactory sessionFactory = null;
    private ObservableList<Feed> feeds = null;
    private ObjectProperty<Path> storageDirectory = null;

    public DatabaseBackedLibrary(SessionFactory sessionFactory, Path storageDirectory) {
        this.setSessionFactory(sessionFactory);
        this.setFeeds(FXCollections.observableArrayList());
        this.setStorageDirectory(new SimpleObjectProperty<>(storageDirectory));
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

    void loadInitialFeeds() {
        try (Session session = this.getSessionFactory().openSession()) {

            List<FeedEntity> feedEntities = session.createQuery("from FeedEntity").list();
            feedEntities.sort(Comparator.comparing(feedEntity -> feedEntity.getData().getTitle()));

            List<EpisodeEntity> episodeEntities = session.createQuery("from EpisodeEntity").list();
            Map<FeedEntity, List<EpisodeEntity>> episodeEntitiesByFeed = new HashMap<>();
            for (EpisodeEntity episodeEntity : episodeEntities) {
                episodeEntitiesByFeed.compute(episodeEntity.getFeed(), (k, v) -> v == null ? new ArrayList<>() : v).add(episodeEntity);
            }

            List<DatabaseBackedFeed> feeds = new ArrayList<>();
            for (FeedEntity feedEntity : feedEntities) {
                List<EpisodeEntity> episodeEntitiesForFeed = Optional.ofNullable(episodeEntitiesByFeed.get(feedEntity)).orElseGet(ArrayList::new);
                episodeEntitiesForFeed.sort(new EpisodeEntity.PublicationDateComparator().reversed());
                feeds.add(new DatabaseBackedFeed(feedEntity, episodeEntitiesForFeed, this.getSessionFactory()));
            }
            this.getFeeds().setAll(feeds);

        }
    }

    @Override
    public void addFeedForInput(FeedInput feedInput) {
        this.doWithinTransaction(session -> {
            CriteriaQuery<FeedEntity> feedQuery = session.getCriteriaBuilder().createQuery(FeedEntity.class);
            feedQuery.where(session.getCriteriaBuilder().equal(feedQuery.from(FeedEntity.class).get("data").get("url"), feedInput.getData().getUrl()));
            FeedEntity feedEntity = session.createQuery(feedQuery).uniqueResultOptional().orElse(null);
            if (feedEntity == null) {
                feedEntity = new FeedEntity();
                feedEntity.setData(feedInput.getData());
                session.save(feedEntity);
                List<EpisodeEntity> episodeEntities = new ArrayList<>();
                for (EpisodeData episodeData : feedInput.getEpisodes()) {
                    EpisodeEntity episodeEntity = new EpisodeEntity();
                    episodeEntity.setFeed(feedEntity);
                    episodeEntity.setData(episodeData);
                    episodeEntity.setDownloadState(EpisodeDownloadState.NEW);
                    session.save(episodeEntity);
                    episodeEntities.add(episodeEntity);
                }
                this.getFeeds().add(new DatabaseBackedFeed(feedEntity, episodeEntities, this.getSessionFactory()));
            }
        });
    }

    private void doWithinTransaction(Consumer<Session> sessionConsumer) {
        try (Session session = this.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            sessionConsumer.accept(session);
            transaction.commit();
        }
    }

    SessionFactory getSessionFactory() {
        return this.sessionFactory;
    }
    private void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public ObservableList<Feed> getFeeds() {
        return this.feeds;
    }
    private void setFeeds(ObservableList<Feed> feeds) {
        this.feeds = feeds;
    }

    public ObjectProperty<Path> getStorageDirectory() {
        return this.storageDirectory;
    }
    private void setStorageDirectory(ObjectProperty<Path> storageDirectory) {
        this.storageDirectory = storageDirectory;
    }

}
