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
package de.perdian.apps.podcastcentral.ui.modules.library_new.components.treetable;

import java.util.List;

import de.perdian.apps.podcastcentral.model.Episode;
import de.perdian.apps.podcastcentral.model.Feed;
import de.perdian.apps.podcastcentral.ui.modules.library_new.LibrarySelection;
import javafx.scene.control.TreeItem;

class LibraryTreeTableSelection implements LibrarySelection {

    private List<Feed> selectedFeeds = null;
    private List<Episode> selectedEpisodesDirectly = null;
    private List<Episode> selectedEpisodesConsolidated = null;

    LibraryTreeTableSelection(List<TreeItem<LibraryTreeItemValue>> selectedItems) {
        this.setSelectedFeeds(LibraryTreeTableSelectionHelper.collectSelectedFeeds(selectedItems));
        this.setSelectedEpisodesDirectly(LibraryTreeTableSelectionHelper.collectSelectedEpisodesDirectly(selectedItems));
        this.setSelectedEpisodesConsolidated(LibraryTreeTableSelectionHelper.collectSelectedEpisodesConsolidated(selectedItems));
    }

    @Override
    public List<Feed> getSelectedFeeds() {
        return this.selectedFeeds;
    }
    private void setSelectedFeeds(List<Feed> selectedFeeds) {
        this.selectedFeeds = selectedFeeds;
    }

    @Override
    public List<Episode> getSelectedEpisodesDirectly() {
        return this.selectedEpisodesDirectly;
    }
    private void setSelectedEpisodesDirectly(List<Episode> selectedEpisodesDirectly) {
        this.selectedEpisodesDirectly = selectedEpisodesDirectly;
    }

    @Override
    public List<Episode> getSelectedEpisodesConsolidated() {
        return this.selectedEpisodesConsolidated;
    }
    private void setSelectedEpisodesConsolidated(List<Episode> selectedEpisodesConsolidated) {
        this.selectedEpisodesConsolidated = selectedEpisodesConsolidated;
    }

}
