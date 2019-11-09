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

import de.perdian.apps.podcentral.downloader.episodes.EpisodeDownloader;
import de.perdian.apps.podcentral.ui.support.backgroundtasks.BackgroundTaskExecutor;
import de.perdian.apps.podcentral.ui.support.localization.Localization;
import javafx.geometry.Insets;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

public class EpisodeContentDownloaderPane extends GridPane {

    public EpisodeContentDownloaderPane(BackgroundTaskExecutor backgroundTaskExecutor, EpisodeDownloader episodeDownloader, Localization localization) {

        EpisodeContentDownloaderToolbar toolbarPane = new EpisodeContentDownloaderToolbar(backgroundTaskExecutor, episodeDownloader, localization);
        GridPane.setMargin(toolbarPane, new Insets(0, 0, 8, 0));
        GridPane.setHgrow(toolbarPane, Priority.ALWAYS);

        GridPane scheduledDownloadsPane = new GridPane();
        TitledPane scheduledDownloadsTitledPane = new TitledPane(localization.scheduledDownloads(), scheduledDownloadsPane);
        scheduledDownloadsTitledPane.setCollapsible(false);
        scheduledDownloadsTitledPane.setMaxHeight(Double.MAX_VALUE);
        GridPane.setHgrow(scheduledDownloadsTitledPane, Priority.ALWAYS);
        GridPane.setVgrow(scheduledDownloadsTitledPane, Priority.ALWAYS);

        GridPane activeDownloadsPane = new GridPane();
        TitledPane activeDownloadsTitledPane = new TitledPane(localization.activeDownloads(), activeDownloadsPane);
        activeDownloadsTitledPane.setCollapsible(false);
        activeDownloadsTitledPane.setMaxHeight(Double.MAX_VALUE);
        GridPane.setHgrow(activeDownloadsTitledPane, Priority.ALWAYS);
        GridPane.setVgrow(activeDownloadsTitledPane, Priority.ALWAYS);

        ColumnConstraints leftColumnConstraints = new ColumnConstraints();
        leftColumnConstraints.setPercentWidth(30);
        ColumnConstraints rightColumnConstraints = new ColumnConstraints();
        rightColumnConstraints.setPercentWidth(70 );
        this.getColumnConstraints().addAll(leftColumnConstraints, rightColumnConstraints);

        this.setHgap(8);
        this.add(toolbarPane, 0, 0, 2, 1);
        this.add(scheduledDownloadsTitledPane, 0, 1, 1, 1);
        this.add(activeDownloadsTitledPane, 1, 1, 1, 1);

    }

}
