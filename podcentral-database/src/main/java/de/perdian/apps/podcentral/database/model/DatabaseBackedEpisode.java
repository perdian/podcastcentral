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

import de.perdian.apps.podcentral.core.model.Episode;
import de.perdian.apps.podcentral.core.model.EpisodeLocalState;
import de.perdian.apps.podcentral.database.entities.EpisodeEntity;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.StringProperty;

public class DatabaseBackedEpisode implements Episode {

    private EpisodeEntity entity = null;
    private StringProperty title = null;
    private StringProperty subtitle = null;
    private StringProperty description = null;
    private ObjectProperty<Duration> duration = null;
    private ObjectProperty<Long> size = null;
    private ObjectProperty<Instant> creationDate = null;
    private ObjectProperty<Instant> publicationDate = null;
    private ObjectProperty<Instant> downloadDate = null;
    private ObjectProperty<EpisodeLocalState> localState = null;
    private StringProperty contentUrl = null;
    private StringProperty contentType = null;
    private StringProperty websiteUrl = null;
    private StringProperty imageUrl = null;

    public DatabaseBackedEpisode(EpisodeEntity episodeEntity, SessionFactory sessionFactory) {
        this.setEntity(episodeEntity);

        DatabasePropertyFactory<EpisodeEntity> propertyFactory = new DatabasePropertyFactory<>(episodeEntity, sessionFactory);
        this.setContentType(propertyFactory.createProperty(e -> e.getData().getContentType(), (e, v) -> e.getData().setContentType(v)));
        this.setContentUrl(propertyFactory.createProperty(e -> e.getData().getContentUrl(), (e, v) -> e.getData().setContentUrl(v)));
        this.setCreationDate(propertyFactory.createProperty(e -> e.getData().getCreationDate(), (e, v) -> e.getData().setCreationDate(v), SimpleObjectProperty::new));
        this.setDescription(propertyFactory.createProperty(e -> e.getData().getDescription(), (e, v) -> e.getData().setDescription(v)));
        this.setDownloadDate(propertyFactory.createProperty(e -> e.getDownloadDate(), (e, v) -> e.setDownloadDate(v), SimpleObjectProperty::new));
        this.setDuration(propertyFactory.createProperty(e -> e.getData().getDuration(), (e, v) -> e.getData().setDuration(v), SimpleObjectProperty::new));
        this.setImageUrl(propertyFactory.createProperty(e -> e.getData().getImageUrl(), (e, v) -> e.getData().setImageUrl(v)));
        this.setLocalState(propertyFactory.createProperty(e -> e.getLocalState(), (e, v) -> e.setLocalState(v), SimpleObjectProperty::new));
        this.setPublicationDate(propertyFactory.createProperty(e -> e.getData().getPublicationDate(), (e, v) -> e.getData().setPublicationDate(v), SimpleObjectProperty::new));
        this.setSize(propertyFactory.createProperty(e -> e.getData().getSize(), (e, v) -> e.getData().setSize(v), SimpleObjectProperty::new));
        this.setSubtitle(propertyFactory.createProperty(e -> e.getData().getSubtitle(), (e, v) -> e.getData().setSubtitle(v)));
        this.setTitle(propertyFactory.createProperty(e -> e.getData().getTitle(), (e, v) -> e.getData().setTitle(v)));
        this.setWebsiteUrl(propertyFactory.createProperty(e -> e.getData().getWebsiteUrl(), (e, v) -> e.getData().setWebsiteUrl(v)));

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
    public ObjectProperty<Instant> getDownloadDate() {
        return this.downloadDate;
    }
    private void setDownloadDate(ObjectProperty<Instant> downloadDate) {
        this.downloadDate = downloadDate;
    }

    @Override
    public ObjectProperty<EpisodeLocalState> getLocalState() {
        return this.localState;
    }
    private void setLocalState(ObjectProperty<EpisodeLocalState> localState) {
        this.localState = localState;
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

}