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
package de.perdian.apps.podcastcentral.ui.modules.library_new.treetable;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DurationFormatUtils;

import de.perdian.apps.podcastcentral.model.Episode;
import de.perdian.apps.podcastcentral.model.EpisodeDownloadState;
import de.perdian.apps.podcastcentral.model.Feed;
import de.perdian.apps.podcastcentral.ui.support.properties.PropertiesHelper;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;

interface LibraryTreeItemValue {

    ObservableValue<String> getTitle();
    ObservableValue<String> getDateString();
    ObservableValue<String> getDurationString();
    ObservableValue<EpisodeDownloadState> getDownloadState();
    ObservableValue<Double> getDownloadProgress();
    ObservableValue<String> getDownloadProgressLabel();
    ObservableValue<String> getDescription();

    static class FeedItemValue implements LibraryTreeItemValue {

        private Feed feed = null;

        FeedItemValue(Feed feed) {
            this.setFeed(feed);
        }

        @Override
        public ObservableValue<String> getTitle() {
            return this.getFeed().getTitle();
        }

        @Override
        public ObservableValue<String> getDateString() {
            return new ReadOnlyStringWrapper("");
        }

        @Override
        public ObservableValue<String> getDurationString() {
            return new ReadOnlyStringWrapper("");
        }

        @Override
        public ObservableValue<EpisodeDownloadState> getDownloadState() {
            return new ReadOnlyObjectWrapper<>(null);
        }

        @Override
        public ObservableValue<Double> getDownloadProgress() {
            return new ReadOnlyObjectWrapper<>(null);
        }

        @Override
        public ObservableValue<String> getDownloadProgressLabel() {
            return new ReadOnlyStringWrapper("");
        }

        @Override
        public ObservableValue<String> getDescription() {
            return PropertiesHelper.map(this.getFeed().getDescription(), StringUtils::normalizeSpace, null);
        }

        Feed getFeed() {
            return this.feed;
        }
        private void setFeed(Feed feed) {
            this.feed = feed;
        }

    }

    static class EpisodeItemValue implements LibraryTreeItemValue {

        private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy").withLocale(Locale.getDefault());

        private Feed feed = null;
        private Episode episode = null;

        EpisodeItemValue(Feed feed, Episode episode) {
            this.setFeed(feed);
            this.setEpisode(episode);
        }

        @Override
        public ObservableValue<String> getTitle() {
            return this.getEpisode().getTitle();
        }

        @Override
        public ObservableValue<String> getDateString() {
            return PropertiesHelper.map(this.getEpisode().getPublicationDate(), date -> DATE_TIME_FORMATTER.format(date.atZone(ZoneId.systemDefault())), null);
        }

        @Override
        public ObservableValue<String> getDurationString() {
            return PropertiesHelper.map(this.getEpisode().getDuration(), duration -> duration == null ? "" : DurationFormatUtils.formatDuration(duration.toMillis(), "HH:mm:ss"), null);
        }

        @Override
        public ObservableValue<EpisodeDownloadState> getDownloadState() {
            return this.getEpisode().getDownloadState();
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
        public ObservableValue<String> getDescription() {
            return PropertiesHelper.map(this.getEpisode().getDescription(), StringUtils::normalizeSpace, null);
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

}
