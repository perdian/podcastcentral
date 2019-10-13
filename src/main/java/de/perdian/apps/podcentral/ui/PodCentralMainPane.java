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

import de.perdian.apps.podcentral.preferences.Preferences;
import de.perdian.apps.podcentral.ui.localization.Localization;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

public class PodCentralMainPane extends GridPane {

    private BorderPane centerPane = null;

    public PodCentralMainPane(Preferences preferences, Localization localization) {

        BorderPane topPane = new BorderPane(new Label("TOP PANE GOES HERE"));
        topPane.setPadding(new Insets(8, 0, 8, 0));
        GridPane.setHgrow(topPane, Priority.ALWAYS);

        BorderPane bottomPane = new BorderPane(new Label("BOTTOM PANE GOES HERE"));
        bottomPane.setPadding(new Insets(8, 0, 8, 0));
        GridPane.setHgrow(bottomPane, Priority.ALWAYS);

        Label libraryLoadingLabel = new Label(localization.loadingLibrary());
        BorderPane libraryLoadingPane = new BorderPane(libraryLoadingLabel);
        BorderPane centerPane = new BorderPane(libraryLoadingPane);
        centerPane.setPadding(new Insets(8, 8, 8, 8));
        GridPane.setVgrow(centerPane, Priority.ALWAYS);
        GridPane.setHgrow(centerPane, Priority.ALWAYS);
        this.setCenterPane(centerPane);

        this.add(topPane, 0, 0, 1, 1);
        this.add(new Separator(), 0, 1, 1, 1);
        this.add(centerPane, 0, 2, 1, 1);
        this.add(new Separator(), 0, 3, 1, 1);
        this.add(bottomPane, 0, 4, 1, 1);

    }

    public void setCenter(Node node) {
        this.getCenterPane().setCenter(node);
    }

    private BorderPane getCenterPane() {
        return this.centerPane;
    }
    private void setCenterPane(BorderPane centerPane) {
        this.centerPane = centerPane;
    }

}
