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

import java.io.File;
import java.util.Optional;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.StringProperty;

public interface Preferences {

    File getApplicationDirectory();
    Optional<String> getString(String key);
    StringProperty getStringProperty(String key, String defaultValue);
    IntegerProperty getIntegerProperty(String key, int defaultValue);

}
