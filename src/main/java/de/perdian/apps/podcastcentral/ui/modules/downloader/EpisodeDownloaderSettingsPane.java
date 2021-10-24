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
package de.perdian.apps.podcastcentral.ui.modules.downloader;

import de.perdian.apps.podcastcentral.downloader.episodes.EpisodeDownloader;
import de.perdian.apps.podcastcentral.ui.support.localization.Localization;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class EpisodeDownloaderSettingsPane extends VBox {

    public EpisodeDownloaderSettingsPane(EpisodeDownloader episodeDownloader, Localization localization) {

        int maxProcessorValue = 64;
        Button minusButton = new Button("–");
        minusButton.setPrefWidth(20);
        minusButton.setOnAction(action -> episodeDownloader.getNumberOfDownloadSlots().setValue(episodeDownloader.getNumberOfDownloadSlots().getValue().intValue() - 1));
        minusButton.disableProperty().bind(episodeDownloader.getNumberOfDownloadSlots().lessThanOrEqualTo(1));
        Button plusButton = new Button("+");
        plusButton.setPrefWidth(20);
        plusButton.setOnAction(action -> episodeDownloader.getNumberOfDownloadSlots().setValue(episodeDownloader.getNumberOfDownloadSlots().getValue().intValue() + 1));
        plusButton.disableProperty().bind(episodeDownloader.getNumberOfDownloadSlots().greaterThanOrEqualTo(maxProcessorValue));
        TextField valueField = new TextField(String.valueOf(episodeDownloader.getNumberOfDownloadSlots().getValue()));
        valueField.textProperty().bind(episodeDownloader.getNumberOfDownloadSlots().asString());
        valueField.setAlignment(Pos.CENTER);
        valueField.setMaxWidth(50);
        valueField.setEditable(false);
        valueField.setFocusTraversable(false);
        Label infoLabel = new Label(localization.numberOfParallelDownloads());
        infoLabel.setMaxWidth(Double.MAX_VALUE);
        GridPane.setHgrow(infoLabel, Priority.ALWAYS);

        GridPane processorCountPane = new GridPane();
        processorCountPane.setHgap(2);
        processorCountPane.add(infoLabel, 1, 1);
        processorCountPane.add(minusButton, 2, 1);
        processorCountPane.add(valueField, 3, 1);
        processorCountPane.add(plusButton, 4, 1);
        this.getChildren().add(processorCountPane);

        this.setSpacing(4);

    }

}
