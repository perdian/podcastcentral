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

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import de.perdian.apps.podcastcentral.model.Episode;
import de.perdian.apps.podcastcentral.model.Feed;
import de.perdian.apps.podcastcentral.model.Library;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.scene.control.TreeItem;

class LibraryTreeRootItem extends TreeItem<LibraryTreeTableValue> {

    LibraryTreeRootItem(Library library) {
        Map<Feed, TreeItem<LibraryTreeTableValue>> feedTreeItemsByFeed = new HashMap<>();
        for (Feed feed : library.getFeeds()) {
            TreeItem<LibraryTreeTableValue> feedTreeItem = LibraryTreeRootItem.createTreeItemForFeed(library, feed);
            this.getChildren().add(feedTreeItem);
            feedTreeItemsByFeed.put(feed, feedTreeItem);
        }
        library.getFeeds().addListener((ListChangeListener.Change<? extends Feed> change) -> {
            while (change.next()) {
                for (Feed removedFeed : change.getRemoved()) {
                    TreeItem<LibraryTreeTableValue> feedTreeItem = feedTreeItemsByFeed.get(removedFeed);
                    if (feedTreeItem != null) {
                        this.getChildren().remove(feedTreeItem);
                    }
                }
                for (Feed addedFeed : change.getAddedSubList()) {
                    TreeItem<LibraryTreeTableValue> feedTreeItem = LibraryTreeRootItem.createTreeItemForFeed(library, addedFeed);
                    int feedTargetIndex = change.getList().indexOf(addedFeed);
                    Platform.runLater(() -> {
                        this.getChildren().add(feedTargetIndex, feedTreeItem);
                    });
                    feedTreeItemsByFeed.put(addedFeed, feedTreeItem);
                }
                if (change.wasPermutated()) {
                    Platform.runLater(() -> {
                        FXCollections.sort(this.getChildren(), Comparator.comparing(item -> item.getValue().getTitle().getValue()));
                    });
                }
            }
        });
    }

    private static TreeItem<LibraryTreeTableValue> createTreeItemForFeed(Library library, Feed feed) {
        TreeItem<LibraryTreeTableValue> feedTreeItem = new TreeItem<>(new LibraryTreeTableValue.FeedTreeValue(library, feed));
        Map<Episode, TreeItem<LibraryTreeTableValue>> episodeTreeItemsByEpisode = new HashMap<>();
        for (Episode episode : feed.getEpisodes()) {
            TreeItem<LibraryTreeTableValue> episodeTreeItem = LibraryTreeRootItem.createTreeItemForEpisode(feed, episode);
            feedTreeItem.setExpanded(Optional.ofNullable(feed.getExpanded().getValue()).orElse(Boolean.TRUE));
            feedTreeItem.getChildren().add(episodeTreeItem);
            feedTreeItem.expandedProperty().addListener((o, oldValue, newValue) -> feed.getExpanded().setValue(newValue));
            episodeTreeItemsByEpisode.put(episode, episodeTreeItem);
        }
        feed.getEpisodes().addListener((ListChangeListener.Change<? extends Episode> change) -> {
            while (change.next()) {
                for (Episode removedEpisode : change.getRemoved()) {
                    TreeItem<LibraryTreeTableValue> removedValue = episodeTreeItemsByEpisode.remove(removedEpisode);
                    if (removedValue != null) {
                        Platform.runLater(() -> {
                            feedTreeItem.getChildren().remove(removedValue);
                        });
                    }
                }
                for (Episode addedEpisode : change.getAddedSubList()) {
                    episodeTreeItemsByEpisode.put(addedEpisode, LibraryTreeRootItem.createTreeItemForEpisode(feed, addedEpisode));
                }
                if (change.wasPermutated()) {
                    List<TreeItem<LibraryTreeTableValue>> episodeTreeItems = change.getList().stream().map(episode -> episodeTreeItemsByEpisode.get(episode)).filter(Objects::nonNull).collect(Collectors.toList());
                    Platform.runLater(() -> {
                        feedTreeItem.getChildren().clear();
                        feedTreeItem.getChildren().setAll(episodeTreeItems);
                    });
                }
            }
        });
        return feedTreeItem;
    }

    private static TreeItem<LibraryTreeTableValue> createTreeItemForEpisode(Feed feed, Episode episode) {
        return new TreeItem<>(new LibraryTreeTableValue.EpisodeTreeValue(feed, episode));
    }

}
