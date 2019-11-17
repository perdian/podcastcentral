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

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import de.perdian.apps.podcastcentral.downloader.episodes.EpisodeDownloader;
import de.perdian.apps.podcastcentral.model.EpisodeDownloadState;
import de.perdian.apps.podcastcentral.model.Library;
import de.perdian.apps.podcastcentral.ui.support.backgroundtasks.BackgroundTaskExecutor;
import de.perdian.apps.podcastcentral.ui.support.localization.Localization;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TreeSortMode;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableView;
import javafx.scene.control.cell.ProgressBarTreeTableCell;
import javafx.scene.control.cell.TextFieldTreeTableCell;
import javafx.scene.layout.BorderPane;

public class LibraryTreeTableView extends TreeTableView<LibraryTreeItemValue> {

    /*
     * The tree table view is *the* main component within the library. Almost every interaction either originates or is
     * performed directly on the tree table view. It took some time to iron out all the details, especially in the
     * interaction between the FX components and the model. So what you'll see here is the end of a pretty intensive
     * trial-and-error period. Some things may seem weird but they were carefully designed to deliver the best porssible
     * result.
     */

    public LibraryTreeTableView(Library library, EpisodeDownloader episodeDownloader, BackgroundTaskExecutor backgroundTaskExecutor, Localization localization) {
        super(LibraryTreeItemFactory.createLibraryRootItem(library));

        this.getColumns().add(LibraryTreeTableColumnFactory.createColumn(localization.title(), 400, 600, LibraryTreeItemValue::getTitle, TextFieldTreeTableCell::new, null));
        this.getColumns().add(LibraryTreeTableColumnFactory.createColumn(localization.date(), 80, 80, LibraryTreeItemValue::getDateString, TextFieldTreeTableCell::new, null));
        this.getColumns().add(LibraryTreeTableColumnFactory.createColumn(localization.duration(), 70, 70, LibraryTreeItemValue::getDurationString, TextFieldTreeTableCell::new, null));
        this.getColumns().add(LibraryTreeTableColumnFactory.createColumn(localization.download(), 105, 105, LibraryTreeItemValue::getDownloadState, () -> new InternalDownloadStateTreeTableCell(localization), null));
        this.getColumns().add(LibraryTreeTableColumnFactory.createColumn(localization.progress(), 80, 80, LibraryTreeItemValue::getDownloadProgress, InternalDownloadProgressBarTreeTableCell::new, null));
        this.getColumns().add(LibraryTreeTableColumnFactory.createColumn(null, 50, 50, LibraryTreeItemValue::getDownloadProgressLabel, TextFieldTreeTableCell::new, "-fx-alignment: CENTER-RIGHT;"));
        this.getColumns().add(LibraryTreeTableColumnFactory.createColumn(localization.description(), 200, Double.MAX_VALUE, LibraryTreeItemValue::getDescription, TextFieldTreeTableCell::new, null));
        this.setColumnResizePolicy(TreeTableView.CONSTRAINED_RESIZE_POLICY);
        this.setSortMode(TreeSortMode.ONLY_FIRST_LEVEL);
        this.setRowFactory(new LibraryTreeTableRowFactory(() -> new LibraryTreeTableSelection(this.getSelectionModel().getSelectedItems())));
        this.setContextMenu(new LibraryTreeTableContextMenu(() -> new LibraryTreeTableSelection(this.getSelectionModel().getSelectedItems()), library, episodeDownloader, backgroundTaskExecutor, localization));
        this.setOnKeyPressed(new LibraryTreeTableKeyEventHandler(() -> new LibraryTreeTableSelection(this.getSelectionModel().getSelectedItems()), library, episodeDownloader, backgroundTaskExecutor, localization));
        this.setOnMouseClicked(new LibraryTreeTableClickMouseEventHandler(() -> new LibraryTreeTableSelection(this.getSelectionModel().getSelectedItems()), episodeDownloader, backgroundTaskExecutor, localization));

        this.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        this.getStyleClass().add("podcastcentral-library");
        this.setShowRoot(false);
        this.setEditable(false);

    }

    private static class InternalDownloadProgressBarTreeTableCell extends ProgressBarTreeTableCell<LibraryTreeItemValue> {

        @Override
        public void updateItem(Double item, boolean empty) {
            if (item == null) {
                super.updateItem(item, true);
            } else {
                super.updateItem(item, empty);
            }
        }

    }

    private static class InternalDownloadStateTreeTableCell extends TreeTableCell<LibraryTreeItemValue, EpisodeDownloadState> {

        private Localization localization = null;

        private InternalDownloadStateTreeTableCell(Localization localization) {
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
