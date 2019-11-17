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
import java.util.Collections;
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

class LibraryTreeItemFactory {

    static TreeItem<LibraryTreeItemValue> createLibraryRootItem(Library library) {
        Map<Feed, TreeItem<LibraryTreeItemValue>> feedTreeItemsByFeed = new HashMap<>();
        TreeItem<LibraryTreeItemValue> rootItem = new TreeItem<>();
        for (Feed feed : library.getFeeds()) {
            TreeItem<LibraryTreeItemValue> feedTreeItem = LibraryTreeItemFactory.createFeedTreeItem(feed);
            rootItem.getChildren().add(feedTreeItem);
            feedTreeItemsByFeed.put(feed, rootItem);
        }
        library.getFeeds().addListener((ListChangeListener.Change<? extends Feed> change) -> {
            synchronized (rootItem) {
                while (change.next()) {
                    for (Feed newFeed : change.getAddedSubList()) {
                        if (feedTreeItemsByFeed.get(newFeed) == null) {
                            TreeItem<LibraryTreeItemValue> newFeedTreeItem = LibraryTreeItemFactory.createFeedTreeItem(newFeed);
                            feedTreeItemsByFeed.put(newFeed, newFeedTreeItem);
                            Platform.runLater(() -> rootItem.getChildren().add(change.getList().indexOf(newFeed), newFeedTreeItem));
                        }
                    }
                    for (Feed removedFeed : change.getRemoved()) {
                        TreeItem<LibraryTreeItemValue> feedTreeItem = feedTreeItemsByFeed.remove(removedFeed);
                        if (feedTreeItem != null) {
                            Platform.runLater(() -> rootItem.getChildren().remove(feedTreeItem));
                        }
                    }
                    if (change.wasPermutated()) {
                        Platform.runLater(() -> FXCollections.sort(rootItem.getChildren(), LibraryTreeItemFactory::compareTreeItems));
                    }
                }
            }
        });
        return rootItem;
    }

    private static TreeItem<LibraryTreeItemValue> createFeedTreeItem(Feed feed) {
        List<TreeItem<LibraryTreeItemValue>> episodeTreeItems = feed.getEpisodes().stream().map(episode -> LibraryTreeItemFactory.createEpisodeTreeItem(episode)).collect(Collectors.toList());
        EpisodeTreeItemsCollector episodeTreeItemsCollector = new EpisodeTreeItemsCollector(episodeTreeItems);
        TreeItem<LibraryTreeItemValue> feedTreeItem = new TreeItem<>(new LibraryTreeItemValue(feed));
        feedTreeItem.setExpanded(feed.getExpanded().getValue());
        feedTreeItem.expandedProperty().addListener((o, oldValue, newValue) -> feed.getExpanded().setValue(newValue));
        feedTreeItem.getChildren().addAll(episodeTreeItems);

        feed.getEpisodes().addListener((ListChangeListener.Change<? extends Episode> episodeChange) -> {
            while (episodeChange.next()) {
                List<TreeItem<LibraryTreeItemValue>> treeItemsToRemove = episodeTreeItemsCollector.removeAll(episodeChange.getRemoved());
                if (!treeItemsToRemove.isEmpty()) {
                    Platform.runLater(() -> feedTreeItem.getChildren().removeAll(treeItemsToRemove));
                }
                List<TreeItem<LibraryTreeItemValue>> treeItemsToAdd = episodeTreeItemsCollector.addAll(feed, episodeChange.getAddedSubList());
                if (!treeItemsToAdd.isEmpty()) {
                    Platform.runLater(() -> {
                        feedTreeItem.getChildren().addAll(treeItemsToAdd);
                    });
                }
                if (!treeItemsToAdd.isEmpty() || episodeChange.wasPermutated()) {
                    List<TreeItem<LibraryTreeItemValue>> treeItemsConsolidated = episodeTreeItemsCollector.consolidateAll(feed, episodeChange.getList());
                    Platform.runLater(() -> {
                        feedTreeItem.getChildren().clear();
                        feedTreeItem.getChildren().setAll(treeItemsConsolidated);
                    });
                }
            }
        });

        return feedTreeItem;
    }

    private static TreeItem<LibraryTreeItemValue> createEpisodeTreeItem(Episode episode) {
        TreeItem<LibraryTreeItemValue> treeItem = new TreeItem<>(new LibraryTreeItemValue(episode));
        return treeItem;
    }

    private static int compareTreeItems(TreeItem<LibraryTreeItemValue> o1, TreeItem<LibraryTreeItemValue> o2) {
        return String.CASE_INSENSITIVE_ORDER.compare(Optional.ofNullable(o1.getValue().getTitle().getValue()).orElse(""), Optional.ofNullable(o2.getValue().getTitle().getValue()).orElse(""));
    }

    private static class EpisodeTreeItemsCollector {

        private Map<Episode, TreeItem<LibraryTreeItemValue>> treeItemsByEpisode = null;

        private EpisodeTreeItemsCollector(List<TreeItem<LibraryTreeItemValue>> treeItems) {
            Map<Episode, TreeItem<LibraryTreeItemValue>> treeItemsByEpisode = new HashMap<>();
            treeItems.forEach(treeItem -> treeItemsByEpisode.put(treeItem.getValue().getEpisode(), treeItem));
            this.setTreeItemsByEpisode(treeItemsByEpisode);
        }

        public List<TreeItem<LibraryTreeItemValue>> addAll(Feed feed, List<? extends Episode> episodesToAdd) {
            List<TreeItem<LibraryTreeItemValue>> resultTreeItems = new ArrayList<>(episodesToAdd.size());
            for (Episode episode : episodesToAdd) {
                TreeItem<LibraryTreeItemValue> resultTreeItem = LibraryTreeItemFactory.createEpisodeTreeItem(episode);
                this.getTreeItemsByEpisode().put(episode, resultTreeItem);
                resultTreeItems.add(resultTreeItem);
            }
            return Collections.unmodifiableList(resultTreeItems);
        }

        public List<TreeItem<LibraryTreeItemValue>> removeAll(List<? extends Episode> episodesToRemove) {
            return episodesToRemove.stream()
                .map(episode -> this.getTreeItemsByEpisode().remove(episode))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        }

        public List<TreeItem<LibraryTreeItemValue>> consolidateAll(Feed feed, List<? extends Episode> episodes) {
            List<TreeItem<LibraryTreeItemValue>> resultTreeItems = new ArrayList<>(episodes.size());
            for (Episode episode : episodes) {
                TreeItem<LibraryTreeItemValue> resultTreeItem = this.getTreeItemsByEpisode().get(episode);
                if (resultTreeItem == null) {
                    resultTreeItem = LibraryTreeItemFactory.createEpisodeTreeItem(episode);
                    this.getTreeItemsByEpisode().put(episode, resultTreeItem);
                }
                resultTreeItems.add(resultTreeItem);
            }
            return Collections.unmodifiableList(resultTreeItems);
        }

        private Map<Episode, TreeItem<LibraryTreeItemValue>> getTreeItemsByEpisode() {
            return this.treeItemsByEpisode;
        }
        private void setTreeItemsByEpisode(Map<Episode, TreeItem<LibraryTreeItemValue>> treeItemsByEpisode) {
            this.treeItemsByEpisode = treeItemsByEpisode;
        }

    }

}
