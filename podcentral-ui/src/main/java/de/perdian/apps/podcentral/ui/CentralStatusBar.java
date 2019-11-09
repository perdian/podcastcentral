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

import org.controlsfx.control.StatusBar;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import de.perdian.apps.podcentral.downloader.episodes.EpisodeContentDownloader;
import de.perdian.apps.podcentral.ui.localization.Localization;
import de.perdian.apps.podcentral.ui.support.tasks.BackgroundTaskExecutor;
import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.geometry.Insets;
import javafx.scene.control.Label;

class CentralStatusBar extends StatusBar {

    CentralStatusBar(BackgroundTaskExecutor backgroundTaskExecutor, EpisodeContentDownloader episodeContentDowloader, Localization localization) {

        Label episodeContentDownloaderLabel = new Label(localization.noDownloadsActive());
        episodeContentDownloaderLabel.setPadding(new Insets(0, 4, 0, 16));
        episodeContentDownloaderLabel.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.CHECK));
        episodeContentDownloaderLabel.setMaxHeight(Double.MAX_VALUE);
        episodeContentDowloader.getDownloadingEpisodes().addListener((ListChangeListener.Change<?> change) -> {
            Platform.runLater(() -> {
                if (change.getList().isEmpty()) {
                    episodeContentDownloaderLabel.setText(localization.noDownloadsActive());
                    episodeContentDownloaderLabel.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.CHECK));
                } else {
                    episodeContentDownloaderLabel.setText(localization.downloadsActive(change.getList().size()));
                    episodeContentDownloaderLabel.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.DOWNLOAD));
                }
            });
        });

        this.setProgress(0);
        this.setText("");
        this.getRightItems().add(episodeContentDownloaderLabel);

        backgroundTaskExecutor.getProgress().addListener((o, oldValue, newValue) -> Platform.runLater(() -> this.setProgress(newValue == null ? 0 : newValue.doubleValue())));
        backgroundTaskExecutor.getText().addListener((o, oldValue, newValue) -> Platform.runLater(() -> this.setText(newValue)));

    }

}
