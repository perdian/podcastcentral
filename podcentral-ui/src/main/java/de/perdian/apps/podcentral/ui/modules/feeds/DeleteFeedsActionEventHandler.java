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
package de.perdian.apps.podcentral.ui.modules.feeds;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import de.perdian.apps.podcentral.model.Episode;
import de.perdian.apps.podcentral.model.Feed;
import de.perdian.apps.podcentral.model.Library;
import de.perdian.apps.podcentral.ui.localization.Localization;
import de.perdian.apps.podcentral.ui.support.tasks.BackgroundTaskExecutor;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;

public class DeleteFeedsActionEventHandler implements EventHandler<ActionEvent> {

    private Supplier<List<Feed>> feedsSupplier = null;
    private Supplier<Map<Feed, List<Episode>>> episodesSupplier = null;
    private BackgroundTaskExecutor backgroundTaskExecutor = null;
    private Library library = null;
    private Localization localization = null;

    public DeleteFeedsActionEventHandler(Supplier<List<Feed>> feedsSupplier, Supplier<Map<Feed, List<Episode>>> episodesSupplier, BackgroundTaskExecutor backgroundTaskExecutor, Library library, Localization localization) {
        this.setFeedsSupplier(feedsSupplier);
        this.setEpisodesSupplier(episodesSupplier);
        this.setLibrary(library);
        this.setBackgroundTaskExecutor(backgroundTaskExecutor);
        this.setLocalization(localization);
    }

    @Override
    public void handle(ActionEvent event) {
        List<Feed> feeds = this.getFeedsSupplier().get();
        Map<Feed, List<Episode>> episodes = this.getEpisodesSupplier().get();
        if (!feeds.isEmpty() || !episodes.isEmpty()) {
            this.getBackgroundTaskExecutor().execute(this.getLocalization().deletingEntries(), progress -> {

                List<Feed> feedsToDelete = new ArrayList<>(feeds);
                Map<Feed, List<Episode>> episodesToDelete = episodes.entrySet().stream().filter(entry -> !feedsToDelete.contains(entry.getKey())).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
                Set<Feed> allFeeds = new HashSet<>();
                allFeeds.addAll(feedsToDelete);
                allFeeds.addAll(episodesToDelete.keySet());
                allFeeds.forEach(feed -> feed.getProcessors().add(this));

                feedsToDelete.forEach(feed -> {
                    try {
                        this.getLibrary().getFeeds().remove(feed);
                    } finally {
                        feed.getProcessors().remove(this);
                    }
                });
                feedsToDelete.forEach(feed -> feed.getProcessors().remove(this));

                episodesToDelete.entrySet().forEach(entry -> {
                    try {
                        entry.getKey().getEpisodes().removeAll(entry.getValue());
                    } finally {
                        entry.getKey().getProcessors().remove(this);
                    }
                });
                episodesToDelete.keySet().forEach(feed -> feed.getProcessors().remove(this));

            });
        }
    }

    private Supplier<List<Feed>> getFeedsSupplier() {
        return this.feedsSupplier;
    }
    private void setFeedsSupplier(Supplier<List<Feed>> feedsSupplier) {
        this.feedsSupplier = feedsSupplier;
    }

    private Supplier<Map<Feed, List<Episode>>> getEpisodesSupplier() {
        return this.episodesSupplier;
    }
    private void setEpisodesSupplier(Supplier<Map<Feed, List<Episode>>> episodesSupplier) {
        this.episodesSupplier = episodesSupplier;
    }

    private BackgroundTaskExecutor getBackgroundTaskExecutor() {
        return this.backgroundTaskExecutor;
    }
    private void setBackgroundTaskExecutor(BackgroundTaskExecutor backgroundTaskExecutor) {
        this.backgroundTaskExecutor = backgroundTaskExecutor;
    }

    private Library getLibrary() {
        return this.library;
    }
    private void setLibrary(Library library) {
        this.library = library;
    }

    private Localization getLocalization() {
        return this.localization;
    }
    private void setLocalization(Localization localization) {
        this.localization = localization;
    }

}
