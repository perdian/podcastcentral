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

import java.io.File;
import java.util.List;

import de.perdian.apps.podcastcentral.downloader.episodes.EpisodeDownloader;
import de.perdian.apps.podcastcentral.model.Episode;
import de.perdian.apps.podcastcentral.model.EpisodeDownloadState;
import de.perdian.apps.podcastcentral.ui.modules.library.actions.OpenEpisodeActionEventHandler;
import de.perdian.apps.podcastcentral.ui.modules.library.actions.StartDownloadEpisodesActionEventHandler;
import de.perdian.apps.podcastcentral.ui.support.backgroundtasks.BackgroundTaskExecutor;
import de.perdian.apps.podcastcentral.ui.support.localization.Localization;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

class LibraryTreeTableMouseClickedEventListener implements EventHandler<MouseEvent> {

    private LibrarySelection librarySelection = null;
    private EpisodeDownloader episodeDownloader = null;
    private BackgroundTaskExecutor backgroundTaskExecutor = null;
    private Localization localization = null;

    LibraryTreeTableMouseClickedEventListener(LibrarySelection librarySelection, EpisodeDownloader episodeDownloader, BackgroundTaskExecutor backgroundTaskExecutor, Localization localization) {
        this.setLibrarySelection(librarySelection);
        this.setEpisodeDownloader(episodeDownloader);
        this.setBackgroundTaskExecutor(backgroundTaskExecutor);
        this.setLocalization(localization);
    }

    @Override
    public void handle(MouseEvent event) {
        if (event.getClickCount() >= 2 && MouseButton.PRIMARY.equals(event.getButton())) {
            List<Episode> selectedEpisodes = this.getLibrarySelection().getSelectedEpisodes();
            if (selectedEpisodes.size() == 1) {
                Episode selectedEpisode = selectedEpisodes.get(0);
                File selectedFile = selectedEpisode.getContentFile().getValue();
                if (selectedFile != null && selectedFile.exists() && EpisodeDownloadState.COMPLETED.equals(selectedEpisode.getDownloadState().getValue())) {
                    OpenEpisodeActionEventHandler eventHandler = new OpenEpisodeActionEventHandler(() -> List.of(selectedEpisode));
                    eventHandler.handle(new ActionEvent(event.getSource(), event.getTarget()));
                } else {
                    StartDownloadEpisodesActionEventHandler eventHandler = new StartDownloadEpisodesActionEventHandler(() -> List.of(selectedEpisode), this.getEpisodeDownloader(), this.getBackgroundTaskExecutor(), this.getLocalization());
                    eventHandler.handle(new ActionEvent(event.getSource(), event.getTarget()));
                }
            }
        }
    }

    private LibrarySelection getLibrarySelection() {
        return this.librarySelection;
    }
    private void setLibrarySelection(LibrarySelection librarySelection) {
        this.librarySelection = librarySelection;
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
