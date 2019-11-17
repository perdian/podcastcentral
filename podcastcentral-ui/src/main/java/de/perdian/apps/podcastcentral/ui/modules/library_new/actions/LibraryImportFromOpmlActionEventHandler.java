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

import java.io.File;

import org.apache.commons.io.FileUtils;

import de.perdian.apps.podcastcentral.model.FeedCollection;
import de.perdian.apps.podcastcentral.model.FeedCollectionItem;
import de.perdian.apps.podcastcentral.model.FeedInput;
import de.perdian.apps.podcastcentral.model.Library;
import de.perdian.apps.podcastcentral.sources.feedcollections.FeedCollectionLoader;
import de.perdian.apps.podcastcentral.sources.feeds.FeedInputLoader;
import de.perdian.apps.podcastcentral.ui.support.backgroundtasks.BackgroundTaskExecutor;
import de.perdian.apps.podcastcentral.ui.support.localization.Localization;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.stage.FileChooser;

public class LibraryImportFromOpmlActionEventHandler implements EventHandler<ActionEvent> {

    private Node parent = null;
    private Library library = null;
    private BackgroundTaskExecutor backgroundTaskExecutor = null;
    private Localization localization = null;
    private File initialDirectory = null;

    public LibraryImportFromOpmlActionEventHandler(Node parent, Library library, BackgroundTaskExecutor backgroundTaskExecutor, Localization localization) {
        this.setParent(parent);
        this.setLibrary(library);
        this.setBackgroundTaskExecutor(backgroundTaskExecutor);
        this.setLocalization(localization);
    }

    @Override
    public void handle(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(this.getInitialDirectory());
        fileChooser.setTitle(this.getLocalization().selectTargetFile());
        fileChooser.setInitialFileName("podcasts.opml");
        File sourceFile = fileChooser.showOpenDialog(this.getParent().getScene().getWindow());
        this.setInitialDirectory(sourceFile.getParentFile());
        if (sourceFile.exists()) {
            this.getBackgroundTaskExecutor().execute(this.getLocalization().importingFeedsFromOpmlFile(), progress -> {
                FeedCollection feedCollection = FeedCollectionLoader.loadFeedCollection(FileUtils.readFileToByteArray(sourceFile), "text/x-opml");
                if (feedCollection != null) {
                    for (int i=0; i < feedCollection.getItems().size(); i++) {
                        progress.updateProgress((double)i / feedCollection.getItems().size(), null);
                        FeedCollectionItem feedCollectionItem = feedCollection.getItems().get(i);
                        FeedInput feedInput = FeedInputLoader.loadFeedInputFromUrl(feedCollectionItem.getFeedUrl().getValue());
                        this.getLibrary().addFeed(feedInput);
                    }
                }
            });
        }
    }

    private Node getParent() {
        return this.parent;
    }
    private void setParent(Node parent) {
        this.parent = parent;
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

    private File getInitialDirectory() {
        return this.initialDirectory;
    }
    private void setInitialDirectory(File initialDirectory) {
        this.initialDirectory = initialDirectory;
    }

}
