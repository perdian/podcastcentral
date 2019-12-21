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
package de.perdian.apps.podcastcentral.ui.modules.library.components.toolbar;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import de.perdian.apps.podcastcentral.downloader.episodes.EpisodeDownloader;
import de.perdian.apps.podcastcentral.model.Episode;
import de.perdian.apps.podcastcentral.model.EpisodeDownloadState;
import de.perdian.apps.podcastcentral.model.Library;
import de.perdian.apps.podcastcentral.ui.modules.library.actions.AddFeedActionEventHandler;
import de.perdian.apps.podcastcentral.ui.modules.library.actions.DownloadEpisodesActionEventHandler;
import de.perdian.apps.podcastcentral.ui.modules.library.actions.LibraryExportAsOpmlActionEventHandler;
import de.perdian.apps.podcastcentral.ui.modules.library.actions.LibraryImportFromOpmlActionEventHandler;
import de.perdian.apps.podcastcentral.ui.modules.library.actions.RefreshFeedsActionEventHandler;
import de.perdian.apps.podcastcentral.ui.modules.library.components.feeds.AddFeedDialog;
import de.perdian.apps.podcastcentral.ui.support.backgroundtasks.BackgroundTaskExecutor;
import de.perdian.apps.podcastcentral.ui.support.localization.Localization;
import javafx.beans.binding.Bindings;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

public class LibraryToolbarPane extends BorderPane {

    public LibraryToolbarPane(Library library, EpisodeDownloader episodeDownloader, BackgroundTaskExecutor backgroundTaskExecutor, Localization localization) {

        Button addFeedButton = new Button(localization.addFeed(), new FontAwesomeIconView(FontAwesomeIcon.PLUS));
        addFeedButton.setOnAction(new AddFeedActionEventHandler(() -> AddFeedDialog.requestFeedInput(addFeedButton, localization), library, backgroundTaskExecutor, localization));

        Button downloadNewEpisodesButton = new Button(localization.downloadNewEpisodes(), new FontAwesomeIconView(FontAwesomeIcon.DOWNLOAD));
        downloadNewEpisodesButton.disableProperty().bind(Bindings.isEmpty(library.getFeeds()));
        downloadNewEpisodesButton.setOnAction(new DownloadEpisodesActionEventHandler(() -> this.loadNewEpisodes(library), episodeDownloader, backgroundTaskExecutor, localization));

        Button refreshAllFeedsButton = new Button(localization.refreshAllFeeds(), new FontAwesomeIconView(FontAwesomeIcon.REFRESH));
        refreshAllFeedsButton.disableProperty().bind(Bindings.isEmpty(library.getFeeds()));
        refreshAllFeedsButton.setOnAction(new RefreshFeedsActionEventHandler(library::getFeeds, Collections.emptySet(), backgroundTaskExecutor, localization));

        HBox separatorPane = new HBox();
        HBox.setHgrow(separatorPane, Priority.ALWAYS);

        Button importButton = new Button(localization.importOpml(), new FontAwesomeIconView(FontAwesomeIcon.FILE));
        importButton.setOnAction(new LibraryImportFromOpmlActionEventHandler(this, library, backgroundTaskExecutor, localization));
        Button exportButton = new Button(localization.exportOpml(), new FontAwesomeIconView(FontAwesomeIcon.FILE));
        exportButton.setOnAction(new LibraryExportAsOpmlActionEventHandler(this, library, backgroundTaskExecutor, localization));

        ToolBar buttonToolbar = new ToolBar();
        buttonToolbar.getItems().addAll(addFeedButton);
        buttonToolbar.getItems().addAll(new Label(" "));
        buttonToolbar.getItems().addAll(downloadNewEpisodesButton, refreshAllFeedsButton);
        buttonToolbar.getItems().addAll(separatorPane);
        buttonToolbar.getItems().addAll(importButton, exportButton);
        this.setCenter(buttonToolbar);

    }

    private List<Episode> loadNewEpisodes(Library library) {
        return library.getFeeds().stream()
            .flatMap(stream -> stream.getEpisodes().stream())
            .filter(episode -> Boolean.FALSE.equals(episode.getRead().getValue()))
            .filter(episode -> List.of(EpisodeDownloadState.CANCELLED, EpisodeDownloadState.ERRORED, EpisodeDownloadState.MISSING, EpisodeDownloadState.NEW).contains(episode.getDownloadState().getValue()))
            .collect(Collectors.toList());
    }

}
