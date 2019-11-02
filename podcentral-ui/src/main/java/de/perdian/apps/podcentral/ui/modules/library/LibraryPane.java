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

import de.perdian.apps.podcentral.model.Library;
import de.perdian.apps.podcentral.retrieval.FeedInputLoader;
import de.perdian.apps.podcentral.ui.localization.Localization;
import de.perdian.apps.podcentral.ui.modules.feeds.inputloader.FeedInputLoaderPane;
import javafx.geometry.Insets;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

public class LibraryPane extends GridPane {

    public LibraryPane(Library library, FeedInputLoader feedInputLoader, Localization localization) {

        LibraryToolbarPane toolbarPane = new LibraryToolbarPane(library, localization);
        GridPane.setMargin(toolbarPane, new Insets(0, 0, 4, 0));
        GridPane.setHgrow(toolbarPane, Priority.ALWAYS);

        LibraryTreeTableView treeTableView = new LibraryTreeTableView(library, feedInputLoader, localization);
        GridPane.setHgrow(treeTableView, Priority.ALWAYS);
        GridPane.setVgrow(treeTableView, Priority.ALWAYS);

        FeedInputLoaderPane feedInputLoaderPane = new FeedInputLoaderPane(feedInputLoader, localization);
        GridPane.setMargin(feedInputLoaderPane, new Insets(4, 0, 0, 0));
        GridPane.setHgrow(feedInputLoaderPane, Priority.ALWAYS);

        this.add(toolbarPane, 0, 0, 1, 1);
        this.add(treeTableView, 0, 1, 1, 1);
        this.add(feedInputLoaderPane, 0, 2, 1, 1);

    }

}
