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

import java.io.File;
import java.time.Duration;
import java.time.Instant;
import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.perdian.apps.podcastcentral.database.entities.EpisodeEntity;
import de.perdian.apps.podcastcentral.model.Episode;
import de.perdian.apps.podcastcentral.model.EpisodeData;
import de.perdian.apps.podcastcentral.model.EpisodeDownloadState;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

class DatabaseBackedEpisode implements Episode {

    private static final Logger log = LoggerFactory.getLogger(DatabaseBackedEpisode.class);

    private DatabaseBackedFeed feed = null;
    private EpisodeEntity entity = null;
    private StringProperty guid = null;
    private StringProperty title = null;
    private StringProperty subtitle = null;
    private StringProperty description = null;
    private ObjectProperty<Duration> duration = null;
    private ObjectProperty<Instant> creationDate = null;
    private ObjectProperty<Instant> publicationDate = null;
    private StringProperty contentUrl = null;
    private ObjectProperty<Long> contentSize = null;
    private StringProperty contentType = null;
    private StringProperty websiteUrl = null;
    private StringProperty imageUrl = null;
    private ObjectProperty<File> contentFile = null;
    private ObjectProperty<EpisodeDownloadState> downloadState = null;
    private ObjectProperty<Double> downloadProgress = null;
    private ObjectProperty<Exception> downloadError = null;
    private ObjectProperty<Long> downloadedBytes = null;
    private ObjectProperty<Boolean> read = null;

    public DatabaseBackedEpisode(DatabaseBackedFeed feed, EpisodeEntity episodeEntity, SessionFactory sessionFactory, File contentFile) {
        this.setFeed(feed);
        this.setEntity(episodeEntity);
        this.setContentSize(DatabaseHelper.createProperty(episodeEntity, e -> e.getData().getSize(), (e, v) -> e.getData().setSize(v), SimpleObjectProperty::new, sessionFactory));
        this.setContentType(DatabaseHelper.createProperty(episodeEntity, e -> e.getData().getContentType(), (e, v) -> e.getData().setContentType(v), SimpleStringProperty::new, sessionFactory));
        this.setContentUrl(DatabaseHelper.createProperty(episodeEntity, e -> e.getData().getContentUrl(), (e, v) -> e.getData().setContentUrl(v), SimpleStringProperty::new, sessionFactory));
        this.setCreationDate(DatabaseHelper.createProperty(episodeEntity, e -> e.getData().getCreationDate(), (e, v) -> e.getData().setCreationDate(v), SimpleObjectProperty::new, sessionFactory));
        this.setDescription(DatabaseHelper.createProperty(episodeEntity, e -> e.getData().getDescription(), (e, v) -> e.getData().setDescription(v), SimpleStringProperty::new, sessionFactory));
        this.setDownloadState(DatabaseHelper.createProperty(episodeEntity, e -> e.getDownloadState(), (e, v) -> e.setDownloadState(v), SimpleObjectProperty::new, sessionFactory));
        this.setDuration(DatabaseHelper.createProperty(episodeEntity, e -> e.getData().getDuration(), (e, v) -> e.getData().setDuration(v), SimpleObjectProperty::new, sessionFactory));
        this.setGuid(DatabaseHelper.createProperty(episodeEntity, e -> e.getData().getGuid(), (e, v) -> e.getData().setGuid(v), SimpleStringProperty::new, sessionFactory));
        this.setImageUrl(DatabaseHelper.createProperty(episodeEntity, e -> e.getData().getImageUrl(), (e, v) -> e.getData().setImageUrl(v), SimpleStringProperty::new, sessionFactory));
        this.setPublicationDate(DatabaseHelper.createProperty(episodeEntity, e -> e.getData().getPublicationDate(), (e, v) -> e.getData().setPublicationDate(v), SimpleObjectProperty::new, sessionFactory));
        this.setRead(DatabaseHelper.createProperty(episodeEntity, e -> e.getRead(), (e, v) -> e.setRead(v), SimpleObjectProperty::new, sessionFactory));
        this.setSubtitle(DatabaseHelper.createProperty(episodeEntity, e -> e.getData().getSubtitle(), (e, v) -> e.getData().setSubtitle(v), SimpleStringProperty::new, sessionFactory));
        this.setTitle(DatabaseHelper.createProperty(episodeEntity, e -> e.getData().getTitle(), (e, v) -> e.getData().setTitle(v), SimpleStringProperty::new, sessionFactory));
        this.setWebsiteUrl(DatabaseHelper.createProperty(episodeEntity, e -> e.getData().getWebsiteUrl(), (e, v) -> e.getData().setWebsiteUrl(v), SimpleStringProperty::new, sessionFactory));
        this.setContentFile(new SimpleObjectProperty<>(contentFile));
        this.setDownloadError(new SimpleObjectProperty<>());
        this.setDownloadProgress(new SimpleObjectProperty<>());
        this.setDownloadedBytes(new SimpleObjectProperty<>());
        this.computeDataFromContentFile(contentFile);
        this.getContentFile().addListener((o, oldValue, newValue) -> this.computeDataFromContentFile(newValue));
    }

    void updateData(EpisodeData episodeData) {
        this.getContentSize().setValue(episodeData.getSize());
        this.getContentType().setValue(episodeData.getContentType());
        this.getContentUrl().setValue(episodeData.getContentUrl());
        this.getCreationDate().setValue(episodeData.getCreationDate());
        this.getDescription().setValue(episodeData.getDescription());
        this.getDuration().setValue(episodeData.getDuration());
        this.getGuid().setValue(episodeData.getGuid());
        this.getImageUrl().setValue(episodeData.getImageUrl());
        this.getPublicationDate().setValue(episodeData.getPublicationDate());
        this.getSubtitle().setValue(episodeData.getSubtitle());
        this.getTitle().setValue(episodeData.getTitle());
        this.getWebsiteUrl().setValue(episodeData.getWebsiteUrl());
    }

    void computeDataFromContentFile(File storageFile) {
        if (storageFile.exists()) {
            if (EpisodeDownloadState.COMPLETED.equals(this.getDownloadState().getValue())) {
                this.getDownloadProgress().setValue(1d);
            } else if (List.of(EpisodeDownloadState.DOWNLOADING, EpisodeDownloadState.SCHEDULED).contains(this.getDownloadState().getValue())) {
                this.getDownloadState().setValue(EpisodeDownloadState.NEW);
                this.getDownloadProgress().setValue(0d);
            } else {
                this.getDownloadState().setValue(EpisodeDownloadState.ERRORED);
                this.getDownloadProgress().setValue(0d);
            }
        } else {
            this.getDownloadProgress().setValue(0d);
            if (EpisodeDownloadState.COMPLETED.equals(this.getDownloadState().getValue())) {
                this.getDownloadState().setValue(EpisodeDownloadState.MISSING);
            } else if (EpisodeDownloadState.ERRORED.equals(this.getDownloadState().getValue())) {
                this.getDownloadState().setValue(EpisodeDownloadState.ERRORED);
            } else {
                this.getDownloadState().setValue(EpisodeDownloadState.NEW);
            }
        }
    }

    void deleteContentFile() {
        File contentFile = this.getContentFile().getValue();
        if (contentFile.exists()) {
            try {
                contentFile.delete();
            } catch (Exception e) {
                log.debug("Cannot delete episode content file at: {}", contentFile.getAbsolutePath(), e);
            }
        }
    }

    @Override
    public String toString() {
        ToStringBuilder toStringBuilder = new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE);
        toStringBuilder.append("title", this.getTitle());
        toStringBuilder.append("contentUrl", this.getContentUrl());
        return toStringBuilder.toString();
    }

    @Override
    public DatabaseBackedFeed getFeed() {
        return this.feed;
    }
    private void setFeed(DatabaseBackedFeed feed) {
        this.feed = feed;
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
    public ObjectProperty<Long> getContentSize() {
        return this.contentSize;
    }
    private void setContentSize(ObjectProperty<Long> contentSize) {
        this.contentSize = contentSize;
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
    public ObjectProperty<File> getContentFile() {
        return this.contentFile;
    }
    private void setContentFile(ObjectProperty<File> contentFile) {
        this.contentFile = contentFile;
    }

    @Override
    public ObjectProperty<EpisodeDownloadState> getDownloadState() {
        return this.downloadState;
    }
    private void setDownloadState(ObjectProperty<EpisodeDownloadState> downloadState) {
        this.downloadState = downloadState;
    }

    @Override
    public ObjectProperty<Double> getDownloadProgress() {
        return this.downloadProgress;
    }
    private void setDownloadProgress(ObjectProperty<Double> downloadProgress) {
        this.downloadProgress = downloadProgress;
    }

    @Override
    public ObjectProperty<Exception> getDownloadError() {
        return this.downloadError;
    }
    private void setDownloadError(ObjectProperty<Exception> downloadError) {
        this.downloadError = downloadError;
    }

    @Override
    public ObjectProperty<Long> getDownloadedBytes() {
        return this.downloadedBytes;
    }
    private void setDownloadedBytes(ObjectProperty<Long> downloadedBytes) {
        this.downloadedBytes = downloadedBytes;
    }

    @Override
    public ObjectProperty<Boolean> getRead() {
        return this.read;
    }
    private void setRead(ObjectProperty<Boolean> read) {
        this.read = read;
    }

}
