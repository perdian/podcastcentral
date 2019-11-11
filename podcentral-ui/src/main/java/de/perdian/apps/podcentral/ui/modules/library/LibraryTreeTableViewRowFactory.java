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

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.scene.control.TreeTableRow;
import javafx.scene.control.TreeTableView;
import javafx.util.Callback;

class LibraryTreeTableViewRowFactory implements Callback<TreeTableView<LibraryTreeTableValue>, TreeTableRow<LibraryTreeTableValue>> {

    private LibrarySelection librarySelection = null;

    LibraryTreeTableViewRowFactory(LibrarySelection librarySelection) {
        this.setLibrarySelection(librarySelection);
    }

    @Override
    public TreeTableRow<LibraryTreeTableValue> call(TreeTableView<LibraryTreeTableValue> param) {

        TreeTableRow<LibraryTreeTableValue> tableRow = new TreeTableRow<>();
        ChangeListener<Boolean> readPropertyChangeListener = (readO, readOldValue, readNewValue) -> {
            Platform.runLater(() -> {
                tableRow.getStyleClass().add(Boolean.TRUE.equals(readNewValue) ? "podcentral-read" : "podcentral-unread");
                tableRow.getStyleClass().remove(Boolean.TRUE.equals(readNewValue) ? "podcentral-unread" : "podcentral-read");
            });
        };
        tableRow.setOnDragDetected(new LibraryTreeTableMouseEventHandler(this.getLibrarySelection()));
        tableRow.treeItemProperty().addListener((o, oldValue, newValue) -> {
            LibraryTreeTableValue oldTreeTableValue = oldValue == null ? null : oldValue.getValue();
            LibraryTreeTableValue newTreeTableValue = newValue == null ? null : newValue.getValue();
            if (oldTreeTableValue instanceof LibraryTreeTableValue.EpisodeTreeValue) {
                ((LibraryTreeTableValue.EpisodeTreeValue)oldTreeTableValue).getEpisode().getRead().removeListener(readPropertyChangeListener);
            }
            if (newTreeTableValue instanceof LibraryTreeTableValue.FeedTreeValue) {
                tableRow.getStyleClass().removeAll("podcentral-episode", "podcentral-read", "podcentral-unread");
                tableRow.getStyleClass().addAll("podcentral-feed");
            } else if (newTreeTableValue instanceof LibraryTreeTableValue.EpisodeTreeValue) {
                LibraryTreeTableValue.EpisodeTreeValue newEpisodeTreeValue = (LibraryTreeTableValue.EpisodeTreeValue)newTreeTableValue;
                Boolean newReadValue = newEpisodeTreeValue.getEpisode().getRead().getValue();
                String newReadClass = Boolean.TRUE.equals(newReadValue) ? "podcentral-read" : "podcentral-unread";
                tableRow.getStyleClass().removeAll("podcentral-feed", "podcentral-read", "podcentral-unread");
                tableRow.getStyleClass().addAll("podcentral-episode", newReadClass);
                ((LibraryTreeTableValue.EpisodeTreeValue)newTreeTableValue).getEpisode().getRead().addListener(readPropertyChangeListener);
            }
        });
        return tableRow;

    }

    private LibrarySelection getLibrarySelection() {
        return this.librarySelection;
    }
    private void setLibrarySelection(LibrarySelection librarySelection) {
        this.librarySelection = librarySelection;
    }

}
