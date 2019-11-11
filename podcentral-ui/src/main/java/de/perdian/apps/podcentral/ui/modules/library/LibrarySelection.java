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

import java.util.List;
import java.util.stream.Collectors;

import de.perdian.apps.podcentral.model.Episode;
import de.perdian.apps.podcentral.model.EpisodeDownloadState;
import de.perdian.apps.podcentral.model.Feed;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableView.TreeTableViewSelectionModel;

class LibrarySelection {

    private TreeTableViewSelectionModel<LibraryTreeTableValue> selectionModel = null;
    private ObservableList<Feed> selectedFeeds = null;
    private ObservableList<Feed> selectedFeedsForDelete = null;
    private ObservableList<Episode> selectedEpisodes = null;
    private ObservableList<Episode> selectedEpisodesForOpen = null;
    private ObservableList<Episode> selectedEpisodesForDownload = null;
    private ObservableList<Episode> selectedEpisodesForDelete = null;
    private ObservableList<Episode> selectedEpisodesForCancel = null;
    private ObservableList<Episode> selectedEpisodesForMarkRead = null;
    private ObservableList<Episode> selectedEpisodesForMarkUnread = null;
    private ObservableList<Episode> selectedEpisodesFromFeeds = null;
    private ObservableList<Episode> selectedEpisodesFromFeedsForDownload = null;

    LibrarySelection(TreeTableViewSelectionModel<LibraryTreeTableValue> selectionModel) {

        ObservableList<Feed> selectedFeeds = FXCollections.observableArrayList();
        ObservableList<Episode> selectedEpisodes = FXCollections.observableArrayList();
        ObservableList<Episode> selectedEpisodesFromFeeds = FXCollections.observableArrayList();

        this.setSelectedFeeds(selectedFeeds);
        this.setSelectedFeedsForDelete(selectedFeeds.filtered(feed -> feed.getEpisodes().stream().filter(episode -> List.of(EpisodeDownloadState.SCHEDULED, EpisodeDownloadState.CANCELLED).contains(episode.getDownloadState().getValue())).findAny().isEmpty()));
        this.setSelectedEpisodes(selectedEpisodes);
        this.setSelectedEpisodesForDelete(selectedEpisodes.filtered(episode -> !List.of(EpisodeDownloadState.SCHEDULED, EpisodeDownloadState.DOWNLOADING).contains(episode.getDownloadState().getValue())));
        this.setSelectedEpisodesForDownload(selectedEpisodes.filtered(episode -> !List.of(EpisodeDownloadState.COMPLETED, EpisodeDownloadState.SCHEDULED, EpisodeDownloadState.DOWNLOADING).contains(episode.getDownloadState().getValue())));
        this.setSelectedEpisodesForOpen(selectedEpisodes.filtered(episode -> List.of(EpisodeDownloadState.COMPLETED).contains(episode.getDownloadState().getValue())));
        this.setSelectedEpisodesForCancel(selectedEpisodes.filtered(episode -> List.of(EpisodeDownloadState.SCHEDULED, EpisodeDownloadState.CANCELLED).contains(episode.getDownloadState().getValue())));
        this.setSelectedEpisodesForMarkRead(selectedEpisodes.filtered(episode -> !Boolean.TRUE.equals(episode.getRead().getValue())));
        this.setSelectedEpisodesForMarkUnread(selectedEpisodes.filtered(episode -> Boolean.TRUE.equals(episode.getRead().getValue())));
        this.setSelectedEpisodesFromFeeds(selectedEpisodesFromFeeds);
        this.setSelectedEpisodesFromFeedsForDownload(selectedEpisodesFromFeeds.filtered(episode -> !List.of(EpisodeDownloadState.COMPLETED, EpisodeDownloadState.SCHEDULED, EpisodeDownloadState.DOWNLOADING).contains(episode.getDownloadState().getValue())));
        this.setSelectionModel(selectionModel);
        this.update();
        selectionModel.getSelectedItems().addListener((ListChangeListener.Change<? extends TreeItem<LibraryTreeTableValue>> change) -> this.update(change.getList().stream().map(TreeItem::getValue).collect(Collectors.toList())));

    }

    public LibrarySelection update() {
        return this.update(this.getSelectionModel().getSelectedItems().stream().map(TreeItem::getValue).collect(Collectors.toList()));
    }

    private LibrarySelection update(List<LibraryTreeTableValue> values) {
        this.collectSelectedFeeds(values);
        this.collectSelectedEpisodes(values);
        this.collectSelectedEpisodesFromFeeds();
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
        this.getSelectedEpisodes().setAll(
            values.stream()
                .filter(item -> item instanceof LibraryTreeTableValue.EpisodeTreeValue)
                .map(item -> ((LibraryTreeTableValue.EpisodeTreeValue)item).getEpisode())
                .collect(Collectors.toList())
        );
    }

    private void collectSelectedEpisodesFromFeeds() {
        this.getSelectedEpisodesFromFeeds().setAll(
            this.getSelectedFeeds().stream()
                .flatMap(feed -> feed.getEpisodes().stream())
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

    ObservableList<Feed> getSelectedFeedsForDelete() {
        return this.selectedFeedsForDelete;
    }
    private void setSelectedFeedsForDelete(ObservableList<Feed> selectedFeedsForDelete) {
        this.selectedFeedsForDelete = selectedFeedsForDelete;
    }

    ObservableList<Episode> getSelectedEpisodes() {
        return this.selectedEpisodes;
    }
    private void setSelectedEpisodes(ObservableList<Episode> selectedEpisodes) {
        this.selectedEpisodes = selectedEpisodes;
    }

    ObservableList<Episode> getSelectedEpisodesForOpen() {
        return this.selectedEpisodesForOpen;
    }
    private void setSelectedEpisodesForOpen(ObservableList<Episode> selectedEpisodesForOpen) {
        this.selectedEpisodesForOpen = selectedEpisodesForOpen;
    }

    ObservableList<Episode> getSelectedEpisodesForDownload() {
        return this.selectedEpisodesForDownload;
    }
    private void setSelectedEpisodesForDownload(ObservableList<Episode> selectedEpisodesForDownload) {
        this.selectedEpisodesForDownload = selectedEpisodesForDownload;
    }

    ObservableList<Episode> getSelectedEpisodesForDelete() {
        return this.selectedEpisodesForDelete;
    }
    private void setSelectedEpisodesForDelete(ObservableList<Episode> selectedEpisodesForDelete) {
        this.selectedEpisodesForDelete = selectedEpisodesForDelete;
    }

    ObservableList<Episode> getSelectedEpisodesForCancel() {
        return this.selectedEpisodesForCancel;
    }
    private void setSelectedEpisodesForCancel(ObservableList<Episode> selectedEpisodesForCancel) {
        this.selectedEpisodesForCancel = selectedEpisodesForCancel;
    }

    ObservableList<Episode> getSelectedEpisodesForMarkRead() {
        return this.selectedEpisodesForMarkRead;
    }
    private void setSelectedEpisodesForMarkRead(ObservableList<Episode> selectedEpisodesForMarkRead) {
        this.selectedEpisodesForMarkRead = selectedEpisodesForMarkRead;
    }

    ObservableList<Episode> getSelectedEpisodesForMarkUnread() {
        return this.selectedEpisodesForMarkUnread;
    }
    private void setSelectedEpisodesForMarkUnread(ObservableList<Episode> selectedEpisodesForMarkUnread) {
        this.selectedEpisodesForMarkUnread = selectedEpisodesForMarkUnread;
    }

    ObservableList<Episode> getSelectedEpisodesFromFeeds() {
        return this.selectedEpisodesFromFeeds;
    }
    private void setSelectedEpisodesFromFeeds(ObservableList<Episode> selectedEpisodesFromFeeds) {
        this.selectedEpisodesFromFeeds = selectedEpisodesFromFeeds;
    }

    ObservableList<Episode> getSelectedEpisodesFromFeedsForDownload() {
        return this.selectedEpisodesFromFeedsForDownload;
    }
    private void setSelectedEpisodesFromFeedsForDownload(ObservableList<Episode> selectedEpisodesFromFeedsForDownload) {
        this.selectedEpisodesFromFeedsForDownload = selectedEpisodesFromFeedsForDownload;
    }

}
