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

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import de.perdian.apps.podcastcentral.downloader.episodes.EpisodeDownloader;
import de.perdian.apps.podcastcentral.model.Episode;
import de.perdian.apps.podcastcentral.model.EpisodeDownloadState;
import de.perdian.apps.podcastcentral.model.Feed;
import de.perdian.apps.podcastcentral.model.Library;
import de.perdian.apps.podcastcentral.ui.modules.library.LibrarySelection;
import de.perdian.apps.podcastcentral.ui.modules.library.actions.CancelEpisodeDownloadsActionEventHandler;
import de.perdian.apps.podcastcentral.ui.modules.library.actions.ChangeEpisodeReadStateActionEventHandler;
import de.perdian.apps.podcastcentral.ui.modules.library.actions.DeleteFeedsOrEpisodesActionEventHandler;
import de.perdian.apps.podcastcentral.ui.modules.library.actions.DownloadEpisodesActionEventHandler;
import de.perdian.apps.podcastcentral.ui.modules.library.actions.RefreshFeedsActionEventHandler;
import de.perdian.apps.podcastcentral.ui.support.backgroundtasks.BackgroundTaskExecutor;
import de.perdian.apps.podcastcentral.ui.support.localization.Localization;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;

class LibraryTreeTableContextMenu extends ContextMenu {

    private Supplier<LibrarySelection> selectionSupplier = null;
    private final ObservableList<Feed> selectedFeeds = FXCollections.observableArrayList();
    private final ObservableList<Feed> selectedFeedsDeletable = FXCollections.observableArrayList();
    private final ObservableList<Episode> selectedEpisodesConsolidated = FXCollections.observableArrayList();
    private final ObservableList<Episode> selectedEpisodesConsolidatedRead = FXCollections.observableArrayList();
    private final ObservableList<Episode> selectedEpisodesConsolidatedUnread = FXCollections.observableArrayList();
    private final ObservableList<Episode> selectedEpisodesConsolidatedNotDownloaded = FXCollections.observableArrayList();
    private final ObservableList<Episode> selectedEpisodesConsolidatedDownloading = FXCollections.observableArrayList();
    private final ObservableList<Episode> selectedEpisodesConsolidatedDeletable = FXCollections.observableArrayList();

    LibraryTreeTableContextMenu(Supplier<LibrarySelection> selectionSupplier, Library library, EpisodeDownloader episodeDownloader, BackgroundTaskExecutor backgroundTaskExecutor, Localization localization) {
        this.setSelectionSupplier(selectionSupplier);

        MenuItem refreshFeedsMenuItem = new MenuItem(localization.refresh(), new FontAwesomeIconView(FontAwesomeIcon.REFRESH));
        refreshFeedsMenuItem.disableProperty().bind(Bindings.isEmpty(this.getSelectedFeeds()));
        refreshFeedsMenuItem.setOnAction(new RefreshFeedsActionEventHandler(this::getSelectedFeeds, Collections.emptySet(), backgroundTaskExecutor, localization));
        MenuItem refreshFeedsRestoreDeletedEpisodesMenuItem = new MenuItem(localization.refreshRestoreDeletedEpisodes(), new FontAwesomeIconView(FontAwesomeIcon.REFRESH));
        refreshFeedsRestoreDeletedEpisodesMenuItem.disableProperty().bind(Bindings.isEmpty(this.getSelectedFeeds()));
        refreshFeedsRestoreDeletedEpisodesMenuItem.setOnAction(new RefreshFeedsActionEventHandler(this::getSelectedFeeds, Set.of(Feed.RefreshOption.RESTORE_DELETED_EPISODES), backgroundTaskExecutor, localization));
        Menu feedsMenu = new Menu(localization.feeds(), new FontAwesomeIconView(FontAwesomeIcon.PODCAST));
        feedsMenu.getItems().addAll(refreshFeedsMenuItem, refreshFeedsRestoreDeletedEpisodesMenuItem);
        feedsMenu.disableProperty().bind(Bindings.isEmpty(this.getSelectedFeeds()));
        this.getItems().add(feedsMenu);

        MenuItem markEpisodesAsReadMenuItem = new MenuItem(localization.markAsRead(), new FontAwesomeIconView(FontAwesomeIcon.FOLDER_ALT));
        markEpisodesAsReadMenuItem.disableProperty().bind(Bindings.isEmpty(this.getSelectedEpisodesConsolidatedUnread()));
        markEpisodesAsReadMenuItem.setOnAction(new ChangeEpisodeReadStateActionEventHandler(this::getSelectedEpisodesConsolidatedUnread, Boolean.TRUE, backgroundTaskExecutor, localization));
        MenuItem markEpisodesAsUnreadMenuItem = new MenuItem(localization.markAsUnRead(), new FontAwesomeIconView(FontAwesomeIcon.FOLDER));
        markEpisodesAsUnreadMenuItem.disableProperty().bind(Bindings.isEmpty(this.getSelectedEpisodesConsolidatedRead()));
        markEpisodesAsUnreadMenuItem.setOnAction(new ChangeEpisodeReadStateActionEventHandler(this::getSelectedEpisodesConsolidatedRead, Boolean.FALSE, backgroundTaskExecutor, localization));
        MenuItem refreshEpisodesMenuItem = new MenuItem(localization.refresh(), new FontAwesomeIconView(FontAwesomeIcon.REFRESH));
        refreshEpisodesMenuItem.disableProperty().bind(Bindings.isEmpty(this.getSelectedEpisodesConsolidated()));
        refreshEpisodesMenuItem.setOnAction(new DownloadEpisodesActionEventHandler(this::getSelectedEpisodesConsolidated, episodeDownloader, backgroundTaskExecutor, localization));
        Menu episodesMenu = new Menu(localization.episodes(), new FontAwesomeIconView(FontAwesomeIcon.MICROPHONE));
        episodesMenu.getItems().addAll(markEpisodesAsReadMenuItem, markEpisodesAsUnreadMenuItem, new SeparatorMenuItem(), refreshEpisodesMenuItem);
        episodesMenu.disableProperty().bind(Bindings.isEmpty(this.getSelectedEpisodesConsolidated()));
        this.getItems().add(episodesMenu);

        this.getItems().add(new SeparatorMenuItem());

        MenuItem downloadMenuItem = new MenuItem(localization.download(), new FontAwesomeIconView(FontAwesomeIcon.DOWNLOAD));
        downloadMenuItem.disableProperty().bind(Bindings.isEmpty(this.getSelectedEpisodesConsolidatedNotDownloaded()));
        downloadMenuItem.setOnAction(new DownloadEpisodesActionEventHandler(this::getSelectedEpisodesConsolidatedNotDownloaded, episodeDownloader, backgroundTaskExecutor, localization));
        this.getItems().add(downloadMenuItem);

        MenuItem cancelDownloadsMenuItem = new MenuItem(localization.cancelDownloads(), new FontAwesomeIconView(FontAwesomeIcon.STOP));
        cancelDownloadsMenuItem.disableProperty().bind(Bindings.isEmpty(this.getSelectedEpisodesConsolidatedDownloading()));
        cancelDownloadsMenuItem.setOnAction(new CancelEpisodeDownloadsActionEventHandler(this::getSelectedEpisodesConsolidatedDownloading, episodeDownloader, backgroundTaskExecutor, localization));

        MenuItem deleteMenuItem = new MenuItem(localization.delete(), new FontAwesomeIconView(FontAwesomeIcon.TRASH));
        deleteMenuItem.disableProperty().bind(Bindings.isEmpty(this.getSelectedFeedsDeletable()).and(Bindings.isEmpty(this.getSelectedEpisodesConsolidatedDeletable())));
        deleteMenuItem.setOnAction(new DeleteFeedsOrEpisodesActionEventHandler(this::getSelectedFeedsDeletable, this::getSelectedEpisodesConsolidatedDeletable, library, episodeDownloader, backgroundTaskExecutor, localization));
        this.getItems().add(deleteMenuItem);

    }

    @Override
    public void show(Node anchor, double screenX, double screenY) {
        LibrarySelection selection = this.getSelectionSupplier().get();
        this.getSelectedFeeds().setAll(selection.getSelectedFeeds());
        this.getSelectedFeedsDeletable().setAll(selection.getSelectedFeeds().stream().filter(feed -> feed.getEpisodes().stream().filter(episode -> List.of(EpisodeDownloadState.SCHEDULED, EpisodeDownloadState.CANCELLED).contains(episode.getDownloadState().getValue())).findAny().isEmpty()).collect(Collectors.toList()));
        this.getSelectedEpisodesConsolidated().setAll(selection.getSelectedEpisodesConsolidated());
        this.getSelectedEpisodesConsolidatedRead().setAll(selection.getSelectedEpisodesConsolidated().stream().filter(episode -> Boolean.TRUE.equals(episode.getRead().getValue())).collect(Collectors.toList()));
        this.getSelectedEpisodesConsolidatedUnread().setAll(selection.getSelectedEpisodesConsolidated().stream().filter(episode -> !Boolean.TRUE.equals(episode.getRead().getValue())).collect(Collectors.toList()));
        this.getSelectedEpisodesConsolidatedNotDownloaded().setAll(selection.getSelectedEpisodesConsolidated().stream().filter(episode -> !List.of(EpisodeDownloadState.COMPLETED).contains(episode.getDownloadState().getValue())).collect(Collectors.toList()));
        this.getSelectedEpisodesConsolidatedDownloading().setAll(selection.getSelectedEpisodesConsolidated().stream().filter(episode -> List.of(EpisodeDownloadState.DOWNLOADING, EpisodeDownloadState.SCHEDULED).contains(episode.getDownloadState().getValue())).collect(Collectors.toList()));
        this.getSelectedEpisodesConsolidatedDeletable().setAll(selection.getSelectedEpisodesConsolidated().stream().filter(episode -> !List.of(EpisodeDownloadState.DOWNLOADING, EpisodeDownloadState.SCHEDULED).contains(episode.getDownloadState().getValue())).collect(Collectors.toList()));
        super.show(anchor, screenX, screenY);
    }

    private Supplier<LibrarySelection> getSelectionSupplier() {
        return this.selectionSupplier;
    }
    private void setSelectionSupplier(Supplier<LibrarySelection> selectionSupplier) {
        this.selectionSupplier = selectionSupplier;
    }

    private ObservableList<Feed> getSelectedFeeds() {
        return this.selectedFeeds;
    }
    private ObservableList<Feed> getSelectedFeedsDeletable() {
        return this.selectedFeedsDeletable;
    }
    private ObservableList<Episode> getSelectedEpisodesConsolidated() {
        return this.selectedEpisodesConsolidated;
    }
    private ObservableList<Episode> getSelectedEpisodesConsolidatedRead() {
        return this.selectedEpisodesConsolidatedRead;
    }
    private ObservableList<Episode> getSelectedEpisodesConsolidatedUnread() {
        return this.selectedEpisodesConsolidatedUnread;
    }
    private ObservableList<Episode> getSelectedEpisodesConsolidatedNotDownloaded() {
        return this.selectedEpisodesConsolidatedNotDownloaded;
    }
    private ObservableList<Episode> getSelectedEpisodesConsolidatedDownloading() {
        return this.selectedEpisodesConsolidatedDownloading;
    }
    private ObservableList<Episode> getSelectedEpisodesConsolidatedDeletable() {
        return this.selectedEpisodesConsolidatedDeletable;
    }

}
