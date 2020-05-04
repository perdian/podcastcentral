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
package de.perdian.apps.podcastcentral.ui.modules.library;

import de.perdian.apps.podcastcentral.downloader.episodes.EpisodeDownloader;
import de.perdian.apps.podcastcentral.model.Library;
import de.perdian.apps.podcastcentral.preferences.Preferences;
import de.perdian.apps.podcastcentral.ui.modules.library.components.toolbar.LibraryToolbarPane;
import de.perdian.apps.podcastcentral.ui.modules.library.components.treetable.LibraryTreeTableView;
import de.perdian.apps.podcastcentral.ui.support.backgroundtasks.BackgroundTaskExecutor;
import de.perdian.apps.podcastcentral.ui.support.localization.Localization;
import javafx.geometry.Insets;
import javafx.scene.layout.BorderPane;

public class LibraryPane extends BorderPane {

    public LibraryPane(Library library, EpisodeDownloader episodeDownloader, BackgroundTaskExecutor backgroundTaskExecutor, Preferences preferences, Localization localization) {

        this.setTop(new LibraryToolbarPane(library, episodeDownloader, backgroundTaskExecutor, localization));

        LibraryTreeTableView libraryTreeTableView = new LibraryTreeTableView(library, episodeDownloader, backgroundTaskExecutor, preferences, localization);
        BorderPane libraryTreeTableViewPane = new BorderPane(libraryTreeTableView);
        libraryTreeTableViewPane.setPadding(new Insets(8, 8, 8, 8));
        this.setCenter(libraryTreeTableViewPane);

    }

}
