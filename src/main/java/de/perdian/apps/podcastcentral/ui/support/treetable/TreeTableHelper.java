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
package de.perdian.apps.podcastcentral.ui.support.treetable;

import java.util.function.Function;

import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.TreeTableColumn;

public class TreeTableHelper {

    public static <S, T> TreeTableColumn<S, T> createColumn(Function<S, Property<T>> sourcePropertyFunction, String title) {
        TreeTableColumn<S, T> column = new TreeTableColumn<>(title);
        column.setCellValueFactory(cell -> cell.getValue() == null || cell.getValue().getValue() == null ? new SimpleObjectProperty<>() : sourcePropertyFunction.apply(cell.getValue().getValue()));
        column.setReorderable(false);
        return column;
    }

}
