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
import java.util.stream.Collectors;

import de.perdian.apps.podcentral.jobscheduler.Job;
import de.perdian.apps.podcentral.jobscheduler.JobScheduler;
import de.perdian.apps.podcentral.model.Episode;
import de.perdian.apps.podcentral.model.Feed;
import de.perdian.apps.podcentral.model.Library;
import de.perdian.apps.podcentral.ui.localization.Localization;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;

public class FeedDeleteEventHandler implements EventHandler<ActionEvent> {

    private List<Feed> feeds = null;
    private Map<Feed, List<Episode>> episodes = null;
    private Runnable clearSelectionCallback = null;
    private JobScheduler jobScheduler = null;
    private Library library = null;
    private Localization localization = null;

    public FeedDeleteEventHandler(List<Feed> feeds, Map<Feed, List<Episode>> episodes, Runnable clearSelectionCallback, JobScheduler jobScheduler, Library library, Localization localization) {
        this.setFeeds(feeds);
        this.setEpisodes(episodes);
        this.setClearSelectionCallback(clearSelectionCallback);
        this.setLibrary(library);
        this.setJobScheduler(jobScheduler);
        this.setLocalization(localization);
    }

    @Override
    public void handle(ActionEvent event) {
        if (!this.getFeeds().isEmpty() || !this.getEpisodes().isEmpty()) {
            this.getJobScheduler().submitJob(new Job(this.getLocalization().deletingEntries(), progress -> {

                List<Feed> feeds = new ArrayList<>(this.getFeeds());
                Map<Feed, List<Episode>> episodes = this.getEpisodes().entrySet().stream().filter(entry -> feeds.contains(entry.getKey())).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
                Set<Feed> allFeeds = new HashSet<>();
                allFeeds.addAll(feeds);
                allFeeds.addAll(episodes.keySet());
                allFeeds.forEach(feed -> feed.getProcessors().add(this));

                feeds.forEach(feed -> {
                    try {
                        this.getLibrary().getFeeds().remove(feed);
                    } finally {
                        feed.getProcessors().remove(this);
                    }
                });
                feeds.forEach(feed -> feed.getProcessors().remove(this));

                episodes.entrySet().forEach(entry -> {
                    try {
                        entry.getKey().getEpisodes().removeAll(entry.getValue());
                    } finally {
                        entry.getKey().getProcessors().remove(this);
                    }
                });
                episodes.keySet().forEach(feed -> feed.getProcessors().remove(this));

                this.getClearSelectionCallback().run();

            }));
        }
    }

    private List<Feed> getFeeds() {
        return this.feeds;
    }
    private void setFeeds(List<Feed> feeds) {
        this.feeds = feeds;
    }

    private Map<Feed, List<Episode>> getEpisodes() {
        return this.episodes;
    }
    private void setEpisodes(Map<Feed, List<Episode>> episodes) {
        this.episodes = episodes;
    }

    private Runnable getClearSelectionCallback() {
        return this.clearSelectionCallback;
    }
    private void setClearSelectionCallback(Runnable clearSelectionCallback) {
        this.clearSelectionCallback = clearSelectionCallback;
    }

    private JobScheduler getJobScheduler() {
        return this.jobScheduler;
    }
    private void setJobScheduler(JobScheduler jobScheduler) {
        this.jobScheduler = jobScheduler;
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
