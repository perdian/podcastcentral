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
import de.perdian.apps.podcentral.downloader.episodes.EpisodeDownloader;
import de.perdian.apps.podcentral.model.EpisodeDownloadState;
import de.perdian.apps.podcentral.model.Library;
import de.perdian.apps.podcentral.ui.support.backgroundtasks.BackgroundTaskExecutor;
import de.perdian.apps.podcentral.ui.support.localization.Localization;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TreeSortMode;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableRow;
import javafx.scene.control.TreeTableView;
import javafx.scene.control.cell.ProgressBarTreeTableCell;
import javafx.scene.control.cell.TextFieldTreeTableCell;
import javafx.scene.layout.BorderPane;

public class LibraryTreeTableView extends TreeTableView<LibraryTreeTableValue> {

    public LibraryTreeTableView(BackgroundTaskExecutor backgroundTaskExecutor, EpisodeDownloader episodeDownloader, Library library, Localization localization) {

        TreeTableColumn<LibraryTreeTableValue, String> titleColumn = new TreeTableColumn<>(localization.title());
        titleColumn.setCellValueFactory(cell -> cell.getValue().getValue().getTitle());
        titleColumn.setCellFactory(cell -> new InternalTextFieldCell());
        titleColumn.setMinWidth(350);
        titleColumn.setMaxWidth(600);
        titleColumn.setPrefWidth(400);
        titleColumn.setEditable(false);
        titleColumn.setReorderable(false);

        TreeTableColumn<LibraryTreeTableValue, String> durationColumn = new TreeTableColumn<>(localization.duration());
        durationColumn.setCellValueFactory(cell -> cell.getValue().getValue().getDuration());
        durationColumn.setCellFactory(cell -> new InternalTextFieldCell());
        durationColumn.setMinWidth(70);
        durationColumn.setMaxWidth(70);
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

        TreeTableColumn<LibraryTreeTableValue, EpisodeDownloadState> downloadStateColumn = new TreeTableColumn<>(localization.download());
        downloadStateColumn.setCellValueFactory(cell -> cell.getValue().getValue().getDownloadState());
        downloadStateColumn.setCellFactory(cell -> new InternalEpisodeStorageStateTreeTableCell(localization));
        downloadStateColumn.setMinWidth(105);
        downloadStateColumn.setMaxWidth(105);
        downloadStateColumn.setEditable(false);
        downloadStateColumn.setSortable(false);
        downloadStateColumn.setReorderable(false);

        TreeTableColumn<LibraryTreeTableValue, Double> downloadProgressColumn = new TreeTableColumn<>(localization.progress());
        downloadProgressColumn.setCellValueFactory(cell -> cell.getValue().getValue().getDownloadProgress());
        downloadProgressColumn.setCellFactory(cell -> new InternalProgressBarTreeTableCell());
        downloadProgressColumn.setMinWidth(80);
        downloadProgressColumn.setMaxWidth(80);
        downloadProgressColumn.setEditable(false);
        downloadProgressColumn.setSortable(false);
        downloadProgressColumn.setReorderable(false);

        TreeTableColumn<LibraryTreeTableValue, String> downloadProgressValueColumn = new TreeTableColumn<>();
        downloadProgressValueColumn.setCellValueFactory(cell -> cell.getValue().getValue().getDownloadProgressLabel());
        downloadProgressValueColumn.setCellFactory(cell -> new InternalTextFieldCell());
        downloadProgressValueColumn.setStyle("-fx-alignment: CENTER-RIGHT;");
        downloadProgressValueColumn.setMinWidth(50);
        downloadProgressValueColumn.setMaxWidth(50);
        downloadProgressValueColumn.setEditable(false);
        downloadProgressValueColumn.setSortable(false);
        downloadProgressValueColumn.setReorderable(false);

        TreeTableColumn<LibraryTreeTableValue, String> descriptionColumn = new TreeTableColumn<>(localization.description());
        descriptionColumn.setCellValueFactory(cell -> cell.getValue().getValue().getDescription());
        descriptionColumn.setCellFactory(cell -> new InternalTextFieldCell());
        descriptionColumn.setMinWidth(300);
        descriptionColumn.setMaxWidth(Double.MAX_VALUE);
        descriptionColumn.setEditable(false);
        descriptionColumn.setSortable(false);
        descriptionColumn.setReorderable(false);

        LibrarySelection librarySelection = new LibrarySelection(this.getSelectionModel());

        this.setShowRoot(false);
        this.setRoot(new LibraryTreeRootItem(library));
        this.setContextMenu(new LibraryTreeTableContextMenu(librarySelection, backgroundTaskExecutor, episodeDownloader, library, localization));
        this.setEditable(false);
        this.setSortMode(TreeSortMode.ONLY_FIRST_LEVEL);
        this.setColumnResizePolicy(TreeTableView.CONSTRAINED_RESIZE_POLICY);
        this.getColumns().addAll(List.of(titleColumn, durationColumn, publicationDateColumn, downloadStateColumn, downloadProgressColumn, downloadProgressValueColumn, descriptionColumn));
        this.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        this.setOnKeyPressed(new LibraryTreeTableKeyListener(librarySelection, backgroundTaskExecutor, episodeDownloader, library, localization));
        this.setOnMouseClicked(new LibraryTreeTableMouseClickedEventListener(librarySelection, backgroundTaskExecutor, episodeDownloader, localization));
        this.setRowFactory(tv -> {
            TreeTableRow<LibraryTreeTableValue> tableRow = new TreeTableRow<>();
            tableRow.setOnDragDetected(new LibraryTreeTableMouseEventHandler(librarySelection));
            return tableRow;
         });

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

    static class InternalEpisodeStorageStateTreeTableCell extends TreeTableCell<LibraryTreeTableValue, EpisodeDownloadState> {

        private Localization localization = null;

        InternalEpisodeStorageStateTreeTableCell(Localization localization) {
            this.setLocalization(localization);
        }

        @Override
        public void updateItem(EpisodeDownloadState item, boolean empty) {
            super.updateItem(item, empty);
            if (item == null || empty) {
                this.setGraphic(null);
            } else {
                Label textLabel = new Label();
                Label iconLabel = new Label();
                iconLabel.setMaxHeight(Double.MAX_VALUE);
                iconLabel.setAlignment(Pos.CENTER);
                switch (item) {
                    case CANCELLED:
                        textLabel.setText(this.getLocalization().cancelled());
                        iconLabel.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.WINDOW_CLOSE));
                        break;
                    case COMPLETED:
                        textLabel.setText(this.getLocalization().completed());
                        iconLabel.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.CHECK));
                        break;
                    case DOWNLOADING:
                        textLabel.setText(this.getLocalization().downloading());
                        iconLabel.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.DOWNLOAD));
                        break;
                    case NEW:
                        textLabel.setText(this.getLocalization().new_());
                        break;
                    case MISSING:
                        textLabel.setText(this.getLocalization().missing());
                        iconLabel.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.QUESTION));
                        break;
                    case SCHEDULED:
                        textLabel.setText(this.getLocalization().scheduled());
                        iconLabel.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.CLOCK_ALT));
                        break;
                    case ERRORED:
                        textLabel.setText(this.getLocalization().errored());
                        iconLabel.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.EXCLAMATION_TRIANGLE));
                        break;
                }
                BorderPane contentPane = new BorderPane();
                contentPane.setLeft(textLabel);
                contentPane.setRight(iconLabel);
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
