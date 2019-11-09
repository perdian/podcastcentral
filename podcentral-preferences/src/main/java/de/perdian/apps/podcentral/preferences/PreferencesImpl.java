package de.perdian.apps.podcentral.preferences;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableMap;
import javafx.collections.WeakMapChangeListener;

class PreferencesImpl implements Preferences {

    private File applicationDirectory = null;
    private ObservableMap<String, String> values = null;
    private Map<String, StringProperty> properties = new HashMap<>();

    @Override
    public synchronized Optional<String> getString(String key) {
        return Optional.ofNullable(this.getValues().get(key));
    }

    @Override
    public synchronized StringProperty getStringProperty(String key, String defaultValue) {
        StringProperty property = this.getProperties().get(key);
        if (property != null) {
            return property;
        } else {
            StringProperty newProperty = new SimpleStringProperty(this.getString(key).orElse(defaultValue));
            MapChangeListener<String, String> mapChangeListener = event -> {
                if (Objects.equals(key, event.getKey())) {
                    if (!Objects.equals(newProperty.getValue(), event.getValueAdded())) {
                        newProperty.setValue(event.getValueAdded());
                    }
                }
            };
            this.getValues().addListener(new WeakMapChangeListener<>(mapChangeListener));
            newProperty.addListener((o, oldValue, newValue) -> {
                if (!Objects.equals(oldValue, newValue) && !Objects.equals(newValue, this.getValues().get(key))) {
                    this.getValues().put(key, newValue);
                }
            });
            this.getProperties().put(key, newProperty);
            return newProperty;
        }
    }

    @Override
    public synchronized IntegerProperty getIntegerProperty(String key, int defaultValue) {
        StringProperty stringProperty = this.getStringProperty(key, null);
        IntegerProperty integerProperty = new SimpleIntegerProperty(StringUtils.isEmpty(stringProperty.getValue()) ? defaultValue : Integer.parseInt(stringProperty.getValue()));
        integerProperty.addListener((o, oldValue, newValue) -> {
            if (!Objects.equals(oldValue, newValue)) {
                stringProperty.setValue(String.valueOf(newValue));
            }
        });
        stringProperty.addListener((o, oldValue, newValue) -> {
            if (!Objects.equals(oldValue, newValue)) {
                integerProperty.setValue(Integer.parseInt(newValue));
            }
        });
        return integerProperty;
    }

    public synchronized Properties toProperties() {
        Properties properties = new Properties();
        this.getValues().forEach((key, value) -> properties.setProperty(key, value));
        return properties;
    }

    @Override
    public File getApplicationDirectory() {
        return this.applicationDirectory;
    }
    void setApplicationDirectory(File applicationDirectory) {
        this.applicationDirectory = applicationDirectory;
    }

    ObservableMap<String, String> getValues() {
        return this.values;
    }
    void setValues(ObservableMap<String, String> values) {
        this.values = values;
    }

    Map<String, StringProperty> getProperties() {
        return this.properties;
    }
    void setProperties(Map<String, StringProperty> properties) {
        this.properties = properties;
    }

}
