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
package de.perdian.apps.podcastcentral.ui.modules.library_new.treetable;

import java.util.function.Supplier;
import java.util.stream.Collectors;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import de.perdian.apps.podcastcentral.downloader.episodes.EpisodeDownloader;
import de.perdian.apps.podcastcentral.model.Episode;
import de.perdian.apps.podcastcentral.model.EpisodeDownloadState;
import de.perdian.apps.podcastcentral.ui.modules.library_new.actions.ChangeEpisodeReadStateActionEventHandler;
import de.perdian.apps.podcastcentral.ui.modules.library_new.actions.DownloadEpisodesActionEventHandler;
import de.perdian.apps.podcastcentral.ui.support.backgroundtasks.BackgroundTaskExecutor;
import de.perdian.apps.podcastcentral.ui.support.localization.Localization;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;

class LibraryTreeTableContextMenu extends ContextMenu {

    private Supplier<LibraryTreeTableSelection> selectionSupplier = null;
    private final ObservableList<Episode> selectedEpisodesConsolidated = FXCollections.observableArrayList();
    private final ObservableList<Episode> selectedEpisodesConsolidatedRead = FXCollections.observableArrayList();
    private final ObservableList<Episode> selectedEpisodesConsolidatedUnread = FXCollections.observableArrayList();
    private final ObservableList<Episode> selectedEpisodesConsolidatedNotDownloaded = FXCollections.observableArrayList();

    LibraryTreeTableContextMenu(Supplier<LibraryTreeTableSelection> selectionSupplier, EpisodeDownloader episodeDownloader, BackgroundTaskExecutor backgroundTaskExecutor, Localization localization) {
        this.setSelectionSupplier(selectionSupplier);

        MenuItem markEpisodesAsReadMenuItem = new MenuItem(localization.read(), new FontAwesomeIconView(FontAwesomeIcon.FOLDER_ALT));
        markEpisodesAsReadMenuItem.disableProperty().bind(Bindings.isEmpty(this.getSelectedEpisodesConsolidatedUnread()));
        markEpisodesAsReadMenuItem.setOnAction(new ChangeEpisodeReadStateActionEventHandler(this::getSelectedEpisodesConsolidatedUnread, Boolean.TRUE, backgroundTaskExecutor, localization));
        MenuItem markEpisodesAsUnreadMenuItem = new MenuItem(localization.unread(), new FontAwesomeIconView(FontAwesomeIcon.FOLDER));
        markEpisodesAsUnreadMenuItem.disableProperty().bind(Bindings.isEmpty(this.getSelectedEpisodesConsolidatedRead()));
        markEpisodesAsUnreadMenuItem.setOnAction(new ChangeEpisodeReadStateActionEventHandler(this::getSelectedEpisodesConsolidatedRead, Boolean.FALSE, backgroundTaskExecutor, localization));
        Menu markEpisodesMenu = new Menu(localization.markEpisodes(), new FontAwesomeIconView(FontAwesomeIcon.FOLDER), markEpisodesAsReadMenuItem, markEpisodesAsUnreadMenuItem);
        markEpisodesMenu.disableProperty().bind(Bindings.isEmpty(this.getSelectedEpisodesConsolidated()));
        this.getItems().add(markEpisodesMenu);

        MenuItem downloadEpisodesMenuItem = new MenuItem(localization.downloadNewEpisodes(), new FontAwesomeIconView(FontAwesomeIcon.DOWNLOAD));
        downloadEpisodesMenuItem.disableProperty().bind(Bindings.isEmpty(this.getSelectedEpisodesConsolidatedNotDownloaded()));
        downloadEpisodesMenuItem.setOnAction(new DownloadEpisodesActionEventHandler(this::getSelectedEpisodesConsolidatedNotDownloaded, episodeDownloader, backgroundTaskExecutor, localization));
        MenuItem downloadEpisodesRedownloadMenuItem = new MenuItem(localization.redownloadEpisodes(), new FontAwesomeIconView(FontAwesomeIcon.DOWNLOAD));
        downloadEpisodesRedownloadMenuItem.disableProperty().bind(Bindings.isEmpty(this.getSelectedEpisodesConsolidated()));
        downloadEpisodesRedownloadMenuItem.setOnAction(new DownloadEpisodesActionEventHandler(this::getSelectedEpisodesConsolidated, episodeDownloader, backgroundTaskExecutor, localization));
        Menu downloadEpisodesMenu = new Menu(localization.downloadEpisodes(), new FontAwesomeIconView(FontAwesomeIcon.DOWNLOAD));
        downloadEpisodesMenu.disableProperty().bind(Bindings.isEmpty(this.getSelectedEpisodesConsolidated()));
        downloadEpisodesMenu.getItems().addAll(downloadEpisodesMenuItem, downloadEpisodesRedownloadMenuItem);
        this.getItems().add(downloadEpisodesMenu);

    }

    @Override
    public void show(Node anchor, double screenX, double screenY) {
        LibraryTreeTableSelection selection = this.getSelectionSupplier().get();
        this.getSelectedEpisodesConsolidated().setAll(selection.getSelectedEpisodesConsolidated());
        this.getSelectedEpisodesConsolidatedRead().setAll(selection.getSelectedEpisodesConsolidated().stream().filter(episode -> Boolean.TRUE.equals(episode.getRead().getValue())).collect(Collectors.toList()));
        this.getSelectedEpisodesConsolidatedUnread().setAll(selection.getSelectedEpisodesConsolidated().stream().filter(episode -> !Boolean.TRUE.equals(episode.getRead().getValue())).collect(Collectors.toList()));
        this.getSelectedEpisodesConsolidatedNotDownloaded().setAll(selection.getSelectedEpisodesConsolidated().stream().filter(episode -> !EpisodeDownloadState.COMPLETED.equals(episode.getDownloadState().getValue())).collect(Collectors.toList()));
        super.show(anchor, screenX, screenY);
    }

    private Supplier<LibraryTreeTableSelection> getSelectionSupplier() {
        return this.selectionSupplier;
    }
    private void setSelectionSupplier(Supplier<LibraryTreeTableSelection> selectionSupplier) {
        this.selectionSupplier = selectionSupplier;
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

}
