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
package de.perdian.apps.podcentral.ui;

import java.util.ServiceLoader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.perdian.apps.podcentral.core.model.Library;
import de.perdian.apps.podcentral.core.model.LibraryFactory;
import de.perdian.apps.podcentral.core.tasks.TaskExecutor;
import de.perdian.apps.podcentral.preferences.Preferences;
import de.perdian.apps.podcentral.preferences.PreferencesFactory;
import de.perdian.apps.podcentral.ui.components.library.LibraryPane;
import de.perdian.apps.podcentral.ui.localization.Localization;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class PodCentralApplication extends Application {

    private static final Logger log = LoggerFactory.getLogger(PodCentralApplication.class);

    @Override
    public void start(Stage primaryStage) throws Exception {

        log.info("Loading preferences");
        Preferences preferences = PreferencesFactory.loadPreferences();

        log.info("Creating JavaFX UI");
        Localization localization = new Localization() {};
        TaskExecutor taskExecutor = new TaskExecutor();
        PodCentralMainPane mainPane = new PodCentralMainPane(preferences, localization);

        log.info("Opening JavaFX stage");
//        primaryStage.getIcons().add(new Image(this.getClass().getClassLoader().getResourceAsStream("icons/256/application.png")));
        primaryStage.setScene(new Scene(mainPane));
        primaryStage.setOnCloseRequest(event -> System.exit(0));
        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(600);
        primaryStage.setTitle("TODO: SET TITLE");
        primaryStage.setWidth(1200);
        primaryStage.setHeight(800);
        primaryStage.show();
        log.info("JavaFX stage setup completed");

        log.info("Loading library");
        taskExecutor.submit(() -> {
            LibraryFactory libraryFactory = ServiceLoader.load(LibraryFactory.class).findFirst().orElseThrow(() -> new IllegalArgumentException("Cannot find ServiceLoader for class: " + LibraryFactory.class.getName()));
            Library library = libraryFactory.createLibrary();
            log.info("Loaded library: {}", library);
            LibraryPane libraryPane = new LibraryPane(library, localization);
            Platform.runLater(() -> mainPane.setCenter(libraryPane));
        });

    }

}
