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

import de.perdian.apps.podcentral.ui.localization.Localization;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class CentralApplication extends Application {

    private static final Logger log = LoggerFactory.getLogger(CentralApplication.class);

    @Override
    public void start(Stage primaryStage) throws Exception {

//        primaryStage.getIcons().add(new Image(this.getClass().getClassLoader().getResourceAsStream("icons/256/application.png")));

        Localization localization = new Localization() {};

        Stage loaderStage = new Stage();
        loaderStage.initOwner(primaryStage);
        loaderStage.setTitle(localization.podCentral());
        loaderStage.setOnCloseRequest(event -> System.exit(0));
        loaderStage.resizableProperty().setValue(false);
        loaderStage.setScene(new Scene(new CentralLoaderPane(localization)));
        loaderStage.sizeToScene();
        loaderStage.show();

        Thread loaderThread = new Thread(() -> {
            try {

                log.info("Creating central");
                Central central = CentralFactory.createCentral(localization);
                CentralMainPane centralMainPane = new CentralMainPane(central, localization);
                centralMainPane.setMinSize(800, 600);

                log.info("Creating main JavaFX window");
                Platform.runLater(() -> {
                    primaryStage.setTitle(localization.podCentral());
                    primaryStage.setOnCloseRequest(event -> System.exit(0));
                    primaryStage.setScene(new Scene(centralMainPane));
                    primaryStage.sizeToScene();
                    primaryStage.show();
                });

            } finally {
                Platform.runLater(() -> loaderStage.close());
            }
        });
        loaderThread.setName(CentralApplication.class.getSimpleName() + "Loader");
        loaderThread.start();

    }

}
