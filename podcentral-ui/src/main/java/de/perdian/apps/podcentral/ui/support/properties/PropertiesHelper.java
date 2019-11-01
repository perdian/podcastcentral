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
package de.perdian.apps.podcentral.ui.support.properties;

import java.util.Objects;
import java.util.function.Function;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.WeakChangeListener;

public class PropertiesHelper {

    public static <S, T> ObjectProperty<T> map(Property<S> sourceProperty, Function<S, T> toConverter, Function<T, S> fromConverter) {
        ObjectProperty<T> targetProperty = new SimpleObjectProperty<>(toConverter.apply(sourceProperty.getValue()));
        sourceProperty.addListener(new WeakChangeListener<>((o, oldValue, newValue) -> {
            T newValueConverted = toConverter.apply(newValue);
            if (!Objects.equals(newValueConverted, targetProperty.getValue())) {
                targetProperty.setValue(newValueConverted);
            }
        }));
        targetProperty.addListener((o, oldValue, newValue) -> {
            if (fromConverter == null) {
                throw new IllegalArgumentException("No converter registered!");
            } else {
                S newValueConverted = fromConverter.apply(newValue);
                if (!Objects.equals(newValueConverted, sourceProperty.getValue())) {
                    sourceProperty.setValue(newValueConverted);
                }
            }
        });
        return targetProperty;
    }

}