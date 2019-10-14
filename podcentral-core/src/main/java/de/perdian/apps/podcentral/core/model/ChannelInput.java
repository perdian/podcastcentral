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

import java.io.Serializable;
import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class ChannelInput implements Serializable {

    static final long serialVersionUID = 1L;

    private String feedUrl = null;
    private String websiteUrl = null;
    private String title = null;
    private String subtitle = null;
    private String description = null;
    private String owner = null;
    private String ownerUrl = null;
    private String languageCode = null;
    private String imageUrl = null;
    private String category = null;
    private List<EpisodeInput> episodes = null;

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

    public String getFeedUrl() {
        return this.feedUrl;
    }
    public void setFeedUrl(String feedUrl) {
        this.feedUrl = feedUrl;
    }

    public String getWebsiteUrl() {
        return this.websiteUrl;
    }
    public void setWebsiteUrl(String websiteUrl) {
        this.websiteUrl = websiteUrl;
    }

    public String getTitle() {
        return this.title;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubtitle() {
        return this.subtitle;
    }
    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public String getDescription() {
        return this.description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    public String getOwner() {
        return this.owner;
    }
    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getOwnerUrl() {
        return this.ownerUrl;
    }
    public void setOwnerUrl(String ownerUrl) {
        this.ownerUrl = ownerUrl;
    }

    public String getLanguageCode() {
        return this.languageCode;
    }
    public void setLanguageCode(String languageCode) {
        this.languageCode = languageCode;
    }

    public String getImageUrl() {
        return this.imageUrl;
    }
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getCategory() {
        return this.category;
    }
    public void setCategory(String category) {
        this.category = category;
    }

    public List<EpisodeInput> getEpisodes() {
        return this.episodes;
    }
    public void setEpisodes(List<EpisodeInput> episodes) {
        this.episodes = episodes;
    }

}
