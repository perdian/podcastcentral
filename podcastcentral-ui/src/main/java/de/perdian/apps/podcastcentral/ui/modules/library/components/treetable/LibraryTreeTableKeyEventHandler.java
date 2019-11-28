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

import de.perdian.apps.podcastcentral.downloader.episodes.EpisodeDownloader;
import de.perdian.apps.podcastcentral.model.Episode;
import de.perdian.apps.podcastcentral.model.EpisodeDownloadState;
import de.perdian.apps.podcastcentral.model.Feed;
import de.perdian.apps.podcastcentral.model.Library;
import de.perdian.apps.podcastcentral.ui.modules.library.actions.DeleteFeedsOrEpisodesActionEventHandler;
import de.perdian.apps.podcastcentral.ui.modules.library.actions.DownloadEpisodesActionEventHandler;
import de.perdian.apps.podcastcentral.ui.modules.library.actions.OpenEpisodesActionEventHandler;
import de.perdian.apps.podcastcentral.ui.modules.library.actions.RefreshFeedsActionEventHandler;
import de.perdian.apps.podcastcentral.ui.modules.library.actions.ShowEpisodeDetailsActionEventHandler;
import de.perdian.apps.podcastcentral.ui.modules.library.actions.ShowFeedDetailsActionEventHandler;
import de.perdian.apps.podcastcentral.ui.support.backgroundtasks.BackgroundTaskExecutor;
import de.perdian.apps.podcastcentral.ui.support.localization.Localization;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Window;

class LibraryTreeTableKeyEventHandler implements EventHandler<KeyEvent> {

    private Supplier<Window> ownerSupplier = null;
    private Supplier<LibraryTreeTableSelection> selectionSupplier = null;
    private Library library = null;
    private EpisodeDownloader episodeDownloader = null;
    private BackgroundTaskExecutor backgroundTaskExecutor = null;
    private Localization localization = null;

    LibraryTreeTableKeyEventHandler(Supplier<Window> ownerSupplier, Supplier<LibraryTreeTableSelection> selectionSupplier, Library library, EpisodeDownloader episodeDownloader, BackgroundTaskExecutor backgroundTaskExecutor, Localization localization) {
        this.setOwnerSupplier(ownerSupplier);
        this.setSelectionSupplier(selectionSupplier);
        this.setLibrary(library);
        this.setEpisodeDownloader(episodeDownloader);
        this.setBackgroundTaskExecutor(backgroundTaskExecutor);
        this.setLocalization(localization);
    }

    @Override
    public void handle(KeyEvent event) {
        if (event.getCode() == KeyCode.DELETE || event.getCode() == KeyCode.X || (event.getCode() == KeyCode.BACK_SPACE && event.isMetaDown())) {
            LibraryTreeTableSelection selection = this.getSelectionSupplier().get();
            List<Feed> selectedFeeds = selection.getSelectedEpisodesDirectly().isEmpty() || event.isShiftDown() ? selection.getSelectedFeeds() : Collections.emptyList();
            List<Episode> selectedEpisodes = selection.getSelectedEpisodesDirectly();
            new DeleteFeedsOrEpisodesActionEventHandler(this.getOwnerSupplier(), () -> selectedFeeds, () -> selectedEpisodes, this.getLibrary(), this.getEpisodeDownloader(), this.getBackgroundTaskExecutor(), this.getLocalization()).handle(new ActionEvent(event.getSource(), event.getTarget()));
            event.consume();
        } else if (event.getCode() == KeyCode.D) {
            List<Episode> downloadableEpisodes = this.getSelectionSupplier().get().getSelectedEpisodesConsolidated().stream().filter(episode -> !List.of(EpisodeDownloadState.COMPLETED, EpisodeDownloadState.SCHEDULED, EpisodeDownloadState.DOWNLOADING).contains(episode.getDownloadState().getValue())).collect(Collectors.toList());
            new DownloadEpisodesActionEventHandler(() -> downloadableEpisodes, this.getEpisodeDownloader(), this.getBackgroundTaskExecutor(), this.getLocalization()).handle(new ActionEvent(event.getSource(), event.getTarget()));
            event.consume();
        } else if (event.getCode() == KeyCode.R) {
            LibraryTreeTableSelection selection = this.getSelectionSupplier().get();
            List<Episode> refreshableEpisodes = selection.getSelectedEpisodesDirectly().stream().filter(episode -> List.of(EpisodeDownloadState.COMPLETED).contains(episode.getDownloadState().getValue())).collect(Collectors.toList());
            Set<Feed.RefreshOption> refreshOptions = event.isShiftDown() ? Set.of(Feed.RefreshOption.RESTORE_DELETED_EPISODES) : Collections.emptySet();
            new DownloadEpisodesActionEventHandler(() -> refreshableEpisodes, this.getEpisodeDownloader(), this.getBackgroundTaskExecutor(), this.getLocalization()).handle(new ActionEvent(event.getSource(), event.getTarget()));
            new RefreshFeedsActionEventHandler(() -> selection.getSelectedFeeds(), refreshOptions, this.getBackgroundTaskExecutor(), this.getLocalization()).handle(new ActionEvent(event.getSource(), event.getTarget()));
            event.consume();
        } else if (event.getCode() == KeyCode.O) {
            List<Episode> openableEpisodes = this.getSelectionSupplier().get().getSelectedEpisodesConsolidated().stream().filter(episode -> List.of(EpisodeDownloadState.COMPLETED).contains(episode.getDownloadState().getValue())).collect(Collectors.toList());
            new OpenEpisodesActionEventHandler(() -> openableEpisodes).handle(new ActionEvent(event.getSource(), event.getTarget()));
            event.consume();
        } else if (event.getCode() == KeyCode.SPACE) {
            LibraryTreeTableSelection selection = this.getSelectionSupplier().get();
            if (selection.getSelectedFeeds().isEmpty() && selection.getSelectedEpisodesDirectly().size() == 1) {
                new ShowEpisodeDetailsActionEventHandler(() -> selection.getSelectedEpisodesDirectly().get(0), this.getLocalization()).handle(new ActionEvent(event.getSource(), event.getTarget()));
                event.consume();
            } else if (selection.getSelectedFeeds().size() == 1 && selection.getSelectedEpisodesDirectly().isEmpty()) {
                new ShowFeedDetailsActionEventHandler(() -> selection.getSelectedFeeds().get(0), this.getLocalization()).handle(new ActionEvent(event.getSource(), event.getTarget()));
                event.consume();
            }
        }
    }

    private Supplier<Window> getOwnerSupplier() {
        return this.ownerSupplier;
    }
    private void setOwnerSupplier(Supplier<Window> ownerSupplier) {
        this.ownerSupplier = ownerSupplier;
    }

    private Supplier<LibraryTreeTableSelection> getSelectionSupplier() {
        return this.selectionSupplier;
    }
    private void setSelectionSupplier(Supplier<LibraryTreeTableSelection> selectionSupplier) {
        this.selectionSupplier = selectionSupplier;
    }

    private Library getLibrary() {
        return this.library;
    }
    private void setLibrary(Library library) {
        this.library = library;
    }

    private EpisodeDownloader getEpisodeDownloader() {
        return this.episodeDownloader;
    }
    private void setEpisodeDownloader(EpisodeDownloader episodeDownloader) {
        this.episodeDownloader = episodeDownloader;
    }

    private BackgroundTaskExecutor getBackgroundTaskExecutor() {
        return this.backgroundTaskExecutor;
    }
    private void setBackgroundTaskExecutor(BackgroundTaskExecutor backgroundTaskExecutor) {
        this.backgroundTaskExecutor = backgroundTaskExecutor;
    }

    private Localization getLocalization() {
        return this.localization;
    }
    private void setLocalization(Localization localization) {
        this.localization = localization;
    }

}
