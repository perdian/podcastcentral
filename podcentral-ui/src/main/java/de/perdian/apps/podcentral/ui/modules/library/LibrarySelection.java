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
import de.perdian.apps.podcentral.model.EpisodeDownloadState;
import de.perdian.apps.podcentral.model.Feed;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableView.TreeTableViewSelectionModel;

class LibrarySelection {

    private ObservableList<Episode> downloadableEpisodes = null;
    private ObservableList<Episode> downloadableFeedEpisodes = null;
    private ObservableList<Episode> cancelableEpisodes = null;
    private ObservableMap<Feed, List<Episode>> selectedEpisodes = null;
    private ObservableList<Episode> selectedEpisodesAsList = null;
    private ObservableList<Feed> selectedFeeds = null;
    private TreeTableViewSelectionModel<LibraryTreeTableValue> selectionModel = null;

    LibrarySelection(TreeTableViewSelectionModel<LibraryTreeTableValue> selectionModel) {
        this.setSelectionModel(selectionModel);
        this.setCancelableEpisodes(FXCollections.observableArrayList());
        this.setDownloadableEpisodes(FXCollections.observableArrayList());
        this.setDownloadableFeedEpisodes(FXCollections.observableArrayList());
        this.setSelectedEpisodes(FXCollections.observableHashMap());
        this.setSelectedEpisodesAsList(FXCollections.observableArrayList());
        this.setSelectedFeeds(FXCollections.observableArrayList());
        this.update();
        selectionModel.getSelectedItems().addListener((ListChangeListener.Change<? extends TreeItem<LibraryTreeTableValue>> change) -> this.update(change.getList().stream().map(TreeItem::getValue).collect(Collectors.toList())));
    }

    public LibrarySelection clear() {
        this.getSelectionModel().clearSelection();
        return this.update();
    }

    public LibrarySelection update() {
        return this.update(this.getSelectionModel().getSelectedItems().stream().map(TreeItem::getValue).collect(Collectors.toList()));
    }

    private LibrarySelection update(List<LibraryTreeTableValue> values) {
        this.collectSelectedFeeds(values);
        this.collectSelectedEpisodes(values);
        this.collectDownloadableFeedEpisodes(values);
        this.collectDownloadableEpisodes(values);
        this.collectCancelableEpisodes(values);
        return this;
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
        List<Episode> selectedEpisodes = values.stream()
            .filter(item -> item instanceof LibraryTreeTableValue.EpisodeTreeValue)
            .map(item -> ((LibraryTreeTableValue.EpisodeTreeValue)item).getEpisode())
            .collect(Collectors.toList());
        this.getSelectedEpisodesAsList().setAll(selectedEpisodes);
        Map<Feed, List<Episode>> episodes = new HashMap<>();
        selectedEpisodes.forEach(episode -> episodes.compute(episode.getFeed(), (k, v) -> v == null ? new ArrayList<>() : v).add(episode));
        this.getSelectedEpisodes().clear();
        this.getSelectedEpisodes().putAll(episodes);
    }

    private void collectDownloadableFeedEpisodes(List<LibraryTreeTableValue> values) {
        this.getDownloadableFeedEpisodes().setAll(
            values.stream()
                .filter(item -> item instanceof LibraryTreeTableValue.FeedTreeValue)
                .flatMap(item -> ((LibraryTreeTableValue.FeedTreeValue)item).getFeed().getEpisodes().stream())
                .filter(episode -> !List.of(EpisodeDownloadState.DOWNLOADING, EpisodeDownloadState.COMPLETED).contains(episode.getDownloadState().getValue()))
                .collect(Collectors.toList())
        );
    }

    private void collectDownloadableEpisodes(List<LibraryTreeTableValue> values) {
        this.getDownloadableEpisodes().setAll(
            values.stream()
                .filter(item -> item instanceof LibraryTreeTableValue.EpisodeTreeValue)
                .map(item -> ((LibraryTreeTableValue.EpisodeTreeValue)item).getEpisode())
                .filter(episode -> !List.of(EpisodeDownloadState.DOWNLOADING, EpisodeDownloadState.COMPLETED).contains(episode.getDownloadState().getValue()))
                .collect(Collectors.toList())
        );
    }

    private void collectCancelableEpisodes(List<LibraryTreeTableValue> values) {
        this.getCancelableEpisodes().setAll(
            values.stream()
                .filter(item -> item instanceof LibraryTreeTableValue.EpisodeTreeValue)
                .map(item -> ((LibraryTreeTableValue.EpisodeTreeValue)item).getEpisode())
                .filter(episode -> List.of(EpisodeDownloadState.SCHEDULED, EpisodeDownloadState.DOWNLOADING).contains(episode.getDownloadState().getValue()))
                .collect(Collectors.toList())
        );
    }

    TreeTableViewSelectionModel<LibraryTreeTableValue> getSelectionModel() {
        return this.selectionModel;
    }
    private void setSelectionModel(TreeTableViewSelectionModel<LibraryTreeTableValue> selectionModel) {
        this.selectionModel = selectionModel;
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

    ObservableList<Episode> getSelectedEpisodesAsList() {
        return this.selectedEpisodesAsList;
    }
    private void setSelectedEpisodesAsList(ObservableList<Episode> selectedEpisodesAsList) {
        this.selectedEpisodesAsList = selectedEpisodesAsList;
    }

    ObservableList<Episode> getCancelableEpisodes() {
        return this.cancelableEpisodes;
    }
    private void setCancelableEpisodes(ObservableList<Episode> cancelableEpisodes) {
        this.cancelableEpisodes = cancelableEpisodes;
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
