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

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import de.perdian.apps.podcastcentral.model.Episode;
import de.perdian.apps.podcastcentral.model.Feed;
import javafx.scene.control.TreeItem;

class LibraryTreeTableSelectionHelper {

    static List<Feed> collectSelectedFeeds(List<TreeItem<LibraryTreeItemValue>> selectedItems) {
        return selectedItems.stream()
            .filter(item -> item.getValue() instanceof LibraryTreeItemValue.FeedItemValue)
            .map(item -> (LibraryTreeItemValue.FeedItemValue)item.getValue())
            .map(value -> value.getFeed())
            .collect(Collectors.toList());
    }

    static List<Episode> collectSelectedEpisodesDirectly(List<TreeItem<LibraryTreeItemValue>> selectedItems) {
        return selectedItems.stream()
            .filter(item -> item.getValue() instanceof LibraryTreeItemValue.EpisodeItemValue)
            .map(item -> (LibraryTreeItemValue.EpisodeItemValue)item.getValue())
            .map(value -> value.getEpisode())
            .collect(Collectors.toList());
    }

    static List<Episode> collectSelectedEpisodesConsolidated(List<TreeItem<LibraryTreeItemValue>> selectedItems) {
        List<Feed> selectedFeeds = LibraryTreeTableSelectionHelper.collectSelectedFeeds(selectedItems);
        List<Episode> selectedEpisodes = LibraryTreeTableSelectionHelper.collectSelectedEpisodesDirectly(selectedItems);
        List<Episode> consolidatedEpisodes = new ArrayList<>();
        selectedEpisodes.stream().filter(episode -> !selectedFeeds.contains(episode.getFeed())).forEach(consolidatedEpisodes::add);
        selectedFeeds.stream().flatMap(feed -> feed.getEpisodes().stream()).forEach(consolidatedEpisodes::add);
        return consolidatedEpisodes;
    }

}
