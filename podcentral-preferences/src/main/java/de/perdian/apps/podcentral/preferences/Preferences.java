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
package de.perdian.apps.podcentral.preferences;

import java.util.Objects;
import java.util.Optional;
import java.util.Properties;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.WeakChangeListener;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableMap;
import javafx.collections.WeakMapChangeListener;

public class Preferences {

    private ObservableMap<String, String> values = null;

    Preferences(ObservableMap<String, String> values) {
        this.setValues(values);
    }

    public Optional<String> getValue(String key) {
        return Optional.ofNullable(this.getValues().get(key));
    }

    public StringProperty getValueProperty(String key) {

        StringProperty property = new SimpleStringProperty(this.getValue(key).orElse(null));
        MapChangeListener<String, String> mapChangeListener = event -> {
            if (Objects.equals(key, event.getKey())) {
                if (!Objects.equals(property.getValue(), event.getValueAdded())) {
                    property.setValue(event.getValueAdded());
                }
            }
        };
        this.getValues().addListener(new WeakMapChangeListener<>(mapChangeListener));
        ChangeListener<String> propertyChangeListener = (o, oldValue, newValue) -> {
            if (!Objects.equals(oldValue, newValue) && !Objects.equals(newValue, this.getValues().get(key))) {
                this.getValues().put(key, newValue);
            }
        };
        property.addListener(new WeakChangeListener<>(propertyChangeListener));

        return property;

    }

    public Properties toProperties() {
        Properties properties = new Properties();
        this.getValues().forEach((key, value) -> properties.setProperty(key, value));
        return properties;
    }

    private ObservableMap<String, String> getValues() {
        return this.values;
    }
    private void setValues(ObservableMap<String, String> values) {
        this.values = values;
    }

}
