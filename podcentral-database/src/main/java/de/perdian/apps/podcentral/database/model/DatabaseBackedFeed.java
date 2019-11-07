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

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.CriteriaQuery;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import de.perdian.apps.podcentral.database.entities.EpisodeEntity;
import de.perdian.apps.podcentral.database.entities.FeedEntity;
import de.perdian.apps.podcentral.model.Episode;
import de.perdian.apps.podcentral.model.EpisodeData;
import de.perdian.apps.podcentral.model.EpisodeStorageState;
import de.perdian.apps.podcentral.model.Feed;
import de.perdian.apps.podcentral.model.FeedData;
import de.perdian.apps.podcentral.model.FeedInput;
import de.perdian.apps.podcentral.storage.Storage;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableBooleanValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

class DatabaseBackedFeed implements Feed {

    private FeedEntity entity = null;
    private SessionFactory sessionFactory = null;
    private StringProperty url = null;
    private StringProperty websiteUrl = null;
    private StringProperty title = null;
    private StringProperty subtitle = null;
    private StringProperty description = null;
    private StringProperty owner = null;
    private StringProperty ownerUrl = null;
    private StringProperty languageCode = null;
    private StringProperty imageUrl = null;
    private StringProperty category = null;
    private ObservableList<Episode> episodes = null;
    private ObservableList<Object> processors = null;
    private ObservableBooleanValue busy = null;
    private Storage storage = null;

    DatabaseBackedFeed(FeedEntity feedEntity, List<EpisodeEntity> episodeEntities, SessionFactory sessionFactory, Storage storage) {
        this.setEntity(feedEntity);
        this.setSessionFactory(sessionFactory);
        this.setCategory(DatabaseHelper.createProperty(feedEntity, e -> e.getData().getCategory(), (e, v) -> e.getData().setCategory(v), SimpleStringProperty::new, sessionFactory));
        this.setDescription(DatabaseHelper.createProperty(feedEntity, e -> e.getData().getDescription(), (e, v) -> e.getData().setDescription(v), SimpleStringProperty::new, sessionFactory));
        this.setImageUrl(DatabaseHelper.createProperty(feedEntity, e -> e.getData().getImageUrl(), (e, v) -> e.getData().setImageUrl(v), SimpleStringProperty::new, sessionFactory));
        this.setLanguageCode(DatabaseHelper.createProperty(feedEntity, e -> e.getData().getLanguageCode(), (e, v) -> e.getData().setLanguageCode(v), SimpleStringProperty::new, sessionFactory));
        this.setOwner(DatabaseHelper.createProperty(feedEntity, e -> e.getData().getOwner(), (e, v) -> e.getData().setOwner(v), SimpleStringProperty::new, sessionFactory));
        this.setOwnerUrl(DatabaseHelper.createProperty(feedEntity, e -> e.getData().getOwnerUrl(), (e, v) -> e.getData().setOwnerUrl(v), SimpleStringProperty::new, sessionFactory));
        this.setSubtitle(DatabaseHelper.createProperty(feedEntity, e -> e.getData().getSubtitle(), (e, v) -> e.getData().setSubtitle(v), SimpleStringProperty::new, sessionFactory));
        this.setTitle(DatabaseHelper.createProperty(feedEntity, e -> e.getData().getTitle(), (e, v) -> e.getData().setTitle(v), SimpleStringProperty::new, sessionFactory));
        this.setUrl(DatabaseHelper.createProperty(feedEntity, e -> e.getData().getUrl(), (e, v) -> e.getData().setUrl(v), SimpleStringProperty::new, sessionFactory));
        this.setWebsiteUrl(DatabaseHelper.createProperty(feedEntity, e -> e.getData().getWebsiteUrl(), (e, v) -> e.getData().setWebsiteUrl(v), SimpleStringProperty::new, sessionFactory));
        this.setEpisodes(FXCollections.observableArrayList(episodeEntities.stream().map(episodeEntity -> new DatabaseBackedEpisode(episodeEntity, sessionFactory, StringUtils.isNotEmpty(episodeEntity.getStorageFileLocation()) ? storage.resolveFileAbsolute(episodeEntity.getStorageFileLocation()) : storage.resolveFileRelative(feedEntity.getData().getTitle(), episodeEntity.getData().getTitle() + episodeEntity.getData().computeFileNameExtension()))).collect(Collectors.toList())));
        this.setProcessors(FXCollections.observableArrayList());
        this.setBusy(Bindings.isNotEmpty(this.getProcessors()));
        this.setStorage(storage);
    }

    void updateData(FeedData feedData) {
        this.getCategory().setValue(feedData.getCategory());
        this.getDescription().setValue(feedData.getDescription());
        this.getImageUrl().setValue(feedData.getImageUrl());
        this.getLanguageCode().setValue(feedData.getLanguageCode());
        this.getOwner().setValue(feedData.getOwner());
        this.getOwnerUrl().setValue(feedData.getOwnerUrl());
        this.getSubtitle().setValue(feedData.getSubtitle());
        this.getTitle().setValue(feedData.getTitle());
        this.getUrl().setValue(feedData.getUrl());
        this.getWebsiteUrl().setValue(feedData.getWebsiteUrl());
    }

    @Override
    public synchronized void refresh(FeedInput feedInput, RefreshOption... refreshOptions) {
        Set<RefreshOption> refreshOptionsSet = Set.of(refreshOptions);
        if (refreshOptionsSet.contains(RefreshOption.OVERWRITE_CHANGED_VALUES)) {
            this.updateData(feedInput.getData());
        }
        try (Session session = this.getSessionFactory().openSession()) {

            CriteriaQuery<EpisodeEntity> episodeEntityQuery = session.getCriteriaBuilder().createQuery(EpisodeEntity.class);
            episodeEntityQuery.where(session.getCriteriaBuilder().equal(episodeEntityQuery.from(EpisodeEntity.class).get("feed"), this.getEntity()));
            List<EpisodeEntity> episodeEntities = session.createQuery(episodeEntityQuery).list();
            Map<String, EpisodeEntity> episodeEntitiesByGuid = new HashMap<>(episodeEntities.stream().collect(Collectors.toMap(episodeEntity -> episodeEntity.getData().getGuid(), Function.identity())));
            Map<String, DatabaseBackedEpisode> episodesByGuid = this.getEpisodes().stream().map(episode -> (DatabaseBackedEpisode)episode).collect(Collectors.toMap(episode -> episode.getGuid().getValue(), Function.identity()));

            List<DatabaseBackedEpisode> newEpisodes = new ArrayList<>();
            for (EpisodeData episodeData : feedInput.getEpisodes()) {
                DatabaseBackedEpisode episode = episodesByGuid.get(episodeData.getGuid());
                EpisodeEntity episodeEntityFromDatabase = episodeEntitiesByGuid.remove(episodeData.getGuid());
                if (episode != null && refreshOptionsSet.contains(RefreshOption.OVERWRITE_CHANGED_VALUES)) {
                    episode.updateData(episodeData);
                } else if (episode == null) {
                    File episodeFile = this.getStorage().resolveFileRelative(feedInput.getData().getTitle(), episodeData.getTitle() + episodeData.computeFileNameExtension());
                    if (episodeEntityFromDatabase != null && refreshOptionsSet.contains(RefreshOption.RESTORE_DELETED_EPISODES)) {
                        episodeEntityFromDatabase.setDeleted(Boolean.FALSE);
                        episodeEntityFromDatabase.setStorageState(EpisodeStorageState.NEW);
                        session.update(episodeEntityFromDatabase);
                        newEpisodes.add(new DatabaseBackedEpisode(episodeEntityFromDatabase, this.getSessionFactory(), episodeFile));
                    } else if (episodeEntityFromDatabase == null) {
                        episodeEntityFromDatabase = new EpisodeEntity();
                        episodeEntityFromDatabase.setFeed(this.getEntity());
                        episodeEntityFromDatabase.setData(episodeData);
                        session.update(episodeEntityFromDatabase);
                        newEpisodes.add(new DatabaseBackedEpisode(episodeEntityFromDatabase, this.getSessionFactory(), episodeFile));
                    }
                }
            }
            if (!newEpisodes.isEmpty()) {
                this.getEpisodes().addAll(newEpisodes);
                FXCollections.sort(this.getEpisodes(), new Episode.PublishedDateComparator().reversed());
            }

            // If we have episodes that are marked as deleted and are no longer contained in the feed the we delete
            // them to save memory
            episodeEntitiesByGuid.values().stream()
                .filter(episodeEntity -> Boolean.TRUE.equals(episodeEntity.getDeleted()))
                .forEach(episodeEntity -> session.delete(episodeEntity));

        }
    }

    void deleteFromDatabase() {
        try (Session session = this.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            CriteriaDelete<EpisodeEntity> episodeEntityDelete = session.getCriteriaBuilder().createCriteriaDelete(EpisodeEntity.class);
            episodeEntityDelete.where(session.getCriteriaBuilder().equal(episodeEntityDelete.from(EpisodeEntity.class).get("feed"), this.getEntity()));
            session.createQuery(episodeEntityDelete).executeUpdate();
            session.delete(this.getEntity());
            transaction.commit();
        }
    }

    @Override
    public String toString() {
        ToStringBuilder toStringBuilder = new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE);
        toStringBuilder.append("title", this.getTitle());
        toStringBuilder.append("url", this.getUrl());
        return toStringBuilder.toString();
    }

    FeedEntity getEntity() {
        return this.entity;
    }
    private void setEntity(FeedEntity entity) {
        this.entity = entity;
    }

    private SessionFactory getSessionFactory() {
        return this.sessionFactory;
    }
    private void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public StringProperty getUrl() {
        return this.url;
    }
    private void setUrl(StringProperty url) {
        this.url = url;
    }

    @Override
    public StringProperty getWebsiteUrl() {
        return this.websiteUrl;
    }
    private void setWebsiteUrl(StringProperty websiteUrl) {
        this.websiteUrl = websiteUrl;
    }

    @Override
    public StringProperty getTitle() {
        return this.title;
    }
    private void setTitle(StringProperty title) {
        this.title = title;
    }

    @Override
    public StringProperty getSubtitle() {
        return this.subtitle;
    }
    private void setSubtitle(StringProperty subtitle) {
        this.subtitle = subtitle;
    }

    @Override
    public StringProperty getDescription() {
        return this.description;
    }
    private void setDescription(StringProperty description) {
        this.description = description;
    }

    @Override
    public StringProperty getOwner() {
        return this.owner;
    }
    private void setOwner(StringProperty owner) {
        this.owner = owner;
    }

    @Override
    public StringProperty getOwnerUrl() {
        return this.ownerUrl;
    }
    private void setOwnerUrl(StringProperty ownerUrl) {
        this.ownerUrl = ownerUrl;
    }

    @Override
    public StringProperty getLanguageCode() {
        return this.languageCode;
    }
    private void setLanguageCode(StringProperty languageCode) {
        this.languageCode = languageCode;
    }

    @Override
    public StringProperty getImageUrl() {
        return this.imageUrl;
    }
    private void setImageUrl(StringProperty imageUrl) {
        this.imageUrl = imageUrl;
    }

    @Override
    public StringProperty getCategory() {
        return this.category;
    }
    private void setCategory(StringProperty category) {
        this.category = category;
    }

    @Override
    public ObservableList<Episode> getEpisodes() {
        return this.episodes;
    }
    private void setEpisodes(ObservableList<Episode> episodes) {
        this.episodes = episodes;
    }

    @Override
    public ObservableList<Object> getProcessors() {
        return this.processors;
    }
    public void setProcessors(ObservableList<Object> processors) {
        this.processors = processors;
    }

    @Override
    public ObservableBooleanValue getBusy() {
        return this.busy;
    }
    private void setBusy(ObservableBooleanValue busy) {
        this.busy = busy;
    }

    private Storage getStorage() {
        return this.storage;
    }
    private void setStorage(Storage storage) {
        this.storage = storage;
    }

}
