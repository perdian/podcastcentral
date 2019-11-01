package de.perdian.apps.podcentral.preferences;

import java.io.File;
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

class PreferencesImpl implements Preferences {

    private File applicationDirectory = null;
    private ObservableMap<String, String> values = null;

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

}
