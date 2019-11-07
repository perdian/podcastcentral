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
import java.util.Set;
import java.util.stream.Collectors;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import de.perdian.apps.podcentral.jobscheduler.JobScheduler;
import de.perdian.apps.podcentral.model.Episode;
import de.perdian.apps.podcentral.model.EpisodeStorageState;
import de.perdian.apps.podcentral.model.Feed;
import de.perdian.apps.podcentral.model.Library;
import de.perdian.apps.podcentral.ui.localization.Localization;
import de.perdian.apps.podcentral.ui.modules.episodes.DownloadEpisodesActionEventHandler;
import de.perdian.apps.podcentral.ui.modules.feeds.FeedDeleteActionEventHandler;
import de.perdian.apps.podcentral.ui.modules.feeds.FeedRefreshActionEventHandler;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;

class LibraryTreeTableContextMenu extends ContextMenu {

    private LibraryTreeTableView libraryTreeTableView = null;
    private ObservableList<Feed> selectedFeeds = FXCollections.observableArrayList();
    private ObservableMap<Feed, List<Episode>> selectedEpisodes = FXCollections.observableHashMap();
    private ObservableList<Episode> downloadableFeedEpisodes = FXCollections.observableArrayList();
    private ObservableList<Episode> downloadableEpisodes = FXCollections.observableArrayList();

    public LibraryTreeTableContextMenu(LibraryTreeTableView libraryTreeTableView, JobScheduler uiJobScheduler, JobScheduler downloadJobScheduler, Library library, Localization localization) {

        MenuItem downloadSelectedEpisodesMenuItem = new MenuItem(localization.downloadSelectedEpisodes());
        downloadSelectedEpisodesMenuItem.disableProperty().bind(Bindings.isEmpty(this.getDownloadableEpisodes()));
        downloadSelectedEpisodesMenuItem.setOnAction(new DownloadEpisodesActionEventHandler(this::getDownloadableEpisodes, uiJobScheduler, downloadJobScheduler, localization));
        this.getItems().add(downloadSelectedEpisodesMenuItem);

        MenuItem downloadAllEpisodesFromFeedMenuItem = new MenuItem(localization.downloadAllEpisodesFromFeed());
        downloadAllEpisodesFromFeedMenuItem.disableProperty().bind(Bindings.isEmpty(this.getDownloadableFeedEpisodes()));
        downloadAllEpisodesFromFeedMenuItem.setOnAction(new DownloadEpisodesActionEventHandler(this::getDownloadableFeedEpisodes, uiJobScheduler, downloadJobScheduler, localization));
        this.getItems().add(downloadAllEpisodesFromFeedMenuItem);

        this.getItems().add(new SeparatorMenuItem());

        MenuItem refreshMenuItem = new MenuItem(localization.refresh(), new FontAwesomeIconView(FontAwesomeIcon.REFRESH));
        refreshMenuItem.disableProperty().bind(Bindings.isEmpty(this.getSelectedFeeds()));
        refreshMenuItem.setOnAction(new FeedRefreshActionEventHandler(this::getSelectedFeeds, Set.of(), () -> this.getLibraryTreeTableView().getSelectionModel().clearSelection(), uiJobScheduler, localization));
        this.getItems().add(refreshMenuItem);

        MenuItem refreshRestoreEpisodesMenuItem = new MenuItem(localization.refreshRestoreDeletedEpisodes(), new FontAwesomeIconView(FontAwesomeIcon.REFRESH));
        refreshRestoreEpisodesMenuItem.disableProperty().bind(Bindings.isEmpty(this.getSelectedFeeds()));
        refreshRestoreEpisodesMenuItem.setOnAction(new FeedRefreshActionEventHandler(this::getSelectedFeeds, Set.of(Feed.RefreshOption.RESTORE_DELETED_EPISODES), () -> this.getLibraryTreeTableView().getSelectionModel().clearSelection(), uiJobScheduler, localization));
        this.getItems().add(refreshRestoreEpisodesMenuItem);

        this.getItems().add(new SeparatorMenuItem());

        MenuItem deleteMenuItem = new MenuItem(localization.delete(), new FontAwesomeIconView(FontAwesomeIcon.REMOVE));
        deleteMenuItem.disableProperty().bind(Bindings.isEmpty(this.getSelectedFeeds()).and(Bindings.isEmpty(this.getSelectedEpisodes())));
        deleteMenuItem.setOnAction(new FeedDeleteActionEventHandler(this::getSelectedFeeds, this::getSelectedEpisodes, uiJobScheduler, library, localization));
        this.getItems().add(deleteMenuItem);

        this.setLibraryTreeTableView(libraryTreeTableView);

    }

    @Override
    public void show(Node anchor, double screenX, double screenY) {
        this.collectSelectedFeeds();
        this.collectSelectedEpisodes();
        this.collectDownloadableFeedEpisodes();
        this.collectDownloadableEpisodes();
        super.show(anchor, screenX, screenY);
    }

    private void collectSelectedFeeds() {
        this.getSelectedFeeds().setAll(this.getLibraryTreeTableView().getSelectionModel().getSelectedItems().stream().filter(item -> item.getValue() instanceof LibraryTreeTableValue.FeedTreeValue).map(item -> ((LibraryTreeTableValue.FeedTreeValue)item.getValue()).getFeed()).collect(Collectors.toList()));
    }

    private void collectSelectedEpisodes() {
        Map<Feed, List<Episode>> episodes = new HashMap<>();
        this.getLibraryTreeTableView().getSelectionModel().getSelectedItems().stream()
            .filter(item -> item.getValue() instanceof LibraryTreeTableValue.EpisodeTreeValue)
            .map(item -> (LibraryTreeTableValue.EpisodeTreeValue)item.getValue())
            .forEach(item -> episodes.compute(item.getFeed(), (k, v) -> v == null ? new ArrayList<>() : v).add(item.getEpisode()));
        this.getSelectedEpisodes().clear();
        this.getSelectedEpisodes().putAll(episodes);
    }

    private void collectDownloadableFeedEpisodes() {
        this.getDownloadableFeedEpisodes().setAll(
            this.getSelectedFeeds().stream()
                .flatMap(feed -> feed.getEpisodes().stream())
                .filter(episode -> !EpisodeStorageState.DOWNLOAD_COMPLETED.equals(episode.getStorageState()))
                .collect(Collectors.toSet())
        );
    }

    private void collectDownloadableEpisodes() {
        this.getDownloadableEpisodes().setAll(
            this.getSelectedEpisodes().values().stream()
                .flatMap(List::stream)
                .filter(episode -> !EpisodeStorageState.DOWNLOAD_COMPLETED.equals(episode.getStorageState()))
                .collect(Collectors.toSet())
        );
    }

    private LibraryTreeTableView getLibraryTreeTableView() {
        return this.libraryTreeTableView;
    }
    private void setLibraryTreeTableView(LibraryTreeTableView libraryTreeTableView) {
        this.libraryTreeTableView = libraryTreeTableView;
    }

    ObservableList<Feed> getSelectedFeeds() {
        return this.selectedFeeds;
    }
    void setSelectedFeeds(ObservableList<Feed> selectedFeeds) {
        this.selectedFeeds = selectedFeeds;
    }

    ObservableMap<Feed, List<Episode>> getSelectedEpisodes() {
        return this.selectedEpisodes;
    }
    void setSelectedEpisodes(ObservableMap<Feed, List<Episode>> selectedEpisodes) {
        this.selectedEpisodes = selectedEpisodes;
    }

    ObservableList<Episode> getDownloadableFeedEpisodes() {
        return this.downloadableFeedEpisodes;
    }
    void setDownloadableFeedEpisodes(ObservableList<Episode> downloadableFeedEpisodes) {
        this.downloadableFeedEpisodes = downloadableFeedEpisodes;
    }

    ObservableList<Episode> getDownloadableEpisodes() {
        return this.downloadableEpisodes;
    }
    void setDownloadableEpisodes(ObservableList<Episode> downloadableEpisodes) {
        this.downloadableEpisodes = downloadableEpisodes;
    }

}
