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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.perdian.apps.podcentral.model.Episode;
import de.perdian.apps.podcentral.model.Feed;
import de.perdian.apps.podcentral.model.FeedInput;
import de.perdian.apps.podcentral.model.FeedInputOptions;
import de.perdian.apps.podcentral.model.Library;
import de.perdian.apps.podcentral.retrieval.FeedInputLoader;
import de.perdian.apps.podcentral.ui.localization.Localization;
import de.perdian.apps.podcentral.ui.support.treetable.TreeTableHelper;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeSortMode;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.scene.control.cell.TextFieldTreeTableCell;
import javafx.scene.input.ContextMenuEvent;

public class LibraryTreeTableView extends TreeTableView<LibraryTreeValue> {

    public LibraryTreeTableView(Library library, FeedInputLoader feedInputLoader, Localization localization) {

        TreeTableColumn<LibraryTreeValue, String> titleColumn = TreeTableHelper.createColumn(LibraryTreeValue::getTitle, localization.title());
        titleColumn.setCellFactory(TextFieldTreeTableCell.forTreeTableColumn());
        titleColumn.setMinWidth(400);
        titleColumn.setMaxWidth(600);
        titleColumn.setPrefWidth(400);
        titleColumn.setEditable(true);

        TreeTableColumn<LibraryTreeValue, String> durationColumn = TreeTableHelper.createColumn(LibraryTreeValue::getDuration, localization.duration());
        durationColumn.setMinWidth(80);
        durationColumn.setMaxWidth(80);
        durationColumn.setEditable(false);
        durationColumn.setSortable(false);

        TreeTableColumn<LibraryTreeValue, String> publicationDateColumn = TreeTableHelper.createColumn(LibraryTreeValue::getPublicationDate, localization.date());
        publicationDateColumn.setMinWidth(80);
        publicationDateColumn.setMaxWidth(80);
        publicationDateColumn.setEditable(false);
        publicationDateColumn.setSortable(false);

        TreeTableColumn<LibraryTreeValue, String> descriptionColumn = TreeTableHelper.createColumn(LibraryTreeValue::getDescription, localization.description());
        descriptionColumn.setCellFactory(TextFieldTreeTableCell.forTreeTableColumn());
        descriptionColumn.setMinWidth(400);
        descriptionColumn.setMaxWidth(Double.MAX_VALUE);
        descriptionColumn.setEditable(false);
        descriptionColumn.setSortable(false);

        this.setShowRoot(false);
        this.setRoot(new LibraryTreeRootItem(library));
        this.setOnContextMenuRequested(event -> this.onTreeTableViewContextMenuEvent(event, library, feedInputLoader, localization));
        this.setEditable(true);
        this.setSortMode(TreeSortMode.ONLY_FIRST_LEVEL);
        this.setColumnResizePolicy(TreeTableView.CONSTRAINED_RESIZE_POLICY);
        this.getColumns().addAll(List.of(titleColumn, durationColumn, publicationDateColumn, descriptionColumn));
        this.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
//        treeTableView.setRowFactory(tv -> {
//            TreeTableRow<LibraryTreeValue> tableRow = new TreeTableRow<>();
//            tableRow.setOnDragDetected(event -> {
//                System.err.println("DRAG!!!");
//                Dragboard db = tableRow.startDragAndDrop(TransferMode.COPY);
//
//                ClipboardContent content = new ClipboardContent();
//                content.putFiles(List.of(new File("/Users/perdian/Downloads/w13xttx2.rss")));
//                db.setContent(content);
//                event.consume();
//            });
//            return tableRow;
//        });

    }

    private void onTreeTableViewContextMenuEvent(ContextMenuEvent contextMenuEvent, Library library, FeedInputLoader feedInputLoader, Localization localization) {
        ContextMenu contextMenu = new ContextMenu();
        List<TreeItem<LibraryTreeValue>> selectedItems = new ArrayList<>(this.getSelectionModel().getSelectedItems());
        if (!selectedItems.isEmpty()) {
MenuItem reloadItem = new MenuItem("TMP__RELOAD");
reloadItem.setOnAction(actionEvent -> {
        LibraryTreeValue.FeedTreeValue feedValue = (LibraryTreeValue.FeedTreeValue)this.getSelectionModel().getSelectedItem().getValue();
        String feedUrl = feedValue.getFeed().getUrl().getValue();
        new Thread(() -> {
            try {
                FeedInput feedInput = feedInputLoader.submitFeedUrl(feedUrl).get();
                FeedInputOptions feedInputOptions = new FeedInputOptions();
                feedInputOptions.setResetLocalValues(true);
                feedInputOptions.setResetDeletedEpisodes(true);
                feedValue.getLibrary().updateFeedFromInput(feedInput, feedInputOptions);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
});
contextMenu.getItems().add(reloadItem);

            MenuItem deleteMenuItem = new MenuItem(localization.delete());
            deleteMenuItem.setOnAction(actionEvent -> {
                Map<Feed, List<Episode>> episodesByFeed = new HashMap<>();
                this.getSelectionModel().getSelectedItems().stream().filter(value -> value.getValue() instanceof LibraryTreeValue.EpisodeTreeValue).map(value -> (LibraryTreeValue.EpisodeTreeValue)value.getValue()).forEach(value -> {
                    episodesByFeed.compute(value.getFeed(), (k, v) -> v == null ? new ArrayList<>() : v).add(value.getEpisode());
                });
                episodesByFeed.forEach((feed, episodes) -> feed.getEpisodes().removeAll(episodes));
            });
            contextMenu.getItems().add(deleteMenuItem);
        }
        if (!contextMenu.getItems().isEmpty()) {
            contextMenu.show((Node)contextMenuEvent.getSource(), contextMenuEvent.getScreenX(), contextMenuEvent.getScreenY());
        }
    }

}
