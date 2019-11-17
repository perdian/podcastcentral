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
package de.perdian.apps.podcastcentral.ui.modules.library.components.treetable;

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

class LibraryTreeItemValue {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy").withLocale(Locale.getDefault());

    private Feed feed = null;
    private Episode episode = null;
    private ObservableValue<String> title = null;
    private ObservableValue<String> dateString = null;
    private ObservableValue<String> durationString = null;
    private ObservableValue<EpisodeDownloadState> downloadState = null;
    private ObservableValue<Double> downloadProgress = null;
    private ObservableValue<String> downloadProgressLabel = null;
    private ObservableValue<String> description = null;

    LibraryTreeItemValue(Feed feed) {
        this.setFeed(feed);
        this.setTitle(feed.getTitle());
        this.setDateString(new ReadOnlyStringWrapper(""));
        this.setDurationString(new ReadOnlyStringWrapper(""));
        this.setDownloadState(new ReadOnlyObjectWrapper<>(null));
        this.setDownloadProgress(new ReadOnlyObjectWrapper<>(null));
        this.setDownloadProgressLabel(new ReadOnlyStringWrapper(""));
        this.setDescription(PropertiesHelper.map(feed.getDescription(), StringUtils::normalizeSpace, null));
    }

    LibraryTreeItemValue(Episode episode) {
        this.setEpisode(episode);
        this.setTitle(episode.getTitle());
        this.setDateString(PropertiesHelper.map(episode.getPublicationDate(), date -> DATE_TIME_FORMATTER.format(date.atZone(ZoneId.systemDefault())), null));
        this.setDurationString(PropertiesHelper.map(episode.getDuration(), duration -> duration == null ? "" : DurationFormatUtils.formatDuration(duration.toMillis(), "HH:mm:ss"), null));
        this.setDownloadState(episode.getDownloadState());
        this.setDownloadProgress(episode.getDownloadProgress());
        this.setDownloadProgressLabel(LibraryTreeItemValue.createDownloadProgressLabel(episode));
        this.setDescription(PropertiesHelper.map(episode.getDescription(), StringUtils::normalizeSpace, null));
    }

    private static ObservableValue<String> createDownloadProgressLabel(Episode episode) {
        Double progressValue = episode.getDownloadProgress().getValue();
        NumberFormat numberFormat = new DecimalFormat("0");
        StringProperty progressLabelProperty = new SimpleStringProperty(progressValue == null || Double.valueOf(0).equals(progressValue) ? "" : (numberFormat.format(progressValue * 100d) + " %"));
        episode.getDownloadProgress().addListener((o, oldValue, newValue) -> progressLabelProperty.setValue(newValue == null  || Double.valueOf(0).equals(newValue) ? "" : (numberFormat.format(newValue * 100d) + " %")));
        return progressLabelProperty;
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

    ObservableValue<String> getTitle() {
        return this.title;
    }
    private void setTitle(ObservableValue<String> title) {
        this.title = title;
    }

    ObservableValue<String> getDateString() {
        return this.dateString;
    }
    private void setDateString(ObservableValue<String> dateString) {
        this.dateString = dateString;
    }

    ObservableValue<String> getDurationString() {
        return this.durationString;
    }
    private void setDurationString(ObservableValue<String> durationString) {
        this.durationString = durationString;
    }

    ObservableValue<EpisodeDownloadState> getDownloadState() {
        return this.downloadState;
    }
    private void setDownloadState(ObservableValue<EpisodeDownloadState> downloadState) {
        this.downloadState = downloadState;
    }

    ObservableValue<Double> getDownloadProgress() {
        return this.downloadProgress;
    }
    private void setDownloadProgress(ObservableValue<Double> downloadProgress) {
        this.downloadProgress = downloadProgress;
    }

    ObservableValue<String> getDownloadProgressLabel() {
        return this.downloadProgressLabel;
    }
    private void setDownloadProgressLabel(ObservableValue<String> downloadProgressLabel) {
        this.downloadProgressLabel = downloadProgressLabel;
    }

    ObservableValue<String> getDescription() {
        return this.description;
    }
    private void setDescription(ObservableValue<String> description) {
        this.description = description;
    }

}
