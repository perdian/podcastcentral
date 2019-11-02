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
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.persistence.criteria.CriteriaQuery;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.perdian.apps.podcentral.database.entities.EpisodeEntity;
import de.perdian.apps.podcentral.database.entities.FeedEntity;
import de.perdian.apps.podcentral.model.Episode;
import de.perdian.apps.podcentral.model.EpisodeData;
import de.perdian.apps.podcentral.model.EpisodeDownloadState;
import de.perdian.apps.podcentral.model.Feed;
import de.perdian.apps.podcentral.model.FeedInput;
import de.perdian.apps.podcentral.model.FeedInputOptions;
import de.perdian.apps.podcentral.model.Library;
import de.perdian.apps.podcentral.storage.Storage;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

class DatabaseBackedLibrary implements Library, AutoCloseable {

    private static final Logger log = LoggerFactory.getLogger(DatabaseBackedLibrary.class);

    private SessionFactory sessionFactory = null;
    private ObservableList<Feed> feeds = null;
    private Map<FeedEntity, DatabaseBackedFeed> feedsByEntity = null;
    private Map<EpisodeEntity, DatabaseBackedEpisode> episodesByEntity = null;

    DatabaseBackedLibrary(SessionFactory sessionFactory, Storage storage) {
        this.setSessionFactory(sessionFactory);
        this.setFeeds(FXCollections.observableArrayList());
        this.setFeedsByEntity(new HashMap<>());
        this.setEpisodesByEntity(new HashMap<>());
        this.getFeeds().addListener(this::onFeedListChange);
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

    @Override
    public Feed updateFeedFromInput(FeedInput feedInput, FeedInputOptions feedInputOptions) {
        try (Session session = this.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();

            CriteriaQuery<FeedEntity> feedQuery = session.getCriteriaBuilder().createQuery(FeedEntity.class);
            feedQuery.where(session.getCriteriaBuilder().equal(feedQuery.from(FeedEntity.class).get("data").get("url"), feedInput.getData().getUrl()));
            FeedEntity feedEntity = session.createQuery(feedQuery).uniqueResultOptional().orElse(null);
            if (feedEntity == null) {
                feedEntity = new FeedEntity();
                feedEntity.setData(feedInput.getData());
                session.save(feedEntity);
            }

            CriteriaQuery<EpisodeEntity> episodeQuery = session.getCriteriaBuilder().createQuery(EpisodeEntity.class);
            episodeQuery.where(session.getCriteriaBuilder().equal(episodeQuery.from(EpisodeEntity.class).get("feed"), feedEntity));
            List<EpisodeEntity> episodes = session.createQuery(episodeQuery).list();
            Map<String, EpisodeEntity> episodesByGuid = episodes.stream().collect(Collectors.toMap(e -> e.getData().getGuid(), Function.identity()));
            List<EpisodeEntity> remainingEpisodes = new ArrayList<>(episodes);
            List<EpisodeEntity> consolidatedEpisodes = new ArrayList<>(episodes);
            for (EpisodeData episodeData : feedInput.getEpisodes()) {
                EpisodeEntity episodeEntity = episodesByGuid.get(episodeData.getGuid());
                if (episodeEntity == null) {
                    episodeEntity = new EpisodeEntity();
                    episodeEntity.setFeed(feedEntity);
                    episodeEntity.setData(episodeData);
                    consolidatedEpisodes.add(episodeEntity);
                    session.save(episodeEntity);
                }
                if (feedInputOptions.isResetDeletedEpisodes() && EpisodeDownloadState.DELETED.equals(episodeEntity.getDownloadState())) {
                    episodeEntity.setDownloadState(EpisodeDownloadState.NEW);
                    session.update(episodeEntity);
                }
                if (feedInputOptions.isResetLocalValues()) {
                    episodeEntity.setData(episodeData);
                    session.update(episodeEntity);
                }
                remainingEpisodes.remove(episodeEntity);
            }

            // If we have episodes that are marked as deleted and are no longer contained in the feed the we delete
            // them to save memory
            remainingEpisodes.stream()
                .filter(episodeEntity -> EpisodeDownloadState.DELETED.equals(episodeEntity.getDownloadState()))
                .forEach(episodeEntity -> session.delete(episodeEntity));

            DatabaseBackedFeed feedImpl = this.updateFeedFromDatabase(feedEntity, consolidatedEpisodes, session);
            transaction.commit();
            return feedImpl;

        }
    }

    DatabaseBackedFeed updateFeedFromDatabase(FeedEntity feedEntity, List<EpisodeEntity> episodeEntities, Session session) {
        synchronized (feedEntity) {
            DatabaseBackedFeed feedImpl = this.getFeedsByEntity().get(feedEntity);
            if (feedImpl == null) {
                feedImpl = this.createFeed(feedEntity);
                this.getFeedsByEntity().put(feedEntity, feedImpl);
                this.getFeeds().add(feedImpl);
            } else {
                feedImpl.updateFeed(feedEntity);
            }
            feedImpl.updateEpisodes(this.updateEpisodes(episodeEntities));
            return feedImpl;
        }
    }

    private DatabaseBackedFeed createFeed(FeedEntity feedEntity) {
        DatabaseBackedFeed feedImpl = new DatabaseBackedFeed(feedEntity, this.getSessionFactory());
        feedImpl.getEpisodes().addListener(this::onEpisodeListChange);
        return feedImpl;
    }

    private List<DatabaseBackedEpisode> updateEpisodes(Collection<EpisodeEntity> episodeEntities) {
        return episodeEntities.stream()
            .filter(episode -> !EpisodeDownloadState.DELETED.equals(episode.getDownloadState()))
            .map(this::updateEpisode)
            .collect(Collectors.toList());
    }

    private DatabaseBackedEpisode updateEpisode(EpisodeEntity episodeEntity) {
        DatabaseBackedEpisode episodeImpl = this.getEpisodesByEntity().get(episodeEntity);
        if (episodeImpl == null) {
            episodeImpl = this.createEpisode(episodeEntity);
            this.getEpisodesByEntity().put(episodeEntity, episodeImpl);
        } else {
            episodeImpl.updateEpisode(episodeEntity);
        }
        return episodeImpl;
    }

    private DatabaseBackedEpisode createEpisode(EpisodeEntity episodeEntity) {
        return new DatabaseBackedEpisode(episodeEntity, this.getSessionFactory());
    }

    private void deleteEpisodes(Collection<? extends Episode> episodes) {
        try (Session session = this.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            for (Episode episode : episodes) {
                DatabaseBackedEpisode episodeImpl = this.getEpisodesByEntity().remove(((DatabaseBackedEpisode)episode).getEntity());
                if (episodeImpl != null) {
                    log.debug("Deleting episode '{}' from feed '{}'", episodeImpl.getEntity(), episodeImpl.getEntity().getFeed());
                    episodeImpl.getEntity().setDownloadState(EpisodeDownloadState.DELETED);
                    this.deleteEpisodeFromStorage(episodeImpl);
                    session.update(episodeImpl.getEntity());
                }
            }
            transaction.commit();
        }
    }

    private void deleteEpisodeFromStorage(DatabaseBackedEpisode episode) {
        log.error("Episode Delete not implemented completely yet! (Connect to Storage)");
    }

    private void onFeedListChange(ListChangeListener.Change<? extends Feed> change) {
        List<Feed> removedFeeds = new ArrayList<>();
        while (change.next()) {
            removedFeeds.addAll(change.getRemoved());
        }
        if (!removedFeeds.isEmpty()) {
            log.info("Processing {} removed feeds", removedFeeds.size());
            log.error("Feed List Change not implemented yet!");
        }
    }

    private void onEpisodeListChange(ListChangeListener.Change<? extends Episode> change) {
        List<Episode> removedEpisodes = new ArrayList<>();
        while (change.next()) {
            removedEpisodes.addAll(change.getRemoved());
        }
        if (!removedEpisodes.isEmpty()) {
            log.info("Processing {} removed episodes", removedEpisodes.size());
            this.deleteEpisodes(removedEpisodes);
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

    private Map<FeedEntity, DatabaseBackedFeed> getFeedsByEntity() {
        return this.feedsByEntity;
    }
    private void setFeedsByEntity(Map<FeedEntity, DatabaseBackedFeed> feedsByEntity) {
        this.feedsByEntity = feedsByEntity;
    }

    private Map<EpisodeEntity, DatabaseBackedEpisode> getEpisodesByEntity() {
        return this.episodesByEntity;
    }
    private void setEpisodesByEntity(Map<EpisodeEntity, DatabaseBackedEpisode> episodesByEntity) {
        this.episodesByEntity = episodesByEntity;
    }

}
