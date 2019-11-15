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
package de.perdian.apps.podcastcentral.ui.modules.library_new.treetable;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

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
        TreeItem<LibraryTreeItemValue> treeItem = new TreeItem<>(new LibraryTreeItemValue.FeedItemValue(feed));
        return treeItem;
    }

    private static TreeItem<LibraryTreeItemValue> createEpisodeTreeItem(Feed feed, Episode episode) {
        TreeItem<LibraryTreeItemValue> treeItem = new TreeItem<>(new LibraryTreeItemValue.EpisodeItemValue(feed, episode));
        return treeItem;
    }

    private static int compareTreeItems(TreeItem<LibraryTreeItemValue> o1, TreeItem<LibraryTreeItemValue> o2) {
        return String.CASE_INSENSITIVE_ORDER.compare(Optional.ofNullable(o1.getValue().getTitle().getValue()).orElse(""), Optional.ofNullable(o2.getValue().getTitle().getValue()).orElse(""));
    }

}
