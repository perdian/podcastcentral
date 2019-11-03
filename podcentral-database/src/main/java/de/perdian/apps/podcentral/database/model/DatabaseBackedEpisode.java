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

import java.time.Duration;
import java.time.Instant;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.hibernate.SessionFactory;

import de.perdian.apps.podcentral.database.entities.EpisodeEntity;
import de.perdian.apps.podcentral.model.Episode;
import de.perdian.apps.podcentral.model.EpisodeData;
import de.perdian.apps.podcentral.storage.StorageDirectory;
import de.perdian.apps.podcentral.storage.StorageFile;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

class DatabaseBackedEpisode implements Episode {

    private EpisodeEntity entity = null;
    private StringProperty guid = null;
    private StringProperty title = null;
    private StringProperty subtitle = null;
    private StringProperty description = null;
    private ObjectProperty<Duration> duration = null;
    private ObjectProperty<Long> size = null;
    private ObjectProperty<Instant> creationDate = null;
    private ObjectProperty<Instant> publicationDate = null;
    private StringProperty contentUrl = null;
    private StringProperty contentType = null;
    private StringProperty websiteUrl = null;
    private StringProperty imageUrl = null;
    private StorageFile storageFile = null;

    public DatabaseBackedEpisode(EpisodeEntity episodeEntity, SessionFactory sessionFactory, StorageDirectory storageDirectory) {
        this.setEntity(episodeEntity);
        this.setContentType(DatabaseHelper.createProperty(episodeEntity, e -> e.getData().getContentType(), (e, v) -> e.getData().setContentType(v), SimpleStringProperty::new, sessionFactory));
        this.setContentUrl(DatabaseHelper.createProperty(episodeEntity, e -> e.getData().getContentUrl(), (e, v) -> e.getData().setContentUrl(v), SimpleStringProperty::new, sessionFactory));
        this.setCreationDate(DatabaseHelper.createProperty(episodeEntity, e -> e.getData().getCreationDate(), (e, v) -> e.getData().setCreationDate(v), SimpleObjectProperty::new, sessionFactory));
        this.setDescription(DatabaseHelper.createProperty(episodeEntity, e -> e.getData().getDescription(), (e, v) -> e.getData().setDescription(v), SimpleStringProperty::new, sessionFactory));
        this.setDuration(DatabaseHelper.createProperty(episodeEntity, e -> e.getData().getDuration(), (e, v) -> e.getData().setDuration(v), SimpleObjectProperty::new, sessionFactory));
        this.setGuid(DatabaseHelper.createProperty(episodeEntity, e -> e.getData().getGuid(), (e, v) -> e.getData().setGuid(v), SimpleStringProperty::new, sessionFactory));
        this.setImageUrl(DatabaseHelper.createProperty(episodeEntity, e -> e.getData().getImageUrl(), (e, v) -> e.getData().setImageUrl(v), SimpleStringProperty::new, sessionFactory));
        this.setPublicationDate(DatabaseHelper.createProperty(episodeEntity, e -> e.getData().getPublicationDate(), (e, v) -> e.getData().setPublicationDate(v), SimpleObjectProperty::new, sessionFactory));
        this.setSize(DatabaseHelper.createProperty(episodeEntity, e -> e.getData().getSize(), (e, v) -> e.getData().setSize(v), SimpleObjectProperty::new, sessionFactory));
        this.setSubtitle(DatabaseHelper.createProperty(episodeEntity, e -> e.getData().getSubtitle(), (e, v) -> e.getData().setSubtitle(v), SimpleStringProperty::new, sessionFactory));
        this.setTitle(DatabaseHelper.createProperty(episodeEntity, e -> e.getData().getTitle(), (e, v) -> e.getData().setTitle(v), SimpleStringProperty::new, sessionFactory));
        this.setWebsiteUrl(DatabaseHelper.createProperty(episodeEntity, e -> e.getData().getWebsiteUrl(), (e, v) -> e.getData().setWebsiteUrl(v), SimpleStringProperty::new, sessionFactory));
        this.setStorageFile(storageDirectory.resolveFile(this.getTitle()));
    }

    void updateData(EpisodeData episodeData) {
        this.getContentType().setValue(episodeData.getContentType());
        this.getContentUrl().setValue(episodeData.getContentUrl());
        this.getCreationDate().setValue(episodeData.getCreationDate());
        this.getDescription().setValue(episodeData.getDescription());
        this.getDuration().setValue(episodeData.getDuration());
        this.getGuid().setValue(episodeData.getGuid());
        this.getImageUrl().setValue(episodeData.getImageUrl());
        this.getPublicationDate().setValue(episodeData.getPublicationDate());
        this.getSize().setValue(episodeData.getSize());
        this.getSubtitle().setValue(episodeData.getSubtitle());
        this.getTitle().setValue(episodeData.getTitle());
        this.getWebsiteUrl().setValue(episodeData.getWebsiteUrl());
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

    EpisodeEntity getEntity() {
        return this.entity;
    }
    private void setEntity(EpisodeEntity entity) {
        this.entity = entity;
    }

    @Override
    public StringProperty getGuid() {
        return this.guid;
    }
    private void setGuid(StringProperty guid) {
        this.guid = guid;
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
    public ObjectProperty<Duration> getDuration() {
        return this.duration;
    }
    private void setDuration(ObjectProperty<Duration> duration) {
        this.duration = duration;
    }

    @Override
    public ObjectProperty<Long> getSize() {
        return this.size;
    }
    private void setSize(ObjectProperty<Long> size) {
        this.size = size;
    }

    @Override
    public ObjectProperty<Instant> getCreationDate() {
        return this.creationDate;
    }
    private void setCreationDate(ObjectProperty<Instant> creationDate) {
        this.creationDate = creationDate;
    }

    @Override
    public ObjectProperty<Instant> getPublicationDate() {
        return this.publicationDate;
    }
    private void setPublicationDate(ObjectProperty<Instant> publicationDate) {
        this.publicationDate = publicationDate;
    }

    @Override
    public StringProperty getContentUrl() {
        return this.contentUrl;
    }
    private void setContentUrl(StringProperty contentUrl) {
        this.contentUrl = contentUrl;
    }

    @Override
    public StringProperty getContentType() {
        return this.contentType;
    }
    private void setContentType(StringProperty contentType) {
        this.contentType = contentType;
    }

    @Override
    public StringProperty getWebsiteUrl() {
        return this.websiteUrl;
    }
    private void setWebsiteUrl(StringProperty websiteUrl) {
        this.websiteUrl = websiteUrl;
    }

    @Override
    public StringProperty getImageUrl() {
        return this.imageUrl;
    }
    public void setImageUrl(StringProperty imageUrl) {
        this.imageUrl = imageUrl;
    }

    @Override
    public StorageFile getStorageFile() {
        return this.storageFile;
    }
    public void setStorageFile(StorageFile storageFile) {
        this.storageFile = storageFile;
    }

}
