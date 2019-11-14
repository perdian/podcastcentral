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

import org.apache.commons.lang3.StringUtils;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import de.perdian.apps.podcastcentral.downloader.episodes.EpisodeDownloader;
import de.perdian.apps.podcastcentral.model.Episode;
import de.perdian.apps.podcastcentral.ui.support.backgroundtasks.BackgroundTaskExecutor;
import de.perdian.apps.podcastcentral.ui.support.fx.components.PreviewImagePane;
import de.perdian.apps.podcastcentral.ui.support.fx.components.ProgressPane;
import de.perdian.apps.podcastcentral.ui.support.localization.Localization;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

public class EpisodeDownloaderPane extends GridPane {

    public EpisodeDownloaderPane(EpisodeDownloader episodeDownloader, BackgroundTaskExecutor backgroundTaskExecutor, Localization localization) {

        EpisodeDownloaderToolbar toolbarPane = new EpisodeDownloaderToolbar(episodeDownloader, backgroundTaskExecutor, localization);
        GridPane.setMargin(toolbarPane, new Insets(0, 0, 8, 0));
        GridPane.setHgrow(toolbarPane, Priority.ALWAYS);

        EpisodeDownloaderItemsPane scheduledDownloadsPane = new EpisodeDownloaderItemsPane(episodeDownloader.getScheduledEpisodes(), episode -> new ScheduledEpisodeItemPane(episode, episodeDownloader, localization));
        TitledPane scheduledDownloadsTitledPane = new TitledPane(localization.scheduledDownloads(), scheduledDownloadsPane);
        scheduledDownloadsTitledPane.setCollapsible(false);
        scheduledDownloadsTitledPane.setMaxHeight(Double.MAX_VALUE);
        GridPane.setHgrow(scheduledDownloadsTitledPane, Priority.ALWAYS);
        GridPane.setVgrow(scheduledDownloadsTitledPane, Priority.ALWAYS);
        GridPane.setMargin(scheduledDownloadsTitledPane, new Insets(0, 0, 4, 0));

        EpisodeDownloaderSettingsPane settingsPane = new EpisodeDownloaderSettingsPane(episodeDownloader, localization);
        settingsPane.setPadding(new Insets(8, 8, 8, 8));
        TitledPane settingsTitledPane = new TitledPane(localization.settings(), settingsPane);
        settingsTitledPane.setCollapsible(false);
        GridPane.setHgrow(settingsTitledPane, Priority.ALWAYS);
        GridPane.setMargin(settingsTitledPane, new Insets(4, 0, 0, 0));

        EpisodeDownloaderItemsPane activeDownloadsPane = new EpisodeDownloaderItemsPane(episodeDownloader.getDownloadingEpisodes(), episode -> new ActiveEpisodeItemPane(episode, episodeDownloader, localization));
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
        this.add(settingsTitledPane, 0, 2, 1, 1);
        this.add(activeDownloadsTitledPane, 1, 1, 1, 2);

    }

    static class ScheduledEpisodeItemPane extends GridPane implements EpisodeDownloaderItemPane {

        ScheduledEpisodeItemPane(Episode episode, EpisodeDownloader episodeDownloader, Localization localization) {

            String imageUrl = StringUtils.defaultIfEmpty(episode.getImageUrl().getValue(), episode.getFeed().getImageUrl().getValue());
            PreviewImagePane imagePane = new PreviewImagePane(imageUrl, 90, 90, localization);
            GridPane.setMargin(imagePane, new Insets(0, 5, 0, 0));

            Label feedTitleLabel = new Label(episode.getFeed().getTitle().getValue());
            GridPane.setMargin(feedTitleLabel, new Insets(0, 0, 1, 0));

            Label episodeTitleLabel = new Label(episode.getTitle().getValue());
            episodeTitleLabel.setAlignment(Pos.TOP_LEFT);
            episodeTitleLabel.setMaxHeight(Double.MAX_VALUE);
            GridPane.setMargin(episodeTitleLabel, new Insets(0, 0, 5, 0));
            GridPane.setVgrow(episodeTitleLabel, Priority.ALWAYS);

            Button forceStartButton = new Button(null, new FontAwesomeIconView(FontAwesomeIcon.PLAY));
            forceStartButton.setMinHeight(30);
            forceStartButton.setMaxWidth(Double.MAX_VALUE);
            forceStartButton.setOnAction(action -> {
                forceStartButton.setDisable(true);
                episodeDownloader.forceDownload(episode);
            });
            GridPane.setHgrow(forceStartButton, Priority.ALWAYS);
            GridPane.setMargin(forceStartButton, new Insets(0, 2, 0, 0));

            Button cancelButton = new Button(null, new FontAwesomeIconView(FontAwesomeIcon.STOP));
            cancelButton.setMinHeight(30);
            cancelButton.setMaxWidth(Double.MAX_VALUE);
            cancelButton.setOnAction(action -> {
                cancelButton.setDisable(true);
                episodeDownloader.cancelDownload(episode);
            });
            GridPane.setHgrow(cancelButton, Priority.ALWAYS);
            GridPane.setMargin(cancelButton, new Insets(0, 0, 0, 2));

            GridPane buttonPane = new GridPane();
            ColumnConstraints buttenLeftColumnConstraints = new ColumnConstraints();
            buttenLeftColumnConstraints.setPercentWidth(50);
            ColumnConstraints buttonRightColumnConstraints = new ColumnConstraints();
            buttonRightColumnConstraints.setPercentWidth(50);
            buttonPane.getColumnConstraints().addAll(buttenLeftColumnConstraints, buttonRightColumnConstraints);
            buttonPane.add(forceStartButton, 0, 0, 1, 1);
            buttonPane.add(cancelButton, 1, 0, 1, 1);
            GridPane.setHgrow(buttonPane, Priority.ALWAYS);

            this.setPadding(new Insets(2, 4, 2, 4));
            this.add(imagePane, 0, 0, 1, 3);
            this.add(feedTitleLabel, 1, 0, 2, 1);
            this.add(episodeTitleLabel, 1, 1, 2, 1);
            this.add(buttonPane, 1, 2, 1, 1);

        }

    }

    static class ActiveEpisodeItemPane extends GridPane implements EpisodeDownloaderItemPane {

        private ProgressPane progressPane = null;
        private Localization localization = null;

        ActiveEpisodeItemPane(Episode episode, EpisodeDownloader episodeDownloader, Localization localization) {

            String imageUrl = StringUtils.defaultIfEmpty(episode.getImageUrl().getValue(), episode.getFeed().getImageUrl().getValue());
            PreviewImagePane imagePane = new PreviewImagePane(imageUrl, 90, 90, localization);

            ProgressPane progressPane = new ProgressPane();
            progressPane.setTitle(episode.getTitle().getValue() + " (" + episode.getFeed().getTitle().getValue() + ")");
            GridPane.setHgrow(progressPane, Priority.ALWAYS);
            this.setProgressPane(progressPane);

            Button cancelButton = new Button(localization.cancel(), new FontAwesomeIconView(FontAwesomeIcon.STOP));
            cancelButton.setMaxWidth(Double.MAX_VALUE);
            cancelButton.setMaxHeight(Double.MAX_VALUE);
            cancelButton.setOnAction(action -> {
                cancelButton.setDisable(true);
                episodeDownloader.cancelDownload(episode);
            });
            GridPane.setHgrow(cancelButton, Priority.ALWAYS);
            GridPane.setVgrow(cancelButton, Priority.ALWAYS);

            GridPane buttonPane = new GridPane();
            buttonPane.setVgap(2);
            buttonPane.setPrefWidth(90);
            buttonPane.setMinWidth(90);
            buttonPane.setMaxWidth(90);
            buttonPane.add(cancelButton, 0, 0, 1, 1);

            this.add(imagePane, 0, 0, 1, 1);
            this.add(progressPane, 1, 0, 1, 1);
            this.add(buttonPane, 2, 0, 1, 1);
            this.setHgap(4);
            this.setMaxWidth(Double.MAX_VALUE);
            this.setPadding(new Insets(2, 8, 2, 8));
            this.setLocalization(localization);

        }

        @Override
        public void updateProgress(Double progress, Episode episode) {
            this.getProgressPane().updateProgress(progress, this.computeProgressMessage(episode));
        }

        private String computeProgressMessage(Episode episode) {
            Long bytesWritten = episode.getDownloadedBytes().getValue();
            Long bytesTotal = episode.getContentSize().getValue();
            if (bytesWritten == null || bytesTotal == null || bytesTotal.longValue() <= 0) {
                return null;
            } else {
                return this.getLocalization().bytesOfBytesTransfered(bytesWritten.longValue(), bytesTotal.longValue());
            }
        }

        private ProgressPane getProgressPane() {
            return this.progressPane;
        }
        private void setProgressPane(ProgressPane progressPane) {
            this.progressPane = progressPane;
        }

        private Localization getLocalization() {
            return this.localization;
        }
        private void setLocalization(Localization localization) {
            this.localization = localization;
        }

    }

}
