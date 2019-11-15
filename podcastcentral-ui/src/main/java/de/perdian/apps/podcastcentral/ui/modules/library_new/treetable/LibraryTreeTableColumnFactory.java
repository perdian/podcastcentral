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

import java.util.function.Function;

import javafx.beans.value.ObservableValue;
import javafx.scene.control.TreeTableColumn;

class LibraryTreeTableColumnFactory {

    static <T> TreeTableColumn<LibraryTreeItemValue, T> createColumn(String title, Function<LibraryTreeItemValue, ObservableValue<T>> valueFunction) {
        TreeTableColumn<LibraryTreeItemValue, T> column = new TreeTableColumn<>(title);
        column.setCellValueFactory(cell -> cell.getValue() == null || cell.getValue().getValue() == null ? null : valueFunction.apply(cell.getValue().getValue()));
        column.setReorderable(false);
        return column;
    }

}
