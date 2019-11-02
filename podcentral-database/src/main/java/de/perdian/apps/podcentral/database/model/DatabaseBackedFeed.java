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
import java.util.stream.Collectors;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.hibernate.SessionFactory;

import de.perdian.apps.podcentral.database.entities.FeedEntity;
import de.perdian.apps.podcentral.model.Episode;
import de.perdian.apps.podcentral.model.EpisodeDownloadState;
import de.perdian.apps.podcentral.model.Feed;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

class DatabaseBackedFeed implements Feed {

    private FeedEntity entity = null;
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

    DatabaseBackedFeed(FeedEntity feedEntity, SessionFactory sessionFactory) {
        this.setEntity(feedEntity);
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
        this.setEpisodes(FXCollections.observableArrayList());
    }

    void updateFeed(FeedEntity feedEntity) {
        this.getCategory().setValue(feedEntity.getData().getCategory());
        this.getDescription().setValue(feedEntity.getData().getDescription());
        this.getImageUrl().setValue(feedEntity.getData().getImageUrl());
        this.getLanguageCode().setValue(feedEntity.getData().getLanguageCode());
        this.getOwner().setValue(feedEntity.getData().getOwner());
        this.getOwnerUrl().setValue(feedEntity.getData().getOwnerUrl());
        this.getSubtitle().setValue(feedEntity.getData().getSubtitle());
        this.getTitle().setValue(feedEntity.getData().getTitle());
        this.getUrl().setValue(feedEntity.getData().getUrl());
        this.getWebsiteUrl().setValue(feedEntity.getData().getWebsiteUrl());
    }

    public void updateEpisodes(List<DatabaseBackedEpisode> newEpisodes) {

        List<Episode> consolidatedEpisodes = new ArrayList<>();
        for (DatabaseBackedEpisode newEpisode : newEpisodes) {
            if (!EpisodeDownloadState.DELETED.equals(newEpisode.getDownload().getState().getValue()) && !consolidatedEpisodes.contains(newEpisode)) {
                consolidatedEpisodes.add(newEpisode);
            }
        }

        List<Episode> consolidatedEpisodesToAdd = consolidatedEpisodes.stream().filter(episode -> !this.getEpisodes().contains(episode)).collect(Collectors.toList());
        List<Episode> consolidatedEpisodesToRemove = this.getEpisodes().stream().filter(episode -> !consolidatedEpisodes.contains(episode)).collect(Collectors.toList());
        this.getEpisodes().removeAll(consolidatedEpisodesToRemove);
        this.getEpisodes().addAll(consolidatedEpisodesToAdd);
        if (!consolidatedEpisodesToAdd.isEmpty() || !consolidatedEpisodesToRemove.isEmpty()) {
            this.getEpisodes().sort(new Episode.PublishedDateComparator().reversed());
        }

    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

    FeedEntity getEntity() {
        return this.entity;
    }
    private void setEntity(FeedEntity entity) {
        this.entity = entity;
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

}
