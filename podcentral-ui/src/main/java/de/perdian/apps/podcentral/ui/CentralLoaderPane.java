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

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import de.perdian.apps.podcentral.ui.localization.Localization;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

public class CentralLoaderPane extends GridPane {

    public CentralLoaderPane(Localization localization) {

        Label iconLabel = new Label();
        iconLabel.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.PODCAST, "64px"));
        iconLabel.setAlignment(Pos.CENTER);
        iconLabel.setMaxWidth(Double.MAX_VALUE);
        GridPane.setHgrow(iconLabel, Priority.ALWAYS);
        GridPane.setMargin(iconLabel, new Insets(0, 0, 24, 0));

        Label loadingLabel = new Label(localization.loadingApplicationData());
        loadingLabel.setAlignment(Pos.CENTER);
        loadingLabel.setMaxWidth(Double.MAX_VALUE);
        GridPane.setHgrow(loadingLabel, Priority.ALWAYS);
        GridPane.setMargin(loadingLabel, new Insets(0, 0, 2, 0));

        ProgressBar progressBar = new ProgressBar(ProgressBar.INDETERMINATE_PROGRESS);
        progressBar.setMinHeight(20);
        progressBar.setMaxWidth(Double.MAX_VALUE);
        GridPane.setHgrow(progressBar, Priority.ALWAYS);
        GridPane.setMargin(progressBar, new Insets(2, 0, 0, 0));

        this.add(iconLabel, 0, 0, 1, 1);
        this.add(loadingLabel, 0, 1, 1, 1);
        this.add(progressBar, 0, 2, 1, 1);
        this.setMinWidth(250);
        this.setPadding(new Insets(32, 32, 32, 32));

    }

}
