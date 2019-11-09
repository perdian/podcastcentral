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

import java.util.Set;
import java.util.stream.Collectors;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import de.perdian.apps.podcentral.downloader.episodes.EpisodeContentDownloader;
import de.perdian.apps.podcentral.model.Feed;
import de.perdian.apps.podcentral.model.Library;
import de.perdian.apps.podcentral.ui.localization.Localization;
import de.perdian.apps.podcentral.ui.modules.episodes.CancelDownloadEpisodesActionEventHandler;
import de.perdian.apps.podcentral.ui.modules.episodes.StartDownloadEpisodesActionEventHandler;
import de.perdian.apps.podcentral.ui.modules.feeds.DeleteFeedsActionEventHandler;
import de.perdian.apps.podcentral.ui.modules.feeds.RefreshFeedsActionEventHandler;
import de.perdian.apps.podcentral.ui.support.tasks.BackgroundTaskExecutor;
import javafx.beans.binding.Bindings;
import javafx.collections.ListChangeListener;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TreeItem;

class LibraryTreeTableContextMenu extends ContextMenu {

    private LibraryTreeTableView libraryTreeTableView = null;
    private LibrarySelection librarySelection = null;

    public LibraryTreeTableContextMenu(LibraryTreeTableView libraryTreeTableView, BackgroundTaskExecutor backgroundTaskExecutor, EpisodeContentDownloader episodeContentDownloader, Library library, Localization localization) {

        LibrarySelection librarySelection = new LibrarySelection();
        libraryTreeTableView.getSelectionModel().getSelectedItems().addListener((ListChangeListener.Change<? extends TreeItem<LibraryTreeTableValue>> change) -> librarySelection.update(change.getList().stream().map(TreeItem::getValue).collect(Collectors.toList())));
        this.setLibrarySelection(librarySelection);

        MenuItem startDownloadSelectedEpisodesMenuItem = new MenuItem(localization.downloadSelectedEpisodes());
        startDownloadSelectedEpisodesMenuItem.disableProperty().bind(Bindings.isEmpty(librarySelection.getDownloadableEpisodes()));
        startDownloadSelectedEpisodesMenuItem.setOnAction(new StartDownloadEpisodesActionEventHandler(librarySelection::getDownloadableEpisodes, backgroundTaskExecutor, episodeContentDownloader, localization));
        this.getItems().add(startDownloadSelectedEpisodesMenuItem);

        MenuItem startDownloadAllEpisodesFromFeedMenuItem = new MenuItem(localization.downloadAllEpisodesFromFeed());
        startDownloadAllEpisodesFromFeedMenuItem.disableProperty().bind(Bindings.isEmpty(librarySelection.getDownloadableFeedEpisodes()));
        startDownloadAllEpisodesFromFeedMenuItem.setOnAction(new StartDownloadEpisodesActionEventHandler(librarySelection::getDownloadableFeedEpisodes, backgroundTaskExecutor, episodeContentDownloader, localization));
        this.getItems().add(startDownloadAllEpisodesFromFeedMenuItem);

        this.getItems().add(new SeparatorMenuItem());

        MenuItem cancelDownloadSelectedEpisodesMenuItem = new MenuItem(localization.cancelDownloads());
        cancelDownloadSelectedEpisodesMenuItem.disableProperty().bind(Bindings.isEmpty(librarySelection.getCancelableEpisodes()));
        cancelDownloadSelectedEpisodesMenuItem.setOnAction(new CancelDownloadEpisodesActionEventHandler(librarySelection::getCancelableEpisodes, backgroundTaskExecutor, episodeContentDownloader, localization));
        this.getItems().add(cancelDownloadSelectedEpisodesMenuItem);

        this.getItems().add(new SeparatorMenuItem());

        MenuItem refreshMenuItem = new MenuItem(localization.refresh(), new FontAwesomeIconView(FontAwesomeIcon.REFRESH));
        refreshMenuItem.disableProperty().bind(Bindings.isEmpty(librarySelection.getSelectedFeeds()));
        refreshMenuItem.setOnAction(new RefreshFeedsActionEventHandler(librarySelection::getSelectedFeeds, Set.of(), () -> this.getLibraryTreeTableView().getSelectionModel().clearSelection(), backgroundTaskExecutor, localization));
        this.getItems().add(refreshMenuItem);

        MenuItem refreshRestoreEpisodesMenuItem = new MenuItem(localization.refreshRestoreDeletedEpisodes(), new FontAwesomeIconView(FontAwesomeIcon.REFRESH));
        refreshRestoreEpisodesMenuItem.disableProperty().bind(Bindings.isEmpty(librarySelection.getSelectedFeeds()));
        refreshRestoreEpisodesMenuItem.setOnAction(new RefreshFeedsActionEventHandler(librarySelection::getSelectedFeeds, Set.of(Feed.RefreshOption.RESTORE_DELETED_EPISODES), () -> this.getLibraryTreeTableView().getSelectionModel().clearSelection(), backgroundTaskExecutor, localization));
        this.getItems().add(refreshRestoreEpisodesMenuItem);

        this.getItems().add(new SeparatorMenuItem());

        MenuItem deleteMenuItem = new MenuItem(localization.delete(), new FontAwesomeIconView(FontAwesomeIcon.REMOVE));
        deleteMenuItem.disableProperty().bind(Bindings.isEmpty(librarySelection.getSelectedFeeds()).and(Bindings.isEmpty(librarySelection.getSelectedEpisodes())));
        deleteMenuItem.setOnAction(new DeleteFeedsActionEventHandler(librarySelection::getSelectedFeeds, librarySelection::getSelectedEpisodes, backgroundTaskExecutor, library, localization));
        this.getItems().add(deleteMenuItem);

        this.setLibraryTreeTableView(libraryTreeTableView);

    }

    @Override
    public void show(Node anchor, double screenX, double screenY) {
        this.getLibrarySelection().update(this.getLibraryTreeTableView().getSelectionModel().getSelectedItems().stream().map(TreeItem::getValue).collect(Collectors.toList()));
        super.show(anchor, screenX, screenY);
    }

    private LibraryTreeTableView getLibraryTreeTableView() {
        return this.libraryTreeTableView;
    }
    private void setLibraryTreeTableView(LibraryTreeTableView libraryTreeTableView) {
        this.libraryTreeTableView = libraryTreeTableView;
    }

    private LibrarySelection getLibrarySelection() {
        return this.librarySelection;
    }
    private void setLibrarySelection(LibrarySelection librarySelection) {
        this.librarySelection = librarySelection;
    }

}
