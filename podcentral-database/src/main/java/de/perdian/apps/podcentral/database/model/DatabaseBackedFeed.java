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

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import de.perdian.apps.podcentral.core.model.Episode;
import de.perdian.apps.podcentral.core.model.Feed;
import de.perdian.apps.podcentral.database.entities.FeedEntity;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class DatabaseBackedFeed implements Feed {

    private FeedEntity entity = null;
    private final StringProperty url = new SimpleStringProperty();
    private final StringProperty websiteUrl = new SimpleStringProperty();
    private final StringProperty title = new SimpleStringProperty();
    private final StringProperty subtitle = new SimpleStringProperty();
    private final StringProperty description = new SimpleStringProperty();
    private final StringProperty owner = new SimpleStringProperty();
    private final StringProperty ownerUrl = new SimpleStringProperty();
    private final StringProperty languageCode = new SimpleStringProperty();
    private final StringProperty imageUrl = new SimpleStringProperty();
    private final StringProperty category = new SimpleStringProperty();
    private final ObservableList<Episode> episodes = FXCollections.observableArrayList();

    DatabaseBackedFeed(FeedEntity entity) {
        this.setEntity(entity);
        this.updateFrom(entity);
    }

    void updateFrom(FeedEntity feedEntity) {
        this.getTitle().setValue(feedEntity.getTitle());
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

    @Override
    public StringProperty getWebsiteUrl() {
        return this.websiteUrl;
    }

    @Override
    public StringProperty getTitle() {
        return this.title;
    }

    @Override
    public StringProperty getSubtitle() {
        return this.subtitle;
    }

    @Override
    public StringProperty getDescription() {
        return this.description;
    }

    @Override
    public StringProperty getOwner() {
        return this.owner;
    }

    @Override
    public StringProperty getOwnerUrl() {
        return this.ownerUrl;
    }

    @Override
    public StringProperty getLanguageCode() {
        return this.languageCode;
    }

    @Override
    public StringProperty getImageUrl() {
        return this.imageUrl;
    }

    @Override
    public StringProperty getCategory() {
        return this.category;
    }

    @Override
    public ObservableList<Episode> getEpisodes() {
        return this.episodes;
    }

}
