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

import javafx.scene.control.TreeTableRow;
import javafx.scene.control.TreeTableView;
import javafx.util.Callback;

class LibraryTreeTableRowFactory implements Callback<TreeTableView<LibraryTreeItemValue>, TreeTableRow<LibraryTreeItemValue>> {

    @Override
    public TreeTableRow<LibraryTreeItemValue> call(TreeTableView<LibraryTreeItemValue> param) {
        TreeTableRow<LibraryTreeItemValue> row = new TreeTableRow<>();
        return row;
    }

}
