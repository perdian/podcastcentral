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
package de.perdian.apps.podcentral.ui.modules.library;

import java.util.List;
import java.util.Map;

import de.perdian.apps.podcentral.model.Episode;
import de.perdian.apps.podcentral.model.Feed;
import de.perdian.apps.podcentral.model.Library;
import de.perdian.apps.podcentral.retrieval.FeedInputLoader;
import de.perdian.apps.podcentral.ui.localization.Localization;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;

class LibraryTreeTableContextMenu extends ContextMenu {

    LibraryTreeTableContextMenu(List<Feed> selectedFeeds, Map<Feed, List<Episode>> selectedEpisodes, Library library, FeedInputLoader feedInputLoader, Localization localization) {

        MenuItem deleteMenuItem = new MenuItem(localization.delete());
        if (selectedFeeds.isEmpty() && selectedEpisodes.isEmpty()) {
            deleteMenuItem.setDisable(true);
        } else {
            deleteMenuItem.setOnAction(event -> this.executeDeleteItems(selectedFeeds, selectedEpisodes, library));
        }
        this.getItems().add(deleteMenuItem);

    }

    private void executeDeleteItems(List<Feed> selectedFeeds, Map<Feed, List<Episode>> selectedEpisodes, Library library) {
    }

}
