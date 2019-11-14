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

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

import de.perdian.apps.podcastcentral.model.Library;
import de.perdian.apps.podcastcentral.ui.support.backgroundtasks.BackgroundTaskExecutor;
import de.perdian.apps.podcastcentral.ui.support.localization.Localization;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.stage.FileChooser;

public class ExportLibraryAsOpmlActionEventHandler implements EventHandler<ActionEvent> {

    private Node parent = null;
    private Library library = null;
    private BackgroundTaskExecutor backgroundTaskExecutor = null;
    private Localization localization = null;
    private File initialDirectory = null;

    public ExportLibraryAsOpmlActionEventHandler(Node parent, Library library, BackgroundTaskExecutor backgroundTaskExecutor, Localization localization) {
        this.setParent(parent);
        this.setLibrary(library);
        this.setBackgroundTaskExecutor(backgroundTaskExecutor);
        this.setLocalization(localization);
        this.setInitialDirectory(new File(System.getProperty("user.home")));
    }

    @Override
    public void handle(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(this.getInitialDirectory());
        fileChooser.setTitle(this.getLocalization().selectTargetFile());
        fileChooser.setInitialFileName("podcasts.opml");
        File targetFile = fileChooser.showSaveDialog(this.getParent().getScene().getWindow());
        if (targetFile != null) {
            this.setInitialDirectory(targetFile.getParentFile());
            this.getBackgroundTaskExecutor().execute(this.getLocalization().exportingLibraryAsXml(), progress -> {
                if (!targetFile.getParentFile().exists()) {
                    targetFile.getParentFile().mkdirs();
                }
                try (OutputStream fileOutputStream = new BufferedOutputStream(new FileOutputStream(targetFile))) {
                    fileOutputStream.write(this.getLibrary().toFeedCollection().toXmlString().getBytes("UTF-8"));
                    fileOutputStream.flush();
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
