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

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import de.perdian.apps.podcentral.jobscheduler.JobScheduler;
import de.perdian.apps.podcentral.model.EpisodeStorageState;
import de.perdian.apps.podcentral.model.Library;
import de.perdian.apps.podcentral.ui.localization.Localization;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TreeSortMode;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.scene.control.cell.ProgressBarTreeTableCell;
import javafx.scene.control.cell.TextFieldTreeTableCell;

public class LibraryTreeTableView extends TreeTableView<LibraryTreeTableValue> {

    public LibraryTreeTableView(JobScheduler uiJobScheduler, JobScheduler downloadJobScheduler, Library library, Localization localization) {

        TreeTableColumn<LibraryTreeTableValue, String> titleColumn = new TreeTableColumn<>(localization.title());
        titleColumn.setCellValueFactory(cell -> cell.getValue().getValue().getTitle());
        titleColumn.setCellFactory(cell -> new InternalTextFieldCell());
        titleColumn.setMinWidth(400);
        titleColumn.setMaxWidth(600);
        titleColumn.setPrefWidth(400);
        titleColumn.setEditable(true);
        titleColumn.setReorderable(false);

        TreeTableColumn<LibraryTreeTableValue, String> durationColumn = new TreeTableColumn<>(localization.duration());
        durationColumn.setCellValueFactory(cell -> cell.getValue().getValue().getDuration());
        durationColumn.setCellFactory(cell -> new InternalTextFieldCell());
        durationColumn.setMinWidth(80);
        durationColumn.setMaxWidth(80);
        durationColumn.setEditable(false);
        durationColumn.setSortable(false);
        durationColumn.setReorderable(false);

        TreeTableColumn<LibraryTreeTableValue, String> publicationDateColumn = new TreeTableColumn<>(localization.date());
        publicationDateColumn.setCellValueFactory(cell -> cell.getValue().getValue().getPublicationDate());
        publicationDateColumn.setCellFactory(cell -> new InternalTextFieldCell());
        publicationDateColumn.setMinWidth(80);
        publicationDateColumn.setMaxWidth(80);
        publicationDateColumn.setEditable(false);
        publicationDateColumn.setSortable(false);
        publicationDateColumn.setReorderable(false);

        TreeTableColumn<LibraryTreeTableValue, EpisodeStorageState> storageStateColumn = new TreeTableColumn<>();
        storageStateColumn.setCellValueFactory(cell -> cell.getValue().getValue().getEpisodeStorageState());
        storageStateColumn.setCellFactory(cell -> new InternalEpisodeStorageStateTreeTableCell(localization));
        storageStateColumn.setMinWidth(80);
        storageStateColumn.setMaxWidth(80);
        storageStateColumn.setEditable(false);
        storageStateColumn.setSortable(false);
        storageStateColumn.setReorderable(false);

        TreeTableColumn<LibraryTreeTableValue, Double> storageProgressColumn = new TreeTableColumn<>(localization.storage());
        storageProgressColumn.setCellValueFactory(cell -> cell.getValue().getValue().getStorageProgress());
        storageProgressColumn.setCellFactory(cell -> new InternalProgressBarTreeTableCell());
        storageProgressColumn.setMinWidth(80);
        storageProgressColumn.setMaxWidth(80);
        storageProgressColumn.setEditable(false);
        storageProgressColumn.setSortable(false);
        storageProgressColumn.setReorderable(false);

        TreeTableColumn<LibraryTreeTableValue, String> storageProgressValueColumn = new TreeTableColumn<>();
        storageProgressValueColumn.setCellValueFactory(cell -> cell.getValue().getValue().getStorageProgressLabel());
        storageProgressValueColumn.setCellFactory(cell -> new InternalTextFieldCell());
        storageProgressValueColumn.setStyle("-fx-alignment: CENTER-RIGHT;");
        storageProgressValueColumn.setMinWidth(50);
        storageProgressValueColumn.setMaxWidth(50);
        storageProgressValueColumn.setEditable(false);
        storageProgressValueColumn.setSortable(false);
        storageProgressValueColumn.setReorderable(false);

        TreeTableColumn<LibraryTreeTableValue, String> descriptionColumn = new TreeTableColumn<>(localization.description());
        descriptionColumn.setCellValueFactory(cell -> cell.getValue().getValue().getDescription());
        descriptionColumn.setCellFactory(cell -> new InternalTextFieldCell());
        descriptionColumn.setMinWidth(400);
        descriptionColumn.setMaxWidth(Double.MAX_VALUE);
        descriptionColumn.setEditable(false);
        descriptionColumn.setSortable(false);
        descriptionColumn.setReorderable(false);

        this.setShowRoot(false);
        this.setRoot(new LibraryTreeRootItem(library));
        this.setContextMenu(new LibraryTreeTableContextMenu(this, uiJobScheduler, downloadJobScheduler, library, localization));
        this.setEditable(true);
        this.setSortMode(TreeSortMode.ONLY_FIRST_LEVEL);
        this.setColumnResizePolicy(TreeTableView.CONSTRAINED_RESIZE_POLICY);
        this.getColumns().addAll(List.of(titleColumn, durationColumn, publicationDateColumn, storageStateColumn, storageProgressColumn, storageProgressValueColumn, descriptionColumn));
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

    static class InternalTextFieldCell extends TextFieldTreeTableCell<LibraryTreeTableValue, String> {
    }

    static class InternalProgressBarTreeTableCell extends ProgressBarTreeTableCell<LibraryTreeTableValue> {

        @Override
        public void updateItem(Double item, boolean empty) {
            if (item == null) {
                super.updateItem(item, true);
            } else {
                super.updateItem(item, empty);
            }
        }

    }

    static class InternalEpisodeStorageStateTreeTableCell extends TreeTableCell<LibraryTreeTableValue, EpisodeStorageState> {

        private Localization localization = null;

        InternalEpisodeStorageStateTreeTableCell(Localization localization) {
            this.setLocalization(localization);
        }

        @Override
        public void updateItem(EpisodeStorageState item, boolean empty) {
            if (item == null) {
                super.updateItem(item, true);
            } else {
                Label newLabel = new Label(item.name());
                newLabel.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.PLUS));
                this.setGraphic(newLabel);
            }
        }

        private Localization getLocalization() {
            return this.localization;
        }
        private void setLocalization(Localization localization) {
            this.localization = localization;
        }

    }

}
