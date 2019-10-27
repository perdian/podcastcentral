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
import java.util.List;
import java.util.function.Consumer;

import javax.persistence.criteria.CriteriaQuery;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import de.perdian.apps.podcentral.core.model.EpisodeData;
import de.perdian.apps.podcentral.core.model.EpisodeLocalState;
import de.perdian.apps.podcentral.core.model.Feed;
import de.perdian.apps.podcentral.core.model.FeedInput;
import de.perdian.apps.podcentral.core.model.Library;
import de.perdian.apps.podcentral.database.entities.EpisodeEntity;
import de.perdian.apps.podcentral.database.entities.FeedEntity;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

class DatabaseBackedLibrary implements Library, AutoCloseable {

    private SessionFactory sessionFactory = null;
    private ObservableList<Feed> feeds = null;

    public DatabaseBackedLibrary(SessionFactory sessionFactory) {
        this.setSessionFactory(sessionFactory);
        this.setFeeds(FXCollections.observableArrayList());
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
                    episodeEntity.setLocalState(EpisodeLocalState.NEW);
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

}
