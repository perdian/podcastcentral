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
import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;

import de.perdian.apps.podcentral.jobscheduler.Job;
import de.perdian.apps.podcentral.jobscheduler.JobScheduler;
import de.perdian.apps.podcentral.model.FeedInput;
import de.perdian.apps.podcentral.model.FeedInputOptions;
import de.perdian.apps.podcentral.model.Library;
import de.perdian.apps.podcentral.retrieval.FeedInputLoader;
import de.perdian.apps.podcentral.ui.localization.Localization;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;

public class FeedRefreshEventHandler implements EventHandler<ActionEvent> {

    private Supplier<Collection<String>> feedUrlListSupplier = null;
    private Runnable clearSelectionCallback = null;
    private JobScheduler jobScheduler = null;
    private Library library = null;
    private Localization localization = null;
    private FeedInputOptions feedInputOptions = new FeedInputOptions();

    public FeedRefreshEventHandler(Supplier<Collection<String>> feedUrlListSupplier, Runnable clearSelectionCallback, JobScheduler jobScheduler, Library library, Localization localization) {
        this.setFeedUrlListSupplier(feedUrlListSupplier);
        this.setClearSelectionCallback(clearSelectionCallback);
        this.setJobScheduler(jobScheduler);
        this.setLibrary(library);
        this.setLocalization(localization);
    }

    @Override
    public void handle(ActionEvent event) {
        List<String> feedUrlList = new ArrayList<>(this.getFeedUrlListSupplier().get());
        if (!feedUrlList.isEmpty()) {
            this.getJobScheduler().submitJob(new Job(this.getLocalization().refreshingFeeds(), progress -> {
                progress.updateProgress(0d, null);
                for (int i=0; i < feedUrlList.size(); i++) {
                    progress.updateProgress((double)(i+1) / (double)feedUrlList.size(), null);
                    this.handleRefreshFeed(feedUrlList.get(i));
                }
                this.getClearSelectionCallback().run();
            }));
        }
    }

    private void handleRefreshFeed(String feedUrl) throws Exception {
        FeedInputLoader feedInputLoader = new FeedInputLoader();
        FeedInput feedInput = feedInputLoader.loadFeedInputFromUrl(feedUrl);
        this.getLibrary().updateFeedFromInput(feedInput, this.getFeedInputOptions());
    }

    private Supplier<Collection<String>> getFeedUrlListSupplier() {
        return this.feedUrlListSupplier;
    }
    private void setFeedUrlListSupplier(Supplier<Collection<String>> feedUrlListSupplier) {
        this.feedUrlListSupplier = feedUrlListSupplier;
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

    public FeedInputOptions getFeedInputOptions() {
        return this.feedInputOptions;
    }
    public void setFeedInputOptions(FeedInputOptions feedInputOptions) {
        this.feedInputOptions = feedInputOptions;
    }

}
