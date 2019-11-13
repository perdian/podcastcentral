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

import java.io.File;

import de.perdian.apps.podcentral.model.Library;
import de.perdian.apps.podcentral.ui.support.backgroundtasks.BackgroundTaskExecutor;
import de.perdian.apps.podcentral.ui.support.localization.Localization;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;

public class ImportFeedCollectionIntoLibraryActionEventHandler implements EventHandler<ActionEvent> {

    private Node parent = null;
    private Library library = null;
    private BackgroundTaskExecutor backgroundTaskExecutor = null;
    private Localization localization = null;
    private File initialDirectory = null;

    public ImportFeedCollectionIntoLibraryActionEventHandler(Node parent, Library library, BackgroundTaskExecutor backgroundTaskExecutor, Localization localization) {
        this.setParent(parent);
        this.setLibrary(library);
        this.setBackgroundTaskExecutor(backgroundTaskExecutor);
        this.setLocalization(localization);
    }

    @Override
    public void handle(ActionEvent event) {
        throw new UnsupportedOperationException();
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
