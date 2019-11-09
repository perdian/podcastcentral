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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableMap;

public class PreferencesFactory {

    private static final Logger log = LoggerFactory.getLogger(PreferencesFactory.class);

    public static Preferences createPreferences() {
        File applicationDirectory = PreferencesFactory.resolveApplicationDirectory();
        PreferencesImpl preferences = new PreferencesImpl();
        preferences.setApplicationDirectory(applicationDirectory);
        preferences.setValues(PreferencesFactory.resolveValues(applicationDirectory));
        return preferences;
    }

    private static File resolveApplicationDirectory() {
        File userDirectory = new File(System.getProperty("user.home"));
        File applicationDirectory = new File(userDirectory, ".podcastcentral");
        if (!applicationDirectory.exists()) {
            log.info("Creating application directory at: {}", applicationDirectory.getAbsolutePath());
            applicationDirectory.mkdirs();
        }
        return applicationDirectory;
    }

    private static ObservableMap<String, String> resolveValues(File applicationDirectory) {
        File valuesFile = new File(applicationDirectory, "preferences/values");
        ObservableMap<String, String> values = FXCollections.observableHashMap();
        values.putAll(PreferencesFactory.loadValues(valuesFile));
        values.addListener((MapChangeListener.Change<?, ?> change) -> PreferencesFactory.storeValues(values, valuesFile));
        return values;
    }

    private static Map<String, String> loadValues(File valuesFile) {
        Map<String, String> values = new HashMap<>();
        try {
            if (valuesFile.exists()) {
                log.debug("Loading preferences from file: {}", valuesFile.getAbsolutePath());
                try (InputStream preferencesStream = new BufferedInputStream(new FileInputStream(valuesFile))) {
                    Properties properties = new Properties();
                    properties.load(preferencesStream);
                    properties.entrySet().stream().filter(entry -> entry.getKey() != null && entry.getValue() != null).forEach(entry -> values.put(entry.getKey().toString(), entry.getValue().toString()));
                }
            } else {
                log.trace("No preferences file found at: {}", valuesFile.getAbsolutePath());
            }
        } catch (Exception e) {
            log.warn("Cannot load preferences from: {}", valuesFile.getAbsolutePath(), e);
        }
        return values;
    }

    private static void storeValues(Map<String, String> values, File valuesFile) {
        try {
            Properties properties = new Properties();
            values.entrySet().stream().filter(entry -> entry.getKey() != null && entry.getValue() != null).forEach(entry -> properties.setProperty(entry.getKey(), entry.getValue()));
            if (!valuesFile.getParentFile().exists()) {
                log.debug("Creating preferences directory at: {}", valuesFile.getParentFile());
                valuesFile.getParentFile().mkdirs();
            }
            log.trace("Storing preferences into file: {}", valuesFile.getAbsolutePath());
            try (OutputStream preferencesStream = new BufferedOutputStream(new FileOutputStream(valuesFile))) {
                properties.store(preferencesStream, null);
                preferencesStream.flush();
            }
        } catch (Exception e) {
            log.warn("Cannot write preferences", e);
        }
    }

}
