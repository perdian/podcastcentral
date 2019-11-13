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

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import de.perdian.apps.podcentral.downloader.episodes.EpisodeDownloader;
import de.perdian.apps.podcentral.model.Feed;
import de.perdian.apps.podcentral.model.Library;
import de.perdian.apps.podcentral.ui.modules.library.actions.CancelDownloadEpisodesActionEventHandler;
import de.perdian.apps.podcentral.ui.modules.library.actions.DeleteFeedActionEventHandler;
import de.perdian.apps.podcentral.ui.modules.library.actions.MarkReadEpisodesActionEventHandler;
import de.perdian.apps.podcentral.ui.modules.library.actions.OpenEpisodeActionEventHandler;
import de.perdian.apps.podcentral.ui.modules.library.actions.RefreshFeedsActionEventHandler;
import de.perdian.apps.podcentral.ui.modules.library.actions.StartDownloadEpisodesActionEventHandler;
import de.perdian.apps.podcentral.ui.support.backgroundtasks.BackgroundTaskExecutor;
import de.perdian.apps.podcentral.ui.support.localization.Localization;
import javafx.beans.binding.Bindings;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;

class LibraryTreeTableContextMenu extends ContextMenu {

    private LibrarySelection librarySelection = null;

    public LibraryTreeTableContextMenu(LibrarySelection librarySelection, BackgroundTaskExecutor backgroundTaskExecutor, EpisodeDownloader episodeDownloader, Library library, Localization localization) {
        this.setLibrarySelection(librarySelection);

        MenuItem markReadMenuItem = new MenuItem(localization.markAsRead());
        markReadMenuItem.disableProperty().bind(Bindings.isEmpty(librarySelection.getSelectedFeeds()).and(Bindings.isEmpty(librarySelection.getSelectedEpisodesForMarkRead())));
        markReadMenuItem.setOnAction(new MarkReadEpisodesActionEventHandler(librarySelection::getSelectedFeeds, librarySelection::getSelectedEpisodesForMarkRead, Boolean.TRUE));
        this.getItems().add(markReadMenuItem);

        MenuItem markUnreadReadMenuItem = new MenuItem(localization.markAsUnread());
        markUnreadReadMenuItem.disableProperty().bind(Bindings.isEmpty(librarySelection.getSelectedFeeds()).and(Bindings.isEmpty(librarySelection.getSelectedEpisodesForMarkUnread())));
        markUnreadReadMenuItem.setOnAction(new MarkReadEpisodesActionEventHandler(librarySelection::getSelectedFeeds, librarySelection::getSelectedEpisodesForMarkUnread, Boolean.FALSE));
        this.getItems().add(markUnreadReadMenuItem);

        this.getItems().add(new SeparatorMenuItem());

        MenuItem openEpisodeMenuItem = new MenuItem(localization.openEpisode());
        openEpisodeMenuItem.disableProperty().bind(Bindings.size(librarySelection.getSelectedEpisodesForOpen()).isNotEqualTo(1));
        openEpisodeMenuItem.setOnAction(new OpenEpisodeActionEventHandler(librarySelection::getSelectedEpisodesForOpen));
        this.getItems().add(openEpisodeMenuItem);

        this.getItems().add(new SeparatorMenuItem());

        MenuItem startDownloadSelectedEpisodesMenuItem = new MenuItem(localization.downloadSelectedEpisodes());
        startDownloadSelectedEpisodesMenuItem.disableProperty().bind(Bindings.isEmpty(librarySelection.getSelectedEpisodesForDownload()));
        startDownloadSelectedEpisodesMenuItem.setOnAction(new StartDownloadEpisodesActionEventHandler(librarySelection::getSelectedEpisodesForDownload, episodeDownloader, backgroundTaskExecutor, localization));
        this.getItems().add(startDownloadSelectedEpisodesMenuItem);

        MenuItem startDownloadSelectedEpisodesRedownloadExistingMenuItem = new MenuItem(localization.downloadSelectedEpisodesRedownloadExistingEpisodes());
        startDownloadSelectedEpisodesRedownloadExistingMenuItem.disableProperty().bind(Bindings.isEmpty(librarySelection.getSelectedEpisodes()));
        startDownloadSelectedEpisodesRedownloadExistingMenuItem.setOnAction(new StartDownloadEpisodesActionEventHandler(librarySelection::getSelectedEpisodes, episodeDownloader, backgroundTaskExecutor, localization));
        this.getItems().add(startDownloadSelectedEpisodesRedownloadExistingMenuItem);

        MenuItem startDownloadAllEpisodesFromFeedMenuItem = new MenuItem(localization.downloadAllEpisodesFromFeed());
        startDownloadAllEpisodesFromFeedMenuItem.disableProperty().bind(Bindings.isEmpty(librarySelection.getSelectedEpisodesFromFeedsForDownload()));
        startDownloadAllEpisodesFromFeedMenuItem.setOnAction(new StartDownloadEpisodesActionEventHandler(librarySelection::getSelectedEpisodesFromFeedsForDownload, episodeDownloader, backgroundTaskExecutor, localization));
        this.getItems().add(startDownloadAllEpisodesFromFeedMenuItem);

        this.getItems().add(new SeparatorMenuItem());

        MenuItem cancelDownloadSelectedEpisodesMenuItem = new MenuItem(localization.cancelDownloads());
        cancelDownloadSelectedEpisodesMenuItem.disableProperty().bind(Bindings.isEmpty(librarySelection.getSelectedEpisodesForCancel()));
        cancelDownloadSelectedEpisodesMenuItem.setOnAction(new CancelDownloadEpisodesActionEventHandler(librarySelection::getSelectedEpisodesForCancel, backgroundTaskExecutor, episodeDownloader, localization));
        this.getItems().add(cancelDownloadSelectedEpisodesMenuItem);

        this.getItems().add(new SeparatorMenuItem());

        MenuItem refreshMenuItem = new MenuItem(localization.refresh(), new FontAwesomeIconView(FontAwesomeIcon.REFRESH));
        refreshMenuItem.disableProperty().bind(Bindings.isEmpty(librarySelection.getSelectedFeeds()));
        refreshMenuItem.setOnAction(new RefreshFeedsActionEventHandler(librarySelection::getSelectedFeeds, Set.of(), backgroundTaskExecutor, localization));
        this.getItems().add(refreshMenuItem);

        MenuItem refreshRestoreEpisodesMenuItem = new MenuItem(localization.refreshRestoreDeletedEpisodes(), new FontAwesomeIconView(FontAwesomeIcon.REFRESH));
        refreshRestoreEpisodesMenuItem.disableProperty().bind(Bindings.isEmpty(librarySelection.getSelectedFeeds()));
        refreshRestoreEpisodesMenuItem.setOnAction(new RefreshFeedsActionEventHandler(librarySelection::getSelectedFeeds, Set.of(Feed.RefreshOption.RESTORE_DELETED_EPISODES), backgroundTaskExecutor, localization));
        this.getItems().add(refreshRestoreEpisodesMenuItem);

        this.getItems().add(new SeparatorMenuItem());

        MenuItem deleteMenuItem = new MenuItem(localization.delete(), new FontAwesomeIconView(FontAwesomeIcon.REMOVE));
        deleteMenuItem.disableProperty().bind(Bindings.isEmpty(librarySelection.getSelectedFeedsForDelete()).and(Bindings.isEmpty(librarySelection.getSelectedEpisodesForDelete())));
        deleteMenuItem.setOnAction(new DeleteFeedActionEventHandler(librarySelection::getSelectedFeedsForDelete, librarySelection::getSelectedEpisodesForDelete, library, backgroundTaskExecutor, localization));
        this.getItems().add(deleteMenuItem);

    }

    @Override
    public void show(Node anchor, double screenX, double screenY) {
        this.getLibrarySelection().update();
        super.show(anchor, screenX, screenY);
    }

    private LibrarySelection getLibrarySelection() {
        return this.librarySelection;
    }
    private void setLibrarySelection(LibrarySelection librarySelection) {
        this.librarySelection = librarySelection;
    }

}
