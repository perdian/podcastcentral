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

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import de.perdian.apps.podcastcentral.downloader.episodes.EpisodeDownloader;
import de.perdian.apps.podcastcentral.ui.modules.library.actions.CancelEpisodeDownloadsActionEventHandler;
import de.perdian.apps.podcastcentral.ui.support.backgroundtasks.BackgroundTaskExecutor;
import de.perdian.apps.podcastcentral.ui.support.localization.Localization;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.scene.control.Button;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.BorderPane;

class EpisodeDownloaderToolbarPane extends BorderPane {

    @SuppressWarnings("unchecked")
    public EpisodeDownloaderToolbarPane(EpisodeDownloader episodeDownloader, BackgroundTaskExecutor backgroundTaskExecutor, Localization localization) {

        Button cancelAllDownloadsButton = new Button(localization.cancelAllDownloads(), new FontAwesomeIconView(FontAwesomeIcon.STOP));
        cancelAllDownloadsButton.setOnAction(new CancelEpisodeDownloadsActionEventHandler(() -> FXCollections.concat(episodeDownloader.getScheduledEpisodes(), episodeDownloader.getDownloadingEpisodes()), episodeDownloader, backgroundTaskExecutor, localization));
        cancelAllDownloadsButton.disableProperty().bind(Bindings.isEmpty(episodeDownloader.getDownloadingEpisodes()));

        ToolBar buttonToolbar = new ToolBar();
        buttonToolbar.getItems().addAll(cancelAllDownloadsButton);
        this.setCenter(buttonToolbar);

    }

}
