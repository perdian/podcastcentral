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
import java.util.stream.Collectors;

import de.perdian.apps.podcentral.jobscheduler.JobScheduler;
import de.perdian.apps.podcentral.model.Episode;
import de.perdian.apps.podcentral.model.Feed;
import de.perdian.apps.podcentral.model.Library;
import de.perdian.apps.podcentral.ui.localization.Localization;
import de.perdian.apps.podcentral.ui.support.treetable.TreeTableHelper;
import javafx.application.Platform;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TreeSortMode;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.scene.control.cell.TextFieldTreeTableCell;
import javafx.scene.input.ContextMenuEvent;

public class LibraryTreeTableView extends TreeTableView<LibraryTreeTableValue> {

    public LibraryTreeTableView(JobScheduler jobScheduler, Library library, Localization localization) {

        TreeTableColumn<LibraryTreeTableValue, String> titleColumn = TreeTableHelper.createColumn(LibraryTreeTableValue::getTitle, localization.title());
        titleColumn.setCellFactory(TextFieldTreeTableCell.forTreeTableColumn());
        titleColumn.setMinWidth(400);
        titleColumn.setMaxWidth(600);
        titleColumn.setPrefWidth(400);
        titleColumn.setEditable(true);

        TreeTableColumn<LibraryTreeTableValue, String> durationColumn = TreeTableHelper.createColumn(LibraryTreeTableValue::getDuration, localization.duration());
        durationColumn.setMinWidth(80);
        durationColumn.setMaxWidth(80);
        durationColumn.setEditable(false);
        durationColumn.setSortable(false);

        TreeTableColumn<LibraryTreeTableValue, String> publicationDateColumn = TreeTableHelper.createColumn(LibraryTreeTableValue::getPublicationDate, localization.date());
        publicationDateColumn.setMinWidth(80);
        publicationDateColumn.setMaxWidth(80);
        publicationDateColumn.setEditable(false);
        publicationDateColumn.setSortable(false);

        TreeTableColumn<LibraryTreeTableValue, String> descriptionColumn = TreeTableHelper.createColumn(LibraryTreeTableValue::getDescription, localization.description());
        descriptionColumn.setCellFactory(TextFieldTreeTableCell.forTreeTableColumn());
        descriptionColumn.setMinWidth(400);
        descriptionColumn.setMaxWidth(Double.MAX_VALUE);
        descriptionColumn.setEditable(false);
        descriptionColumn.setSortable(false);

        this.setShowRoot(false);
        this.setRoot(new LibraryTreeRootItem(library));
        this.setOnContextMenuRequested(event -> this.onTreeTableViewContextMenuEvent(event, jobScheduler, library, localization));
        this.setEditable(true);
        this.setSortMode(TreeSortMode.ONLY_FIRST_LEVEL);
        this.setColumnResizePolicy(TreeTableView.CONSTRAINED_RESIZE_POLICY);
        this.getColumns().addAll(List.of(titleColumn, durationColumn, publicationDateColumn, descriptionColumn));
        this.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
//        treeTableView.setRowFactory(tv -> {
//            TreeTableRow<LibraryTreeTableValue> tableRow = new TreeTableRow<>();
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

    private List<Feed> collectSelectedFeeds() {
        return this.getSelectionModel().getSelectedItems().stream()
            .filter(item -> item.getValue() instanceof LibraryTreeTableValue.FeedTreeValue)
            .map(item -> ((LibraryTreeTableValue.FeedTreeValue)item.getValue()).getFeed())
            .collect(Collectors.toList());
    }

    private Map<Feed, List<Episode>> collectSelectedEpisodes() {
        Map<Feed, List<Episode>> episodes = new HashMap<>();
        this.getSelectionModel().getSelectedItems().stream()
            .filter(item -> item.getValue() instanceof LibraryTreeTableValue.EpisodeTreeValue)
            .map(item -> (LibraryTreeTableValue.EpisodeTreeValue)item.getValue())
            .forEach(item -> episodes.compute(item.getFeed(), (k, v) -> v == null ? new ArrayList<>() : v).add(item.getEpisode()));
        return episodes;
    }

    private void onTreeTableViewContextMenuEvent(ContextMenuEvent contextMenuEvent, JobScheduler jobScheduler, Library library, Localization localization) {
        Runnable clearSelectionCallback = () -> Platform.runLater(() -> this.getSelectionModel().clearSelection());
        LibraryTreeTableContextMenu contextMenu = new LibraryTreeTableContextMenu(this.collectSelectedFeeds(), this.collectSelectedEpisodes(), clearSelectionCallback, jobScheduler, library, localization);
        contextMenu.show(this, contextMenuEvent.getScreenX(), contextMenuEvent.getScreenY());
    }

}
