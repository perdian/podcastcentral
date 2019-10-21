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
package de.perdian.apps.podcentral.ui.components.library;

import de.perdian.apps.podcentral.core.model.Episode;
import de.perdian.apps.podcentral.core.model.Feed;
import de.perdian.apps.podcentral.core.model.Library;
import de.perdian.apps.podcentral.ui.localization.Localization;
import de.perdian.apps.podcentral.ui.support.TreeTableHelper;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.scene.layout.BorderPane;

public class LibraryPane extends BorderPane {

    public LibraryPane(Library library, Localization localization) {

        TreeItem<LibraryTreeValue> rootItem = new TreeItem<>(null);
        for (Feed feed : library.getFeeds()) {
            LibraryTreeItem feedTreeItem = new LibraryTreeItem(new LibraryTreeValue.FeedTreeValue(feed));
            for (Episode episode : feed.getEpisodes()) {
                feedTreeItem.setExpanded(true);
                feedTreeItem.getChildren().add(new LibraryTreeItem(new LibraryTreeValue.EpisodeTreeValue(episode)));
            }
            rootItem.getChildren().add(feedTreeItem);
        }

        TreeTableColumn<LibraryTreeValue, String> titleColumn = new TreeTableColumn<>();
        titleColumn.setMaxWidth(Double.MAX_VALUE);
        titleColumn.setText(localization.title());
        titleColumn.setCellValueFactory(p -> p.getValue() == null || p.getValue().getValue() == null ? new SimpleStringProperty("") : p.getValue().getValue().getTitle());

        TreeTableView<LibraryTreeValue> treeTableView = new TreeTableView<>(rootItem);
        treeTableView.setShowRoot(false);
        treeTableView.setColumnResizePolicy(TreeTableView.CONSTRAINED_RESIZE_POLICY);
        treeTableView.getColumns().add(TreeTableHelper.createColumn(LibraryTreeValue::getTitle, localization.title()));
        this.setCenter(treeTableView);

    }

}
