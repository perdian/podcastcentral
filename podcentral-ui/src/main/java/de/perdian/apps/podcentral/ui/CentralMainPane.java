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

import de.perdian.apps.podcentral.ui.components.library.LibraryPane;
import de.perdian.apps.podcentral.ui.localization.Localization;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

class CentralMainPane extends GridPane {

    public CentralMainPane(Central central, Localization localization) {

        BorderPane bottomPane = new BorderPane(new Label("BOTTOM PANE GOES HERE"));
        bottomPane.setPadding(new Insets(0, 0, 0, 0));
        GridPane.setHgrow(bottomPane, Priority.ALWAYS);

        LibraryPane libraryPane = new LibraryPane(central.getLibrary(), localization);
        libraryPane.setPadding(new Insets(0, 0, 0, 0));
        GridPane.setVgrow(libraryPane, Priority.ALWAYS);
        GridPane.setHgrow(libraryPane, Priority.ALWAYS);

        this.add(libraryPane, 0, 0, 1, 1);
        this.add(bottomPane, 0, 1, 1, 1);

    }

}
