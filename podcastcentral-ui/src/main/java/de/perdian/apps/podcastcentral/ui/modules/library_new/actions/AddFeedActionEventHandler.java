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
package de.perdian.apps.podcastcentral.ui.modules.library_new.actions;

import java.util.function.Supplier;

import de.perdian.apps.podcastcentral.model.FeedInput;
import de.perdian.apps.podcastcentral.model.Library;
import de.perdian.apps.podcastcentral.ui.support.backgroundtasks.BackgroundTaskExecutor;
import de.perdian.apps.podcastcentral.ui.support.localization.Localization;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;

public class AddFeedActionEventHandler implements EventHandler<ActionEvent> {

    private Supplier<FeedInput> feedInputSupplier = null;
    private Library library = null;
    private BackgroundTaskExecutor backgroundTaskExecutor = null;
    private Localization localization = null;

    public AddFeedActionEventHandler(Supplier<FeedInput> feedInputSupplier, Library library, BackgroundTaskExecutor backgroundTaskExecutor, Localization localization) {
        this.setFeedInputSupplier(feedInputSupplier);
        this.setLibrary(library);
        this.setBackgroundTaskExecutor(backgroundTaskExecutor);
        this.setLocalization(localization);
    }

    @Override
    public void handle(ActionEvent event) {
        FeedInput feedInput = this.getFeedInputSupplier().get();
        if (feedInput != null) {
            this.getBackgroundTaskExecutor().execute(this.getLocalization().addingFeed(), () -> this.getLibrary().addFeed(feedInput));
        }
    }

    private Supplier<FeedInput> getFeedInputSupplier() {
        return this.feedInputSupplier;
    }
    private void setFeedInputSupplier(Supplier<FeedInput> feedInputSupplier) {
        this.feedInputSupplier = feedInputSupplier;
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
