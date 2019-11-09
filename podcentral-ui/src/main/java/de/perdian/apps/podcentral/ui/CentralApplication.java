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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import de.perdian.apps.podcentral.ui.support.localization.Localization;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;

public class CentralApplication extends Application {

    private static final Logger log = LoggerFactory.getLogger(CentralApplication.class);

    @Override
    public void start(Stage primaryStage) throws Exception {

        primaryStage.getIcons().add(new Image(CentralApplication.class.getClassLoader().getResourceAsStream("META-INF/icons/podcast.png")));

        Localization localization = new Localization() {};

        Scene loaderScene = new Scene(this.createLoaderPane(localization));
        loaderScene.getStylesheets().add("META-INF/stylesheets/podcentral.css");
        Stage loaderStage = new Stage();
        loaderStage.initOwner(primaryStage);
        loaderStage.setTitle(localization.podCentral());
        loaderStage.setOnCloseRequest(event -> System.exit(0));
        loaderStage.resizableProperty().setValue(false);
        loaderStage.setScene(loaderScene);
        loaderStage.sizeToScene();
        loaderStage.show();

        Thread loaderThread = new Thread(() -> {
            try {

                log.info("Creating central");
                Central central = new Central(localization);
                CentralMainPane centralMainPane = new CentralMainPane(central, localization);
                Scene centralScene = new Scene(centralMainPane);
                centralScene.getStylesheets().add("META-INF/stylesheets/podcentral.css");

                log.info("Creating main JavaFX window");
                Platform.runLater(() -> {
                    primaryStage.setMinWidth(800);
                    primaryStage.setMinHeight(600);
                    primaryStage.setTitle(localization.podCentral());
                    primaryStage.setOnCloseRequest(event -> System.exit(0));
                    primaryStage.setScene(centralScene);
                    primaryStage.setWidth(1200);
                    primaryStage.setHeight(800);
                    primaryStage.show();
                });

            } finally {
                Platform.runLater(() -> loaderStage.close());
            }
        });
        loaderThread.setName(CentralApplication.class.getSimpleName() + "Loader");
        loaderThread.start();

    }

    private Pane createLoaderPane(Localization localization) {

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

        GridPane pane = new GridPane();
        pane.add(iconLabel, 0, 0, 1, 1);
        pane.add(loadingLabel, 0, 1, 1, 1);
        pane.add(progressBar, 0, 2, 1, 1);
        pane.setMinWidth(250);
        pane.setPadding(new Insets(32, 32, 32, 32));
        return pane;

    }

}
