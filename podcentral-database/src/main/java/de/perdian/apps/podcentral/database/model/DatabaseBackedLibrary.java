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

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.persistence.criteria.CriteriaQuery;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import de.perdian.apps.podcentral.core.model.Feed;
import de.perdian.apps.podcentral.core.model.FeedInput;
import de.perdian.apps.podcentral.core.model.Library;
import de.perdian.apps.podcentral.database.entities.FeedEntity;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

class DatabaseBackedLibrary implements Library, AutoCloseable {

    private SessionFactory sessionFactory = null;
    private ObservableList<Feed> feeds = null;

    public DatabaseBackedLibrary(SessionFactory sessionFactory) {
        this.setSessionFactory(sessionFactory);
        this.setFeeds(FXCollections.observableArrayList());
        this.loadFeeds();
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
        FeedEntity feedEntity = this.doWithinTransaction(session -> {

            CriteriaQuery<FeedEntity> feedQuery = session.getCriteriaBuilder().createQuery(FeedEntity.class);
            feedQuery.where(session.getCriteriaBuilder().equal(feedQuery.from(FeedEntity.class).get("url"), feedInput.getUrl()));
            List<FeedEntity> feeds = session.createQuery(feedQuery).list();

            FeedEntity feed = feeds == null || feeds.isEmpty() ? new FeedEntity() : feeds.get(0);
            feed.setCategory(feedInput.getCategory());
            feed.setDescription(feedInput.getDescription());
            feed.setImageUrl(feedInput.getImageUrl());
            feed.setLanguageCode(feedInput.getLanguageCode());
            feed.setOwner(feedInput.getOwner());
            feed.setOwnerUrl(feedInput.getOwnerUrl());
            feed.setSubtitle(feedInput.getSubtitle());
            feed.setTitle(feedInput.getTitle());
            feed.setUrl(feedInput.getUrl());
            feed.setWebsiteUrl(feedInput.getWebsiteUrl());
            session.saveOrUpdate(feed);
            return feed;

        });
        this.synchronizeFeedEntity(feedEntity);
    }

    private void loadFeeds() {
        try (Session session = this.getSessionFactory().openSession()) {
            CriteriaQuery<FeedEntity> feedQuery = session.getCriteriaBuilder().createQuery(FeedEntity.class);
            feedQuery.orderBy(session.getCriteriaBuilder().asc(feedQuery.from(FeedEntity.class).get("title")));
            List<DatabaseBackedFeed> dbFeeds = session.createQuery(feedQuery).stream().map(DatabaseBackedFeed::new).collect(Collectors.toList());
            this.getFeeds().addAll(dbFeeds);
        }
    }

    private void synchronizeFeedEntity(FeedEntity feedEntity) {
        DatabaseBackedFeed dbFeed = this.getFeeds().stream().map(feed -> (DatabaseBackedFeed)feed).filter(feed -> feed.getEntity().equals(feedEntity)).findFirst().orElse(null);
        if (dbFeed == null) {
            dbFeed = new DatabaseBackedFeed(feedEntity);
            dbFeed.updateFrom(feedEntity);
            this.getFeeds().add(dbFeed);
        } else {
            dbFeed.updateFrom(feedEntity);
        }
    }

    private <T> T doWithinTransaction(Function<Session, T> sessionConsumer) {
        try (Session session = this.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            T returnValue = sessionConsumer.apply(session);
            transaction.commit();
            return returnValue;
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
