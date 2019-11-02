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
import de.perdian.apps.podcentral.ui.modules.scheduler.SchedulerPane;
import javafx.geometry.Insets;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;

class CentralMainPane extends BorderPane {

    public CentralMainPane(Central central, Localization localization) {

        LibraryPane libraryPane = new LibraryPane(central.getLibrary(), central.getFeedInputLoader(), localization);
        libraryPane.setPadding(new Insets(8, 8, 8, 8));
        Tab libraryTab = new Tab(localization.library(), libraryPane);
        libraryTab.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.PODCAST));
        libraryTab.setClosable(false);

        SchedulerPane schedulerPane = new SchedulerPane(central.getScheduler(), localization);
        schedulerPane.setPadding(new Insets(8, 8, 8, 8));
        Tab schedulerTab = new Tab(localization.scheduler(), schedulerPane);
        schedulerTab.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.DOWNLOAD));
        schedulerTab.setClosable(false);

        TabPane tabPane = new TabPane(libraryTab, schedulerTab);
        this.setCenter(tabPane);

    }

}
