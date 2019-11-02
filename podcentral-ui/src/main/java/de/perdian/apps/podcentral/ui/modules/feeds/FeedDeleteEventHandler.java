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
import java.util.List;
import java.util.Map;

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
                List<Runnable> deleteRunnables = this.createDeleteRunnables();
                for (int i=0; i < deleteRunnables.size(); i++) {
                    progress.updateProgress((double)(i+1) / (double)deleteRunnables.size(), null);
                    deleteRunnables.get(i).run();
                }
                this.getClearSelectionCallback().run();
            }));
        }
    }

    private List<Runnable> createDeleteRunnables() {
        List<Runnable> runnables = new ArrayList<>();
        this.getFeeds().stream().forEach(feed -> runnables.add(() -> this.getLibrary().getFeeds().remove(feed)));
        this.getEpisodes().entrySet().stream().filter(entry -> !this.getFeeds().contains(entry.getKey())).forEach(entry -> entry.getKey().getEpisodes().removeAll(entry.getValue()));
        return runnables;
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
