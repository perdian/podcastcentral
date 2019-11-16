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

import java.util.ArrayList;
import java.util.List;

import de.perdian.apps.podcastcentral.model.Episode;
import de.perdian.apps.podcastcentral.model.Feed;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.scene.control.TreeTableRow;
import javafx.scene.control.TreeTableView;
import javafx.util.Callback;

class LibraryTreeTableRowFactory implements Callback<TreeTableView<LibraryTreeItemValue>, TreeTableRow<LibraryTreeItemValue>> {

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

        row.treeItemProperty().addListener((o, oldValue, newValue) -> {
            LibraryTreeItemValue oldTreeItemValue = oldValue == null ? null : oldValue.getValue();
            LibraryTreeItemValue newTreeItemValue = newValue == null ? null : newValue.getValue();
            if (oldTreeItemValue instanceof LibraryTreeItemValue.EpisodeItemValue) {
                ((LibraryTreeItemValue.EpisodeItemValue)oldTreeItemValue).getEpisode().getRead().removeListener(readPropertyChangeListener);
            }
            row.getStyleClass().removeIf(styleClass -> styleClass.startsWith("podcastcentral-"));
            if (newTreeItemValue instanceof LibraryTreeItemValue.FeedItemValue) {
                row.getStyleClass().addAll(LibraryTreeTableRowFactory.computeStyleClassesForFeed(((LibraryTreeItemValue.FeedItemValue)newTreeItemValue).getFeed()));
            } else if (newTreeItemValue instanceof LibraryTreeItemValue.EpisodeItemValue) {
                row.getStyleClass().addAll(LibraryTreeTableRowFactory.computeStyleClassesForEpisode(((LibraryTreeItemValue.EpisodeItemValue)newTreeItemValue).getEpisode()));
                ((LibraryTreeItemValue.EpisodeItemValue)newTreeItemValue).getEpisode().getRead().addListener(readPropertyChangeListener);
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

}
