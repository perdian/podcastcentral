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
package de.perdian.apps.podcastcentral.ui.modules.library.actions;

import java.util.List;
import java.util.function.Supplier;

import de.perdian.apps.podcastcentral.downloader.episodes.EpisodeDownloader;
import de.perdian.apps.podcastcentral.model.Episode;
import de.perdian.apps.podcastcentral.ui.support.backgroundtasks.BackgroundTaskExecutor;
import de.perdian.apps.podcastcentral.ui.support.localization.Localization;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;

public class CancelDownloadEpisodesActionEventHandler implements EventHandler<ActionEvent> {

    private Supplier<List<Episode>> episodesSupplier = null;
    private BackgroundTaskExecutor backgroundTaskExecutor = null;
    private EpisodeDownloader episodeDownloader = null;
    private Localization localization = null;

    public CancelDownloadEpisodesActionEventHandler(Supplier<List<Episode>> episodesSupplier, BackgroundTaskExecutor backgroundTaskExecutor, EpisodeDownloader episodeDownloader, Localization localization) {
        this.setEpisodesSupplier(episodesSupplier);
        this.setBackgroundTaskExecutor(backgroundTaskExecutor);
        this.setEpisodeContentDownloader(episodeDownloader);
        this.setLocalization(localization);
    }

    @Override
    public void handle(ActionEvent event) {
        List<Episode> episodes = this.getEpisodesSupplier().get();
        if (!episodes.isEmpty()) {
            this.getBackgroundTaskExecutor().execute(this.getLocalization().cancellingEpisodeDownloads(), progress -> {
                for (int i=0; i < episodes.size(); i++) {
                    progress.updateProgress((double)i / (double)episodes.size(), null);
                    this.getEpisodeContentDownloader().cancelDownload(episodes.get(i));
                }
            });
        }
    }

    private Supplier<List<Episode>> getEpisodesSupplier() {
        return this.episodesSupplier;
    }
    private void setEpisodesSupplier(Supplier<List<Episode>> episodesSupplier) {
        this.episodesSupplier = episodesSupplier;
    }

    private BackgroundTaskExecutor getBackgroundTaskExecutor() {
        return this.backgroundTaskExecutor;
    }
    private void setBackgroundTaskExecutor(BackgroundTaskExecutor backgroundTaskExecutor) {
        this.backgroundTaskExecutor = backgroundTaskExecutor;
    }

    private EpisodeDownloader getEpisodeContentDownloader() {
        return this.episodeDownloader;
    }
    private void setEpisodeContentDownloader(EpisodeDownloader episodeDownloader) {
        this.episodeDownloader = episodeDownloader;
    }

    private Localization getLocalization() {
        return this.localization;
    }
    private void setLocalization(Localization localization) {
        this.localization = localization;
    }

}
