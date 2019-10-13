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
package de.perdian.apps.podcentral.model.preferences;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
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

    public static Preferences loadPreferences() {
        ObservableMap<String, String> values = FXCollections.observableHashMap();
        values.putAll(PreferencesFactory.loadValues());
        values.addListener((MapChangeListener.Change<?, ?> change) -> PreferencesFactory.writeValues(values));
        return new Preferences(values);
    }

    private static Map<String, String> loadValues() {
        Map<String, String> values = new HashMap<>();
        try {
            File preferencesFile = PreferencesFactory.resolvePreferencesFile(false);
            if (preferencesFile.exists()) {
                log.debug("Loading preferences from file: {}", preferencesFile.getAbsolutePath());
                try (InputStream preferencesStream = new BufferedInputStream(new FileInputStream(preferencesFile))) {
                    Properties properties = new Properties();
                    properties.load(preferencesStream);
                    properties.entrySet().stream().filter(entry -> entry.getKey() != null && entry.getValue() != null).forEach(entry -> values.put(entry.getKey().toString(), entry.getValue().toString()));
                }
            } else {
                log.trace("No preferences file found at: {}", preferencesFile.getAbsolutePath());
            }
        } catch (Exception e) {
            log.warn("Cannot load preferences", e);
        }
        return values;
    }

    private static void writeValues(Map<String, String> values) {
        try {
            Properties properties = new Properties();
            values.entrySet().stream().filter(entry -> entry.getKey() != null && entry.getValue() != null).forEach(entry -> properties.setProperty(entry.getKey(), entry.getValue()));
            File preferencesFile = PreferencesFactory.resolvePreferencesFile(true);
            log.trace("Writing preferences into file: {}", preferencesFile.getAbsolutePath());
            try (OutputStream preferencesStream = new BufferedOutputStream(new FileOutputStream(preferencesFile))) {
                properties.store(preferencesStream, null);
                preferencesStream.flush();
            }
        } catch (Exception e) {
            log.warn("Cannot write preferences", e);
        }
    }

    private static File resolvePreferencesFile(boolean createIfNotExisting) throws IOException {
        File userDirectory = new File(System.getProperty("user.home"));
        File podcentralDirectory = new File(userDirectory, ".podcentral");
        File preferencesFile = new File(podcentralDirectory, "preferences").getCanonicalFile();
        if (!preferencesFile.exists() && createIfNotExisting) {
            if (!preferencesFile.getParentFile().exists()) {
                preferencesFile.getParentFile().mkdirs();
            }
            preferencesFile.createNewFile();
        }
        return preferencesFile;
    }

}
