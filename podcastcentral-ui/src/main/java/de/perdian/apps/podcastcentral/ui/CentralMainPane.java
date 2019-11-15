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
package de.perdian.apps.podcastcentral.ui;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import de.perdian.apps.podcastcentral.ui.modules.downloader.EpisodeDownloaderPane;
import de.perdian.apps.podcastcentral.ui.modules.library.LibraryPane;
import de.perdian.apps.podcastcentral.ui.support.localization.Localization;
import javafx.geometry.Insets;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

class CentralMainPane extends GridPane {

    public CentralMainPane(Central central, Localization localization) {

de.perdian.apps.podcastcentral.ui.modules.library_new.LibraryPane newLibraryPane = new de.perdian.apps.podcastcentral.ui.modules.library_new.LibraryPane(central.getLibrary(), localization);
newLibraryPane.setPadding(new Insets(10, 8, 8, 8));
Tab newLibraryTab = new Tab(localization.library(), newLibraryPane);
newLibraryTab.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.PODCAST));
newLibraryTab.setClosable(false);

        LibraryPane libraryPane = new LibraryPane(central.getLibrary(), central.getEpisodeContentDownloader(), central.getBackgroundTaskExecutor(), localization);
        libraryPane.setPadding(new Insets(10, 8, 8, 8));
        Tab libraryTab = new Tab(localization.library(), libraryPane);
        libraryTab.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.PODCAST));
        libraryTab.setClosable(false);

        EpisodeDownloaderPane episodeDownloaderPane = new EpisodeDownloaderPane(central.getEpisodeContentDownloader(), central.getBackgroundTaskExecutor(), localization);
        episodeDownloaderPane.setPadding(new Insets(8, 8, 8, 8));
        Tab schedulerTab = new Tab(localization.downloads(), episodeDownloaderPane);
        schedulerTab.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.DOWNLOAD));
        schedulerTab.setClosable(false);

TabPane tabPane = new TabPane(newLibraryTab, libraryTab, schedulerTab);
//        TabPane tabPane = new TabPane(libraryTab, schedulerTab);
        GridPane.setHgrow(tabPane, Priority.ALWAYS);
        GridPane.setVgrow(tabPane, Priority.ALWAYS);

        CentralStatusBar statusPane = new CentralStatusBar(central.getEpisodeContentDownloader(), central.getBackgroundTaskExecutor(), localization);

        this.add(tabPane, 0, 0, 1, 1);
        this.add(statusPane, 0, 1, 1, 1);

    }

}