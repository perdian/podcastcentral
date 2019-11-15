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
package de.perdian.apps.podcastcentral.ui.modules.library;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DurationFormatUtils;

import de.perdian.apps.podcastcentral.model.Episode;
import de.perdian.apps.podcastcentral.model.EpisodeDownloadState;
import de.perdian.apps.podcastcentral.model.Feed;
import de.perdian.apps.podcastcentral.model.Library;
import de.perdian.apps.podcastcentral.ui.support.properties.PropertiesHelper;
import javafx.beans.property.Property;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;

interface LibraryTreeTableValue {

    static class FeedTreeValue implements LibraryTreeTableValue {

        private Library library = null;
        private Feed feed = null;

        FeedTreeValue(Library library, Feed feed) {
            this.setLibrary(library);
            this.setFeed(feed);
        }

        @Override
        public void delete() {
            this.getLibrary().getFeeds().remove(this.getFeed());
        }

        @Override
        public Property<String> getTitle() {
            return this.getFeed().getTitle();
        }

        @Override
        public Property<String> getDescription() {
            return PropertiesHelper.map(this.getFeed().getDescription(), StringUtils::normalizeSpace, null);
        }

        Library getLibrary() {
            return this.library;
        }
        private void setLibrary(Library library) {
            this.library = library;
        }

        Feed getFeed() {
            return this.feed;
        }
        private void setFeed(Feed feed) {
            this.feed = feed;
        }

    }

    static class EpisodeTreeValue implements LibraryTreeTableValue {

        private Feed feed = null;
        private Episode episode = null;

        EpisodeTreeValue(Feed feed, Episode episode) {
            this.setFeed(feed);
            this.setEpisode(episode);
        }

        @Override
        public void delete() {
            this.getFeed().deleteEpisodes(List.of(this.getEpisode()));
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
        public ObservableValue<String> getDuration() {
            return PropertiesHelper.map(this.getEpisode().getDuration(), duration -> duration == null ? "" : DurationFormatUtils.formatDuration(duration.toMillis(), "HH:mm:ss"), null);
        }

        @Override
        public ObservableValue<String> getPublicationDate() {
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
            return PropertiesHelper.map(this.getEpisode().getPublicationDate(), date -> dateTimeFormatter.format(date.atZone(ZoneId.systemDefault())), null);
        }

        @Override
        public ObservableValue<Double> getDownloadProgress() {
            return this.getEpisode().getDownloadProgress();
        }

        @Override
        public ObservableValue<String> getDownloadProgressLabel() {
            Double progressValue = this.getEpisode().getDownloadProgress().getValue();
            NumberFormat numberFormat = new DecimalFormat("0");
            StringProperty progressLabelProperty = new SimpleStringProperty(progressValue == null || Double.valueOf(0).equals(progressValue) ? "" : (numberFormat.format(progressValue * 100d) + " %"));
            this.getEpisode().getDownloadProgress().addListener((o, oldValue, newValue) -> progressLabelProperty.setValue(newValue == null  || Double.valueOf(0).equals(newValue) ? "" : (numberFormat.format(newValue * 100d) + " %")));
            return progressLabelProperty;
        }

        @Override
        public ObservableValue<EpisodeDownloadState> getDownloadState() {
            return this.getEpisode().getDownloadState();
        }

        Feed getFeed() {
            return this.feed;
        }
        private void setFeed(Feed feed) {
            this.feed = feed;
        }

        Episode getEpisode() {
            return this.episode;
        }
        private void setEpisode(Episode episode) {
            this.episode = episode;
        }

    }

    Property<String> getTitle();
    Property<String> getDescription();
    void delete();

    default ObservableValue<String> getPublicationDate() {
        return new ReadOnlyStringWrapper();
    }

    default ObservableValue<String> getDuration() {
        return new ReadOnlyStringWrapper();
    }

    default ObservableValue<Double> getDownloadProgress() {
        return new ReadOnlyObjectWrapper<>(null);
    }
    default ObservableValue<String> getDownloadProgressLabel() {
        return new ReadOnlyStringWrapper();
    }
    default ObservableValue<EpisodeDownloadState> getDownloadState() {
        return new ReadOnlyObjectWrapper<>(null);
    }

}