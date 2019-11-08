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
import de.perdian.apps.podcentral.downloader.episodes.EpisodeContentDownloader;
import de.perdian.apps.podcentral.jobscheduler.JobScheduler;
import de.perdian.apps.podcentral.model.EpisodeContentDownloadState;
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
import javafx.scene.layout.BorderPane;

public class LibraryTreeTableView extends TreeTableView<LibraryTreeTableValue> {

    public LibraryTreeTableView(JobScheduler uiJobScheduler, EpisodeContentDownloader episodeContentDownloader, Library library, Localization localization) {

        TreeTableColumn<LibraryTreeTableValue, String> titleColumn = new TreeTableColumn<>(localization.title());
        titleColumn.setCellValueFactory(cell -> cell.getValue().getValue().getTitle());
        titleColumn.setCellFactory(cell -> new InternalTextFieldCell());
        titleColumn.setMinWidth(400);
        titleColumn.setMaxWidth(600);
        titleColumn.setPrefWidth(400);
        titleColumn.setEditable(false);
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

        TreeTableColumn<LibraryTreeTableValue, EpisodeContentDownloadState> contentDownloadStateColumn = new TreeTableColumn<>(localization.download());
        contentDownloadStateColumn.setCellValueFactory(cell -> cell.getValue().getValue().getContentDownloadState());
        contentDownloadStateColumn.setCellFactory(cell -> new InternalEpisodeStorageStateTreeTableCell(localization));
        contentDownloadStateColumn.setMinWidth(105);
        contentDownloadStateColumn.setMaxWidth(105);
        contentDownloadStateColumn.setEditable(false);
        contentDownloadStateColumn.setSortable(false);
        contentDownloadStateColumn.setReorderable(false);

        TreeTableColumn<LibraryTreeTableValue, Double> contentDownloadProgressColumn = new TreeTableColumn<>(localization.progress());
        contentDownloadProgressColumn.setCellValueFactory(cell -> cell.getValue().getValue().getContentDownloadProgress());
        contentDownloadProgressColumn.setCellFactory(cell -> new InternalProgressBarTreeTableCell());
        contentDownloadProgressColumn.setMinWidth(80);
        contentDownloadProgressColumn.setMaxWidth(80);
        contentDownloadProgressColumn.setEditable(false);
        contentDownloadProgressColumn.setSortable(false);
        contentDownloadProgressColumn.setReorderable(false);

        TreeTableColumn<LibraryTreeTableValue, String> contentDownloadProgressValueColumn = new TreeTableColumn<>();
        contentDownloadProgressValueColumn.setCellValueFactory(cell -> cell.getValue().getValue().getContentDownloadProgressLabel());
        contentDownloadProgressValueColumn.setCellFactory(cell -> new InternalTextFieldCell());
        contentDownloadProgressValueColumn.setStyle("-fx-alignment: CENTER-RIGHT;");
        contentDownloadProgressValueColumn.setMinWidth(50);
        contentDownloadProgressValueColumn.setMaxWidth(50);
        contentDownloadProgressValueColumn.setEditable(false);
        contentDownloadProgressValueColumn.setSortable(false);
        contentDownloadProgressValueColumn.setReorderable(false);

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
        this.setContextMenu(new LibraryTreeTableContextMenu(this, uiJobScheduler, episodeContentDownloader, library, localization));
        this.setEditable(true);
        this.setSortMode(TreeSortMode.ONLY_FIRST_LEVEL);
        this.setColumnResizePolicy(TreeTableView.CONSTRAINED_RESIZE_POLICY);
        this.getColumns().addAll(List.of(titleColumn, durationColumn, publicationDateColumn, contentDownloadStateColumn, contentDownloadProgressColumn, contentDownloadProgressValueColumn, descriptionColumn));
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

    static class InternalEpisodeStorageStateTreeTableCell extends TreeTableCell<LibraryTreeTableValue, EpisodeContentDownloadState> {

        private Localization localization = null;

        InternalEpisodeStorageStateTreeTableCell(Localization localization) {
            this.setLocalization(localization);
        }

        @Override
        public void updateItem(EpisodeContentDownloadState item, boolean empty) {
            super.updateItem(item, empty);
            if (item == null || empty) {
                this.setGraphic(null);
            } else {
                BorderPane contentPane = new BorderPane();
                switch (item) {
                    case CANCELLED:
                        contentPane.setLeft(new Label(this.getLocalization().cancelled()));
                        contentPane.setRight(new Label("", new FontAwesomeIconView(FontAwesomeIcon.WINDOW_CLOSE)));
                        break;
                    case COMPLETED:
                        contentPane.setLeft(new Label(this.getLocalization().completed()));
                        contentPane.setRight(new Label("", new FontAwesomeIconView(FontAwesomeIcon.CHECK)));
                        break;
                    case DOWNLOADING:
                        contentPane.setLeft(new Label(this.getLocalization().downloading()));
                        contentPane.setRight(new Label("", new FontAwesomeIconView(FontAwesomeIcon.DOWNLOAD)));
                        break;
                    case NEW:
                        contentPane.setLeft(new Label(this.getLocalization().new_()));
                        break;
                    case SCHEDULED:
                        contentPane.setLeft(new Label(this.getLocalization().scheduled()));
                        contentPane.setRight(new Label("", new FontAwesomeIconView(FontAwesomeIcon.CLOCK_ALT)));
                        break;
                    case ERRORED:
                        contentPane.setLeft(new Label(this.getLocalization().errored()));
                        contentPane.setRight(new Label("", new FontAwesomeIconView(FontAwesomeIcon.EXCLAMATION_TRIANGLE)));
                        break;
                }
                this.setGraphic(contentPane);
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
