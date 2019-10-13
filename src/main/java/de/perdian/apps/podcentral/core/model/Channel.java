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
package de.perdian.apps.podcentral.core.model;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;

public class Channel {

    private StringProperty feedUrl = null;
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

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

    public StringProperty getFeedUrl() {
        return this.feedUrl;
    }
    public void setFeedUrl(StringProperty feedUrl) {
        this.feedUrl = feedUrl;
    }

    public StringProperty getWebsiteUrl() {
        return this.websiteUrl;
    }
    public void setWebsiteUrl(StringProperty websiteUrl) {
        this.websiteUrl = websiteUrl;
    }

    public StringProperty getTitle() {
        return this.title;
    }
    public void setTitle(StringProperty title) {
        this.title = title;
    }

    public StringProperty getSubtitle() {
        return this.subtitle;
    }
    public void setSubtitle(StringProperty subtitle) {
        this.subtitle = subtitle;
    }

    public StringProperty getDescription() {
        return this.description;
    }
    public void setDescription(StringProperty description) {
        this.description = description;
    }

    public StringProperty getOwner() {
        return this.owner;
    }
    public void setOwner(StringProperty owner) {
        this.owner = owner;
    }

    public StringProperty getOwnerUrl() {
        return this.ownerUrl;
    }
    public void setOwnerUrl(StringProperty ownerUrl) {
        this.ownerUrl = ownerUrl;
    }

    public StringProperty getLanguageCode() {
        return this.languageCode;
    }
    public void setLanguageCode(StringProperty languageCode) {
        this.languageCode = languageCode;
    }

    public StringProperty getImageUrl() {
        return this.imageUrl;
    }
    public void setImageUrl(StringProperty imageUrl) {
        this.imageUrl = imageUrl;
    }

    public StringProperty getCategory() {
        return this.category;
    }
    public void setCategory(StringProperty category) {
        this.category = category;
    }

    public ObservableList<Episode> getEpisodes() {
        return this.episodes;
    }
    public void setEpisodes(ObservableList<Episode> episodes) {
        this.episodes = episodes;
    }

}
