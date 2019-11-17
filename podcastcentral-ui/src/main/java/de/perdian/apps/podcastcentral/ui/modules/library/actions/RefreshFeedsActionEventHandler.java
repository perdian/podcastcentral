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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

import de.perdian.apps.podcastcentral.model.Feed;
import de.perdian.apps.podcastcentral.model.FeedInput;
import de.perdian.apps.podcastcentral.sources.feeds.FeedInputLoader;
import de.perdian.apps.podcastcentral.ui.support.backgroundtasks.BackgroundTaskExecutor;
import de.perdian.apps.podcastcentral.ui.support.localization.Localization;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;

public class RefreshFeedsActionEventHandler implements EventHandler<ActionEvent> {

    private Supplier<List<Feed>> feedListSupplier = null;
    private BackgroundTaskExecutor backgroundTaskExecutor = null;
    private Localization localization = null;
    private Set<Feed.RefreshOption> feedRefreshOptions = Collections.emptySet();

    public RefreshFeedsActionEventHandler(Supplier<List<Feed>> feedListSupplier, Set<Feed.RefreshOption> feedRefreshOptions, BackgroundTaskExecutor backgroundTaskExecutor, Localization localization) {
        this.setFeedListSupplier(feedListSupplier);
        this.setFeedRefreshOptions(feedRefreshOptions);
        this.setBackgroundTaskExecutor(backgroundTaskExecutor);
        this.setLocalization(localization);
    }

    @Override
    public void handle(ActionEvent event) {
        List<Feed> feedList = new ArrayList<>(this.getFeedListSupplier().get());
        if (!feedList.isEmpty()) {
            this.getBackgroundTaskExecutor().execute(this.getLocalization().refreshingFeeds(), progress -> {
                progress.updateProgress(0d, null);
                for (int i = 0; i < feedList.size(); i++) {
                    progress.updateProgress((double)(i+1) / (double)feedList.size(), this.getLocalization().refreshingFeed(feedList.get(i).getTitle().getValue()));
                    this.handleRefreshFeed(feedList.get(i));
                }
            });
        }
    }

    private void handleRefreshFeed(Feed feed) throws Exception {
        FeedInput feedInput = FeedInputLoader.loadFeedInputFromUrl(feed.getUrl().getValue());
        feed.refresh(feedInput, this.getFeedRefreshOptions().toArray(Feed.RefreshOption[]::new));
    }

    private Supplier<List<Feed>> getFeedListSupplier() {
        return this.feedListSupplier;
    }
    private void setFeedListSupplier(Supplier<List<Feed>> feedListSupplier) {
        this.feedListSupplier = feedListSupplier;
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

    private Set<Feed.RefreshOption> getFeedRefreshOptions() {
        return this.feedRefreshOptions;
    }
    private void setFeedRefreshOptions(Set<Feed.RefreshOption> feedRefreshOptions) {
        this.feedRefreshOptions = feedRefreshOptions;
    }

}
