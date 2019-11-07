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
import de.perdian.apps.podcentral.jobscheduler.JobScheduler;
import de.perdian.apps.podcentral.model.Feed;
import de.perdian.apps.podcentral.model.Library;
import de.perdian.apps.podcentral.ui.localization.Localization;
import de.perdian.apps.podcentral.ui.modules.episodes.DownloadEpisodesActionEventHandler;
import de.perdian.apps.podcentral.ui.modules.feeds.FeedDeleteActionEventHandler;
import de.perdian.apps.podcentral.ui.modules.feeds.FeedRefreshActionEventHandler;
import javafx.beans.binding.Bindings;
import javafx.collections.ListChangeListener;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TreeItem;

class LibraryTreeTableContextMenu extends ContextMenu {

    private LibraryTreeTableView libraryTreeTableView = null;

    public LibraryTreeTableContextMenu(LibraryTreeTableView libraryTreeTableView, JobScheduler uiJobScheduler, JobScheduler downloadJobScheduler, Library library, Localization localization) {

        LibrarySelection librarySelection = new LibrarySelection();
        libraryTreeTableView.getSelectionModel().getSelectedItems().addListener((ListChangeListener.Change<? extends TreeItem<LibraryTreeTableValue>> change) -> librarySelection.update(change.getList().stream().map(TreeItem::getValue).collect(Collectors.toList())));

        MenuItem downloadSelectedEpisodesMenuItem = new MenuItem(localization.downloadSelectedEpisodes());
        downloadSelectedEpisodesMenuItem.disableProperty().bind(Bindings.isEmpty(librarySelection.getDownloadableEpisodes()));
        downloadSelectedEpisodesMenuItem.setOnAction(new DownloadEpisodesActionEventHandler(librarySelection::getDownloadableEpisodes, uiJobScheduler, downloadJobScheduler, localization));
        this.getItems().add(downloadSelectedEpisodesMenuItem);

        MenuItem downloadAllEpisodesFromFeedMenuItem = new MenuItem(localization.downloadAllEpisodesFromFeed());
        downloadAllEpisodesFromFeedMenuItem.disableProperty().bind(Bindings.isEmpty(librarySelection.getDownloadableFeedEpisodes()));
        downloadAllEpisodesFromFeedMenuItem.setOnAction(new DownloadEpisodesActionEventHandler(librarySelection::getDownloadableFeedEpisodes, uiJobScheduler, downloadJobScheduler, localization));
        this.getItems().add(downloadAllEpisodesFromFeedMenuItem);

        this.getItems().add(new SeparatorMenuItem());

        MenuItem refreshMenuItem = new MenuItem(localization.refresh(), new FontAwesomeIconView(FontAwesomeIcon.REFRESH));
        refreshMenuItem.disableProperty().bind(Bindings.isEmpty(librarySelection.getSelectedFeeds()));
        refreshMenuItem.setOnAction(new FeedRefreshActionEventHandler(librarySelection::getSelectedFeeds, Set.of(), () -> this.getLibraryTreeTableView().getSelectionModel().clearSelection(), uiJobScheduler, localization));
        this.getItems().add(refreshMenuItem);

        MenuItem refreshRestoreEpisodesMenuItem = new MenuItem(localization.refreshRestoreDeletedEpisodes(), new FontAwesomeIconView(FontAwesomeIcon.REFRESH));
        refreshRestoreEpisodesMenuItem.disableProperty().bind(Bindings.isEmpty(librarySelection.getSelectedFeeds()));
        refreshRestoreEpisodesMenuItem.setOnAction(new FeedRefreshActionEventHandler(librarySelection::getSelectedFeeds, Set.of(Feed.RefreshOption.RESTORE_DELETED_EPISODES), () -> this.getLibraryTreeTableView().getSelectionModel().clearSelection(), uiJobScheduler, localization));
        this.getItems().add(refreshRestoreEpisodesMenuItem);

        this.getItems().add(new SeparatorMenuItem());

        MenuItem deleteMenuItem = new MenuItem(localization.delete(), new FontAwesomeIconView(FontAwesomeIcon.REMOVE));
        deleteMenuItem.disableProperty().bind(Bindings.isEmpty(librarySelection.getSelectedFeeds()).and(Bindings.isEmpty(librarySelection.getSelectedEpisodes())));
        deleteMenuItem.setOnAction(new FeedDeleteActionEventHandler(librarySelection::getSelectedFeeds, librarySelection::getSelectedEpisodes, uiJobScheduler, library, localization));
        this.getItems().add(deleteMenuItem);

        this.setLibraryTreeTableView(libraryTreeTableView);

    }

    private LibraryTreeTableView getLibraryTreeTableView() {
        return this.libraryTreeTableView;
    }
    private void setLibraryTreeTableView(LibraryTreeTableView libraryTreeTableView) {
        this.libraryTreeTableView = libraryTreeTableView;
    }

}
