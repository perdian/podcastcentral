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
package de.perdian.apps.podcastcentral.ui.modules.library.components.treetable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import de.perdian.apps.podcastcentral.model.Episode;
import de.perdian.apps.podcastcentral.model.Feed;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.scene.control.TreeTableRow;
import javafx.scene.control.TreeTableView;
import javafx.util.Callback;

class LibraryTreeTableRowFactory implements Callback<TreeTableView<LibraryTreeItemValue>, TreeTableRow<LibraryTreeItemValue>> {

    private Supplier<LibraryTreeTableSelection> selectionSupplier = null;

    LibraryTreeTableRowFactory(Supplier<LibraryTreeTableSelection> selectionSupplier) {
        this.setSelectionSupplier(selectionSupplier);
    }

    @Override
    public TreeTableRow<LibraryTreeItemValue> call(TreeTableView<LibraryTreeItemValue> treeTableView) {
        TreeTableRow<LibraryTreeItemValue> row = new TreeTableRow<>();
        this.attachTreeItemListener(row);
        return row;
    }

    private void attachTreeItemListener(TreeTableRow<LibraryTreeItemValue> row) {

        ChangeListener<Boolean> readPropertyChangeListener = (readO, readOldValue, readNewValue) -> {
            Platform.runLater(() -> {
                row.getStyleClass().add(Boolean.TRUE.equals(readNewValue) ? "podcastcentral-read" : "podcastcentral-unread");
                row.getStyleClass().remove(Boolean.TRUE.equals(readNewValue) ? "podcastcentral-unread" : "podcastcentral-read");
            });
        };

        row.setOnDragDetected(new LibraryTreeTableDragMouseEventHandler(() -> this.getSelectionSupplier().get().getSelectedEpisodesDirectly()));
        row.treeItemProperty().addListener((o, oldValue, newValue) -> {
            LibraryTreeItemValue oldTreeItemValue = oldValue == null ? null : oldValue.getValue();
            LibraryTreeItemValue newTreeItemValue = newValue == null ? null : newValue.getValue();
            if (oldTreeItemValue != null && oldTreeItemValue.getEpisode() != null) {
                oldTreeItemValue.getEpisode().getRead().removeListener(readPropertyChangeListener);
            }
            row.getStyleClass().removeIf(styleClass -> styleClass.startsWith("podcastcentral-"));
            if (newTreeItemValue != null && newTreeItemValue.getFeed() != null) {
                row.getStyleClass().addAll(LibraryTreeTableRowFactory.computeStyleClassesForFeed(newTreeItemValue.getFeed()));
            } else if (newTreeItemValue != null && newTreeItemValue.getEpisode() != null) {
                row.getStyleClass().addAll(LibraryTreeTableRowFactory.computeStyleClassesForEpisode(newTreeItemValue.getEpisode()));
                newTreeItemValue.getEpisode().getRead().addListener(readPropertyChangeListener);
            }
        });

    }

    private static List<String> computeStyleClassesForFeed(Feed feed) {
        return List.of("podcastcentral-feed");
    }

    private static List<String> computeStyleClassesForEpisode(Episode episode) {
        List<String> styleClasses = new ArrayList<>(List.of("podcastcentral-episode"));
        styleClasses.add(Boolean.TRUE.equals(episode.getRead().getValue()) ? "podcastcentral-read" : "podcastcentral-unread");
        return styleClasses;
    }

    private Supplier<LibraryTreeTableSelection> getSelectionSupplier() {
        return this.selectionSupplier;
    }
    private void setSelectionSupplier(Supplier<LibraryTreeTableSelection> selectionSupplier) {
        this.selectionSupplier = selectionSupplier;
    }

}
