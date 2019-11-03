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
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

import de.perdian.apps.podcentral.jobscheduler.Job;
import de.perdian.apps.podcentral.jobscheduler.JobScheduler;
import de.perdian.apps.podcentral.model.Feed;
import de.perdian.apps.podcentral.model.FeedInput;
import de.perdian.apps.podcentral.retrieval.FeedInputLoader;
import de.perdian.apps.podcentral.ui.localization.Localization;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;

public class FeedRefreshActionEventHandler implements EventHandler<ActionEvent> {

    private Supplier<List<Feed>> feedListSupplier = null;
    private Runnable clearSelectionCallback = null;
    private JobScheduler jobScheduler = null;
    private Localization localization = null;
    private Set<Feed.RefreshOption> feedRefreshOptions = Collections.emptySet();

    public FeedRefreshActionEventHandler(Supplier<List<Feed>> feedListSupplier, Set<Feed.RefreshOption> feedRefreshOptions, Runnable clearSelectionCallback, JobScheduler jobScheduler, Localization localization) {
        this.setFeedListSupplier(feedListSupplier);
        this.setFeedRefreshOptions(feedRefreshOptions);
        this.setClearSelectionCallback(clearSelectionCallback);
        this.setJobScheduler(jobScheduler);
        this.setLocalization(localization);
    }

    @Override
    public void handle(ActionEvent event) {
        List<Feed> feedList = new ArrayList<>(this.getFeedListSupplier().get());
        if (!feedList.isEmpty()) {
            this.getJobScheduler().submitJob(new Job(this.getLocalization().refreshingFeeds(), progress -> {
                feedList.forEach(feed -> feed.getProcessors().add(this));
                try {
                    progress.updateProgress(0d, null);
                    for (int i = 0; i < feedList.size(); i++) {
                        progress.updateProgress((double)(i+1) / (double)feedList.size(), null);
                        this.handleRefreshFeed(feedList.get(i));
                        feedList.get(i).getProcessors().remove(this);
                    }
                    this.getClearSelectionCallback().run();
                } finally {
                    feedList.forEach(feed -> feed.getProcessors().remove(this));
                }
            }));
        }
    }

    private void handleRefreshFeed(Feed feed) throws Exception {
        FeedInputLoader feedInputLoader = new FeedInputLoader();
        FeedInput feedInput = feedInputLoader.loadFeedInputFromUrl(feed.getUrl().getValue());
        feed.refresh(feedInput, this.getFeedRefreshOptions().toArray(Feed.RefreshOption[]::new));
    }

    private Supplier<List<Feed>> getFeedListSupplier() {
        return this.feedListSupplier;
    }
    private void setFeedListSupplier(Supplier<List<Feed>> feedListSupplier) {
        this.feedListSupplier = feedListSupplier;
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

    private Localization getLocalization() {
        return this.localization;
    }
    private void setLocalization(Localization localization) {
        this.localization = localization;
    }

    private Set<Feed.RefreshOption> getFeedRefreshOptions() {
        return this.feedRefreshOptions;
    }
    private void setFeedRefreshOptions(Set<Feed.RefreshOption> feedRefreshOptions) {
        this.feedRefreshOptions = feedRefreshOptions;
    }

}
