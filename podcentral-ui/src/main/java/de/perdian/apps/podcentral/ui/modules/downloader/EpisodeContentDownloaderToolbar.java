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
package de.perdian.apps.podcentral.ui.modules.downloader;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import de.perdian.apps.podcentral.downloader.episodes.EpisodeContentDownloader;
import de.perdian.apps.podcentral.ui.localization.Localization;
import de.perdian.apps.podcentral.ui.modules.episodes.CancelDownloadEpisodesActionEventHandler;
import de.perdian.apps.podcentral.ui.support.tasks.BackgroundTaskExecutor;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.layout.BorderPane;

class EpisodeContentDownloaderToolbar extends BorderPane {

    @SuppressWarnings("unchecked")
    public EpisodeContentDownloaderToolbar(BackgroundTaskExecutor backgroundTaskExecutor, EpisodeContentDownloader episodeContentDownloader, Localization localization) {

        Button cancelAllDownloadsButton = new Button(localization.cancelAllDownloads(), new FontAwesomeIconView(FontAwesomeIcon.STOP));
        cancelAllDownloadsButton.setOnAction(new CancelDownloadEpisodesActionEventHandler(() -> FXCollections.concat(episodeContentDownloader.getScheduledEpisodes(), episodeContentDownloader.getDownloadingEpisodes()), backgroundTaskExecutor, episodeContentDownloader, localization));
        cancelAllDownloadsButton.disableProperty().bind(Bindings.isEmpty(episodeContentDownloader.getDownloadingEpisodes()));
        ButtonBar.setButtonData(cancelAllDownloadsButton, ButtonData.LEFT);

        ButtonBar buttonBar = new ButtonBar();
        buttonBar.getButtons().addAll(cancelAllDownloadsButton);

        this.setLeft(buttonBar);

    }

}
