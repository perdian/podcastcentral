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
package de.perdian.apps.podcentral.ui.modules.library.actions;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import de.perdian.apps.podcentral.model.Episode;
import de.perdian.apps.podcentral.model.Feed;
import de.perdian.apps.podcentral.model.Library;
import de.perdian.apps.podcentral.ui.support.backgroundtasks.BackgroundTaskExecutor;
import de.perdian.apps.podcentral.ui.support.localization.Localization;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;

public class DeleteFeedActionEventHandler implements EventHandler<ActionEvent> {

    private Supplier<List<Feed>> feedsSupplier = null;
    private Supplier<List<Episode>> episodesSupplier = null;
    private Library library = null;
    private BackgroundTaskExecutor backgroundTaskExecutor = null;
    private Localization localization = null;

    public DeleteFeedActionEventHandler(Supplier<List<Feed>> feedsSupplier, Supplier<List<Episode>> episodesSupplier, Library library, BackgroundTaskExecutor backgroundTaskExecutor, Localization localization) {
        this.setFeedsSupplier(feedsSupplier);
        this.setEpisodesSupplier(episodesSupplier);
        this.setLibrary(library);
        this.setBackgroundTaskExecutor(backgroundTaskExecutor);
        this.setLocalization(localization);
    }

    @Override
    public void handle(ActionEvent event) {
        List<Feed> feeds = this.getFeedsSupplier().get();
        List<Episode> episodes = this.getEpisodesSupplier().get().stream().filter(episode -> !feeds.contains(episode.getFeed())).collect(Collectors.toList());
        if (!feeds.isEmpty() || !episodes.isEmpty()) {
            this.getBackgroundTaskExecutor().execute(this.getLocalization().deletingEntries(), progress -> {
                this.getLibrary().deleteFeeds(feeds);
                Episode.mapByFeed(episodes).forEach((feed, feedEpisodes) -> feed.deleteEpisodes(feedEpisodes));
            });
        }
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
