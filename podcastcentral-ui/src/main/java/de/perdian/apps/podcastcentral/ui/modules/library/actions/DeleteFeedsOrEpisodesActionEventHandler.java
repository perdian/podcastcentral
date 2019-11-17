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
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import de.perdian.apps.podcastcentral.downloader.episodes.EpisodeDownloader;
import de.perdian.apps.podcastcentral.model.Episode;
import de.perdian.apps.podcastcentral.model.Feed;
import de.perdian.apps.podcastcentral.model.Library;
import de.perdian.apps.podcastcentral.ui.support.backgroundtasks.BackgroundTaskExecutor;
import de.perdian.apps.podcastcentral.ui.support.localization.Localization;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;

public class DeleteFeedsOrEpisodesActionEventHandler implements EventHandler<ActionEvent> {

    private Supplier<List<Feed>> feedsSupplier = null;
    private Supplier<List<Episode>> episodesSupplier = null;
    private Library library = null;
    private EpisodeDownloader episodeDownloader = null;
    private BackgroundTaskExecutor backgroundTaskExecutor = null;
    private Localization localization = null;

    public DeleteFeedsOrEpisodesActionEventHandler(Supplier<List<Feed>> feedsSupplier, Supplier<List<Episode>> episodesSupplier, Library library, EpisodeDownloader episodeDownloader, BackgroundTaskExecutor backgroundTaskExecutor, Localization localization) {
        this.setFeedsSupplier(feedsSupplier);
        this.setEpisodesSupplier(episodesSupplier);
        this.setLibrary(library);
        this.setEpisodeDownloader(episodeDownloader);
        this.setBackgroundTaskExecutor(backgroundTaskExecutor);
        this.setLocalization(localization);
    }

    @Override
    public void handle(ActionEvent event) {
        List<Feed> feeds = this.getFeedsSupplier().get();
        if (!feeds.isEmpty()) {
            Alert confirmationAlert = new Alert(AlertType.CONFIRMATION);
            confirmationAlert.setTitle(this.getLocalization().delete());
            confirmationAlert.setHeaderText(null);
            confirmationAlert.setContentText(this.getLocalization().reallyDeleteFeeds(feeds.size()));
            if (!ButtonType.OK.equals(confirmationAlert.showAndWait().orElse(ButtonType.CANCEL))) {
                return;
            }
        }
        this.getBackgroundTaskExecutor().execute(this.getLocalization().deletingEntries(), progress -> {
            for (int i=0; i < feeds.size(); i++) {
                progress.updateProgress((double)(i+1) / (double)feeds.size(), this.getLocalization().deletingFeed(feeds.get(i).getTitle().getValue()));
                feeds.get(i).getEpisodes().forEach(episode -> this.getEpisodeDownloader().cancelDownload(episode));
                this.getLibrary().deleteFeeds(List.of(feeds.get(i)));
            }
            List<Episode> remainingEpisodes = this.getEpisodesSupplier().get().stream().filter(episode -> !feeds.contains(episode.getFeed())).collect(Collectors.toList());
            Map<Feed, List<Episode>> remainingEpisodesByFeed = Episode.mapByFeed(remainingEpisodes);
            int feedIndex = 0;
            for (Map.Entry<Feed, List<Episode>> remainingEpisodesByFeedEntry : remainingEpisodesByFeed.entrySet()) {
                progress.updateProgress((double)(++feedIndex) / (double)remainingEpisodes.size(), this.getLocalization().deletingEpisodesFromFeed(remainingEpisodesByFeedEntry.getValue().size(), remainingEpisodesByFeedEntry.getKey().getTitle().getValue()));
                remainingEpisodesByFeedEntry.getValue().forEach(episode -> this.getEpisodeDownloader().cancelDownload(episode));
                remainingEpisodesByFeedEntry.getKey().deleteEpisodes(remainingEpisodesByFeedEntry.getValue());
            }
        });
    }

    private Supplier<List<Feed>> getFeedsSupplier() {
        return this.feedsSupplier;
    }
    private void setFeedsSupplier(Supplier<List<Feed>> feedsSupplier) {
        this.feedsSupplier = feedsSupplier;
    }

    private Supplier<List<Episode>> getEpisodesSupplier() {
        return this.episodesSupplier;
    }
    private void setEpisodesSupplier(Supplier<List<Episode>> episodesSupplier) {
        this.episodesSupplier = episodesSupplier;
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
