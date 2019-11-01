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
package de.perdian.apps.podcentral.ui.modules.library;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DurationFormatUtils;

import de.perdian.apps.podcentral.core.model.Episode;
import de.perdian.apps.podcentral.core.model.Feed;
import de.perdian.apps.podcentral.ui.support.properties.PropertiesHelper;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleStringProperty;

interface LibraryTreeValue {

    static class FeedTreeValue implements LibraryTreeValue {

        private Feed feed = null;

        FeedTreeValue(Feed feed) {
            this.setFeed(feed);
        }

        @Override
        public Property<String> getTitle() {
            return this.getFeed().getTitle();
        }

        @Override
        public Property<String> getDescription() {
            return PropertiesHelper.map(this.getFeed().getDescription(), StringUtils::normalizeSpace, null);
        }

        private Feed getFeed() {
            return this.feed;
        }
        private void setFeed(Feed feed) {
            this.feed = feed;
        }

    }

    static class EpisodeTreeValue implements LibraryTreeValue {

        private Episode episode = null;

        EpisodeTreeValue(Episode episode) {
            this.setEpisode(episode);
        }

        @Override
        public Property<String> getTitle() {
            return this.getEpisode().getTitle();
        }

        @Override
        public Property<String> getDescription() {
            return PropertiesHelper.map(this.getEpisode().getDescription(), StringUtils::normalizeSpace, null);
        }

        @Override
        public Property<String> getDuration() {
            return PropertiesHelper.map(this.getEpisode().getDuration(), duration -> duration == null ? "" : DurationFormatUtils.formatDuration(duration.toMillis(), "HH:mm:ss"), null);
        }

        @Override
        public Property<String> getPublicationDate() {
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
            return PropertiesHelper.map(this.getEpisode().getPublicationDate(), date -> dateTimeFormatter.format(date.atZone(ZoneId.systemDefault())), null);
        }

        private Episode getEpisode() {
            return this.episode;
        }
        private void setEpisode(Episode episode) {
            this.episode = episode;
        }

    }

    Property<String> getTitle();
    Property<String> getDescription();

    default Property<String> getPublicationDate() {
        return new SimpleStringProperty();
    }

    default Property<String> getDuration() {
        return new SimpleStringProperty();
    }

}
