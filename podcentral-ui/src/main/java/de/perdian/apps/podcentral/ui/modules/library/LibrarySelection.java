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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import de.perdian.apps.podcentral.model.Episode;
import de.perdian.apps.podcentral.model.EpisodeStorageState;
import de.perdian.apps.podcentral.model.Feed;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;

class LibrarySelection {

    private ObservableList<Episode> downloadableEpisodes = null;
    private ObservableList<Episode> downloadableFeedEpisodes = null;
    private ObservableMap<Feed, List<Episode>> selectedEpisodes = null;
    private ObservableList<Feed> selectedFeeds = null;

    LibrarySelection() {
        this.setDownloadableEpisodes(FXCollections.observableArrayList());
        this.setDownloadableFeedEpisodes(FXCollections.observableArrayList());
        this.setSelectedEpisodes(FXCollections.observableHashMap());
        this.setSelectedFeeds(FXCollections.observableArrayList());
    }

    public void update(List<LibraryTreeTableValue> values) {
        this.collectSelectedFeeds(values);
        this.collectSelectedEpisodes(values);
        this.collectDownloadableFeedEpisodes(values);
        this.collectDownloadableEpisodes(values);
    }

    private void collectSelectedFeeds(List<LibraryTreeTableValue> values) {
        this.getSelectedFeeds().setAll(
            values.stream()
                .filter(item -> item instanceof LibraryTreeTableValue.FeedTreeValue)
                .map(item -> ((LibraryTreeTableValue.FeedTreeValue)item).getFeed())
                .collect(Collectors.toList())
        );
    }

    private void collectSelectedEpisodes(List<LibraryTreeTableValue> values) {
        Map<Feed, List<Episode>> episodes = new HashMap<>();
        values.stream()
            .filter(item -> item instanceof LibraryTreeTableValue.EpisodeTreeValue)
            .map(item -> (LibraryTreeTableValue.EpisodeTreeValue)item)
            .forEach(item -> episodes.compute(item.getFeed(), (k, v) -> v == null ? new ArrayList<>() : v).add(item.getEpisode()));
        this.getSelectedEpisodes().clear();
        this.getSelectedEpisodes().putAll(episodes);
    }

    private void collectDownloadableFeedEpisodes(List<LibraryTreeTableValue> values) {
        this.getDownloadableFeedEpisodes().setAll(
            values.stream()
                .filter(item -> item instanceof LibraryTreeTableValue.FeedTreeValue)
                .flatMap(item -> ((LibraryTreeTableValue.FeedTreeValue)item).getFeed().getEpisodes().stream())
                .filter(episode -> !EpisodeStorageState.DOWNLOAD_COMPLETED.equals(episode.getStorageState().getValue()))
                .collect(Collectors.toSet())
        );
    }

    private void collectDownloadableEpisodes(List<LibraryTreeTableValue> values) {
        this.getDownloadableEpisodes().setAll(
            values.stream()
                .filter(item -> item instanceof LibraryTreeTableValue.EpisodeTreeValue)
                .map(item -> ((LibraryTreeTableValue.EpisodeTreeValue)item).getEpisode())
                .filter(episode -> !EpisodeStorageState.DOWNLOAD_COMPLETED.equals(episode.getStorageState().getValue()))
                .collect(Collectors.toSet())
        );
    }

    ObservableList<Feed> getSelectedFeeds() {
        return this.selectedFeeds;
    }
    private void setSelectedFeeds(ObservableList<Feed> selectedFeeds) {
        this.selectedFeeds = selectedFeeds;
    }

    ObservableMap<Feed, List<Episode>> getSelectedEpisodes() {
        return this.selectedEpisodes;
    }
    private void setSelectedEpisodes(ObservableMap<Feed, List<Episode>> selectedEpisodes) {
        this.selectedEpisodes = selectedEpisodes;
    }

    ObservableList<Episode> getDownloadableFeedEpisodes() {
        return this.downloadableFeedEpisodes;
    }
    private void setDownloadableFeedEpisodes(ObservableList<Episode> downloadableFeedEpisodes) {
        this.downloadableFeedEpisodes = downloadableFeedEpisodes;
    }

    ObservableList<Episode> getDownloadableEpisodes() {
        return this.downloadableEpisodes;
    }
    private void setDownloadableEpisodes(ObservableList<Episode> downloadableEpisodes) {
        this.downloadableEpisodes = downloadableEpisodes;
    }

}
