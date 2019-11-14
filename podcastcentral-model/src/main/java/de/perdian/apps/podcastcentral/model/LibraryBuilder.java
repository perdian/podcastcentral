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
package de.perdian.apps.podcastcentral.model;

import java.util.ServiceLoader;

import de.perdian.apps.podcastcentral.preferences.Preferences;
import de.perdian.apps.podcastcentral.storage.Storage;

public interface LibraryBuilder {

    static LibraryBuilder createInstance() {
        return ServiceLoader.load(LibraryBuilder.class)
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Cannot find ServiceLoader for class: " + LibraryBuilder.class.getName()));
    }

    Library buildLibrary(Storage storage, Preferences preferences);

}
