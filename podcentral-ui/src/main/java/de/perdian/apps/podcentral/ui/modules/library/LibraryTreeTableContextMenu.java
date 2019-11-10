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
import de.perdian.apps.podcentral.ui.modules.episodes.CancelDownloadEpisodesActionEventHandler;
import de.perdian.apps.podcentral.ui.modules.episodes.OpenEpisodeActionEventHandler;
import de.perdian.apps.podcentral.ui.modules.episodes.StartDownloadEpisodesActionEventHandler;
import de.perdian.apps.podcentral.ui.modules.feeds.DeleteActionEventHandler;
import de.perdian.apps.podcentral.ui.modules.feeds.RefreshFeedsActionEventHandler;
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

        MenuItem openEpisodeMenuItem = new MenuItem(localization.openEpisode());
        openEpisodeMenuItem.disableProperty().bind(Bindings.size(librarySelection.getSelectedEpisodesAsList()).isNotEqualTo(1));
        openEpisodeMenuItem.setOnAction(new OpenEpisodeActionEventHandler(librarySelection::getSelectedEpisodesAsList));
        this.getItems().add(openEpisodeMenuItem);

        this.getItems().add(new SeparatorMenuItem());

        MenuItem startDownloadSelectedEpisodesMenuItem = new MenuItem(localization.downloadSelectedEpisodes());
        startDownloadSelectedEpisodesMenuItem.disableProperty().bind(Bindings.isEmpty(librarySelection.getDownloadableEpisodes()));
        startDownloadSelectedEpisodesMenuItem.setOnAction(new StartDownloadEpisodesActionEventHandler(librarySelection::getDownloadableEpisodes, backgroundTaskExecutor, episodeDownloader, localization));
        this.getItems().add(startDownloadSelectedEpisodesMenuItem);

        MenuItem startDownloadSelectedEpisodesRedownloadExistingMenuItem = new MenuItem(localization.downloadSelectedEpisodesRedownloadExistingEpisodes());
        startDownloadSelectedEpisodesRedownloadExistingMenuItem.disableProperty().bind(Bindings.isEmpty(librarySelection.getSelectedEpisodes()));
        startDownloadSelectedEpisodesRedownloadExistingMenuItem.setOnAction(new StartDownloadEpisodesActionEventHandler(librarySelection::getSelectedEpisodesAsList, backgroundTaskExecutor, episodeDownloader, localization));
        this.getItems().add(startDownloadSelectedEpisodesRedownloadExistingMenuItem);

        MenuItem startDownloadAllEpisodesFromFeedMenuItem = new MenuItem(localization.downloadAllEpisodesFromFeed());
        startDownloadAllEpisodesFromFeedMenuItem.disableProperty().bind(Bindings.isEmpty(librarySelection.getDownloadableFeedEpisodes()));
        startDownloadAllEpisodesFromFeedMenuItem.setOnAction(new StartDownloadEpisodesActionEventHandler(librarySelection::getDownloadableFeedEpisodes, backgroundTaskExecutor, episodeDownloader, localization));
        this.getItems().add(startDownloadAllEpisodesFromFeedMenuItem);

        this.getItems().add(new SeparatorMenuItem());

        MenuItem cancelDownloadSelectedEpisodesMenuItem = new MenuItem(localization.cancelDownloads());
        cancelDownloadSelectedEpisodesMenuItem.disableProperty().bind(Bindings.isEmpty(librarySelection.getCancelableEpisodes()));
        cancelDownloadSelectedEpisodesMenuItem.setOnAction(new CancelDownloadEpisodesActionEventHandler(librarySelection::getCancelableEpisodes, backgroundTaskExecutor, episodeDownloader, localization));
        this.getItems().add(cancelDownloadSelectedEpisodesMenuItem);

        this.getItems().add(new SeparatorMenuItem());

        MenuItem refreshMenuItem = new MenuItem(localization.refresh(), new FontAwesomeIconView(FontAwesomeIcon.REFRESH));
        refreshMenuItem.disableProperty().bind(Bindings.isEmpty(librarySelection.getSelectedFeeds()));
        refreshMenuItem.setOnAction(new RefreshFeedsActionEventHandler(librarySelection::getSelectedFeeds, Set.of(), () -> librarySelection.getSelectionModel().clearSelection(), backgroundTaskExecutor, localization));
        this.getItems().add(refreshMenuItem);

        MenuItem refreshRestoreEpisodesMenuItem = new MenuItem(localization.refreshRestoreDeletedEpisodes(), new FontAwesomeIconView(FontAwesomeIcon.REFRESH));
        refreshRestoreEpisodesMenuItem.disableProperty().bind(Bindings.isEmpty(librarySelection.getSelectedFeeds()));
        refreshRestoreEpisodesMenuItem.setOnAction(new RefreshFeedsActionEventHandler(librarySelection::getSelectedFeeds, Set.of(Feed.RefreshOption.RESTORE_DELETED_EPISODES), () -> librarySelection.getSelectionModel().clearSelection(), backgroundTaskExecutor, localization));
        this.getItems().add(refreshRestoreEpisodesMenuItem);

        this.getItems().add(new SeparatorMenuItem());

        MenuItem deleteMenuItem = new MenuItem(localization.delete(), new FontAwesomeIconView(FontAwesomeIcon.REMOVE));
        deleteMenuItem.disableProperty().bind(Bindings.isEmpty(librarySelection.getSelectedFeeds()).and(Bindings.isEmpty(librarySelection.getSelectedEpisodes())));
        deleteMenuItem.setOnAction(new DeleteActionEventHandler(librarySelection::getSelectedFeeds, librarySelection::getSelectedEpisodesAsList, backgroundTaskExecutor, library, localization));
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
