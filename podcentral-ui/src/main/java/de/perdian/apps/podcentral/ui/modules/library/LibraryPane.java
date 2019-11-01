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

import java.io.File;
import java.util.List;

import de.perdian.apps.podcentral.core.model.Episode;
import de.perdian.apps.podcentral.core.model.Feed;
import de.perdian.apps.podcentral.core.model.Library;
import de.perdian.apps.podcentral.ui.localization.Localization;
import de.perdian.apps.podcentral.ui.support.treetable.TreeTableHelper;
import javafx.geometry.Insets;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableRow;
import javafx.scene.control.TreeTableView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

public class LibraryPane extends GridPane {

    public LibraryPane(Library library, Localization localization) {

        LibraryToolbarPane toolbarPane = new LibraryToolbarPane(library, localization);
        GridPane.setMargin(toolbarPane, new Insets(0, 0, 4, 0));
        GridPane.setHgrow(toolbarPane, Priority.ALWAYS);

        TreeItem<LibraryTreeValue> rootItem = new TreeItem<>(null);
        for (Feed feed : library.getFeeds()) {
            LibraryTreeItem feedTreeItem = new LibraryTreeItem(new LibraryTreeValue.FeedTreeValue(feed));
            for (Episode episode : feed.getEpisodes()) {
                feedTreeItem.setExpanded(true);
                feedTreeItem.getChildren().add(new LibraryTreeItem(new LibraryTreeValue.EpisodeTreeValue(episode)));
            }
            rootItem.getChildren().add(feedTreeItem);
        }

        TreeTableColumn<LibraryTreeValue, String> titleColumn = TreeTableHelper.createColumn(LibraryTreeValue::getTitle, localization.title());
        titleColumn.setMaxWidth(Double.MAX_VALUE);

        TreeTableColumn<LibraryTreeValue, String> durationColumn = TreeTableHelper.createColumn(LibraryTreeValue::getDuration, localization.duration());
        durationColumn.setPrefWidth(100);
        durationColumn.setMinWidth(100);

        TreeTableView<LibraryTreeValue> treeTableView = new TreeTableView<>(rootItem);
        treeTableView.setShowRoot(false);
        treeTableView.setColumnResizePolicy(TreeTableView.CONSTRAINED_RESIZE_POLICY);
        treeTableView.getColumns().addAll(List.of(titleColumn, durationColumn));
        treeTableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        treeTableView.setRowFactory(tv -> {
            TreeTableRow<LibraryTreeValue> tableRow = new TreeTableRow<>();
            tableRow.setOnDragDetected(event -> {
                System.err.println("DRAG!!!");
                Dragboard db = tableRow.startDragAndDrop(TransferMode.COPY);

                ClipboardContent content = new ClipboardContent();
                content.putFiles(List.of(new File("/Users/perdian/Downloads/w13xttx2.rss")));
                db.setContent(content);
                event.consume();
            });
            return tableRow;
        });
        GridPane.setHgrow(treeTableView, Priority.ALWAYS);
        GridPane.setVgrow(treeTableView, Priority.ALWAYS);

        this.add(toolbarPane, 0, 0, 1, 1);
        this.add(treeTableView, 0, 1, 1, 1);

    }

}
