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
import java.io.IOException;

public class Storage {

    private File rootDirectory = null;

    Storage(File rootDirectory) {
        this.setRootDirectory(rootDirectory);
    }

    public File resolveFileRelative(String... locations) {
        File file = this.getRootDirectory();
        for (String location : locations) {
            file = new File(file, StorageHelper.cleanupFileName(location));
        }
        try {
            return file.getCanonicalFile();
        } catch (IOException e) {
            return file;
        }
    }

    public File resolveFileAbsolute(String location) {
        try {
            return new File(location).getCanonicalFile();
        } catch (IOException e) {
            return new File(location);
        }
    }

    File getRootDirectory() {
        return this.rootDirectory;
    }
    private void setRootDirectory(File rootDirectory) {
        this.rootDirectory = rootDirectory;
    }

}
