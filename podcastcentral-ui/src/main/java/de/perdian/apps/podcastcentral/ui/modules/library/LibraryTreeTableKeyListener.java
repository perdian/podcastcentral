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
package de.perdian.apps.podcastcentral.ui.modules.library;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.perdian.apps.podcastcentral.downloader.episodes.EpisodeDownloader;
import de.perdian.apps.podcastcentral.model.Episode;
import de.perdian.apps.podcastcentral.model.Library;
import de.perdian.apps.podcastcentral.ui.modules.library.actions.DeleteFeedActionEventHandler;
import de.perdian.apps.podcastcentral.ui.modules.library.actions.OpenEpisodeActionEventHandler;
import de.perdian.apps.podcastcentral.ui.modules.library.actions.StartDownloadEpisodesActionEventHandler;
import de.perdian.apps.podcastcentral.ui.support.backgroundtasks.BackgroundTaskExecutor;
import de.perdian.apps.podcastcentral.ui.support.localization.Localization;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

class LibraryTreeTableKeyListener implements EventHandler<KeyEvent> {

    private LibrarySelection librarySelection = null;
    private Library library = null;
    private EpisodeDownloader episodeDownloader = null;
    private BackgroundTaskExecutor backgroundTaskExecutor = null;
    private Localization localization = null;

    LibraryTreeTableKeyListener(LibrarySelection librarySelection, Library library, EpisodeDownloader episodeDownloader, BackgroundTaskExecutor backgroundTaskExecutor, Localization localization) {
        this.setLibrarySelection(librarySelection);
        this.setLibrary(library);
        this.setEpisodeDownloader(episodeDownloader);
        this.setBackgroundTaskExecutor(backgroundTaskExecutor);
        this.setLocalization(localization);
    }

    @Override
    public void handle(KeyEvent event) {
        if (event.getCode() == KeyCode.DELETE || event.getCode() == KeyCode.X || (event.getCode() == KeyCode.BACK_SPACE && event.isMetaDown())) {
            List<Episode> selectedEpisodes = new ArrayList<>(this.getLibrarySelection().update().getSelectedEpisodes());
            if (!selectedEpisodes.isEmpty()) {
                DeleteFeedActionEventHandler deleteFeedActionEventHandler = new DeleteFeedActionEventHandler(Collections::emptyList, () -> selectedEpisodes, this.getLibrary(), this.getBackgroundTaskExecutor(), this.getLocalization());
                deleteFeedActionEventHandler.handle(new ActionEvent(event.getSource(), event.getTarget()));
            }
            event.consume();
        } else if (event.getCode() == KeyCode.D) {
            List<Episode> downloadableEpisodes = new ArrayList<>(this.getLibrarySelection().update().getSelectedEpisodesForDownload());
            if (!downloadableEpisodes.isEmpty()) {
                StartDownloadEpisodesActionEventHandler downloadActionEventHandler = new StartDownloadEpisodesActionEventHandler(() -> downloadableEpisodes, this.getEpisodeDownloader(), this.getBackgroundTaskExecutor(), this.getLocalization());
                downloadActionEventHandler.handle(new ActionEvent(event.getSource(), event.getTarget()));
            }
            event.consume();
        } else if (event.getCode() == KeyCode.O) {
            List<Episode> openableEpisodes = new ArrayList<>(this.getLibrarySelection().update().getSelectedEpisodesForOpen());
            OpenEpisodeActionEventHandler openEpisodeEventHandler = new OpenEpisodeActionEventHandler(() -> openableEpisodes);
            openEpisodeEventHandler.handle(new ActionEvent(event.getSource(), event.getTarget()));
            event.consume();
        }
    }

    private LibrarySelection getLibrarySelection() {
        return this.librarySelection;
    }
    private void setLibrarySelection(LibrarySelection librarySelection) {
        this.librarySelection = librarySelection;
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