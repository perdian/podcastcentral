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
package de.perdian.apps.podcentral.storage;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.perdian.apps.podcentral.preferences.Preferences;

public class StorageFactory {

    private static final Logger log = LoggerFactory.getLogger(StorageFactory.class);

    public static Storage createStorage(Preferences preferences) {
        File downloadsDirectory = new File(preferences.getApplicationDirectory(), "downloads");
        log.info("Resolved storage location to directory: {}", downloadsDirectory);
        if (!downloadsDirectory.exists()) {
            log.info("Creating storage location at directory: {}", downloadsDirectory.getAbsolutePath());
            downloadsDirectory.mkdirs();
        }
        return new Storage(downloadsDirectory);
    }

}
