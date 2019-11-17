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

import java.io.File;
import java.util.List;
import java.util.function.Supplier;

import de.perdian.apps.podcastcentral.downloader.episodes.EpisodeDownloader;
import de.perdian.apps.podcastcentral.model.Episode;
import de.perdian.apps.podcastcentral.model.EpisodeDownloadState;
import de.perdian.apps.podcastcentral.ui.modules.library.LibrarySelection;
import de.perdian.apps.podcastcentral.ui.modules.library.actions.DownloadEpisodesActionEventHandler;
import de.perdian.apps.podcastcentral.ui.modules.library.actions.OpenEpisodesActionEventHandler;
import de.perdian.apps.podcastcentral.ui.support.backgroundtasks.BackgroundTaskExecutor;
import de.perdian.apps.podcastcentral.ui.support.localization.Localization;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

class LibraryTreeTableClickMouseEventHandler implements EventHandler<MouseEvent> {

    private Supplier<LibrarySelection> selectionSupplier = null;
    private EpisodeDownloader episodeDownloader = null;
    private BackgroundTaskExecutor backgroundTaskExecutor = null;
    private Localization localization = null;

    LibraryTreeTableClickMouseEventHandler(Supplier<LibrarySelection> selectionSupplier, EpisodeDownloader episodeDownloader, BackgroundTaskExecutor backgroundTaskExecutor, Localization localization) {
        this.setSelectionSupplier(selectionSupplier);
        this.setEpisodeDownloader(episodeDownloader);
        this.setBackgroundTaskExecutor(backgroundTaskExecutor);
        this.setLocalization(localization);
    }

    @Override
    public void handle(MouseEvent event) {
        if (event.getClickCount() >= 2 && MouseButton.PRIMARY.equals(event.getButton())) {
            List<Episode> selectedEpisodes = this.getSelectionSupplier().get().getSelectedEpisodesDirectly();
            if (selectedEpisodes.size() == 1) {
                Episode selectedEpisode = selectedEpisodes.get(0);
                File selectedFile = selectedEpisode.getContentFile().getValue();
                if (selectedFile != null && selectedFile.exists() && EpisodeDownloadState.COMPLETED.equals(selectedEpisode.getDownloadState().getValue())) {
                    new OpenEpisodesActionEventHandler(() -> List.of(selectedEpisode)).handle(new ActionEvent(event.getSource(), event.getTarget()));
                } else {
                    new DownloadEpisodesActionEventHandler(() -> List.of(selectedEpisode), this.getEpisodeDownloader(), this.getBackgroundTaskExecutor(), this.getLocalization()).handle(new ActionEvent(event.getSource(), event.getTarget()));
                }
            }
        }
    }

    private Supplier<LibrarySelection> getSelectionSupplier() {
        return this.selectionSupplier;
    }
    private void setSelectionSupplier(Supplier<LibrarySelection> selectionSupplier) {
        this.selectionSupplier = selectionSupplier;
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
