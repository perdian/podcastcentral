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

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import de.perdian.apps.podcentral.ui.localization.Localization;
import de.perdian.apps.podcentral.ui.modules.library.LibraryPane;
import de.perdian.apps.podcentral.ui.modules.scheduler.DownloadSchedulerPane;
import de.perdian.apps.podcentral.ui.modules.scheduler.UiSchedulerPane;
import javafx.geometry.Insets;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

class CentralMainPane extends GridPane {

    public CentralMainPane(Central central, Localization localization) {

        LibraryPane libraryPane = new LibraryPane(central.getUiJobScheduler(), central.getLibrary(), localization);
        libraryPane.setPadding(new Insets(10, 8, 8, 8));
        Tab libraryTab = new Tab(localization.library(), libraryPane);
        libraryTab.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.PODCAST));
        libraryTab.setClosable(false);

        DownloadSchedulerPane downloadSchedulerPane = new DownloadSchedulerPane(central.getDownloadJobScheduler(), localization);
        downloadSchedulerPane.setPadding(new Insets(8, 8, 8, 8));
        Tab schedulerTab = new Tab(localization.downloads(), downloadSchedulerPane);
        schedulerTab.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.DOWNLOAD));
        schedulerTab.setClosable(false);

        TabPane tabPane = new TabPane(libraryTab, schedulerTab);
        GridPane.setHgrow(tabPane, Priority.ALWAYS);
        GridPane.setVgrow(tabPane, Priority.ALWAYS);

        UiSchedulerPane uiSchedulerPane = new UiSchedulerPane(central.getUiJobScheduler(), localization);
        uiSchedulerPane.setPadding(new Insets(0, 8, 8, 8));

        this.add(tabPane, 0, 0, 1, 1);
        this.add(uiSchedulerPane, 0, 1, 1, 1);

    }

}
