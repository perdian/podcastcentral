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

public class StorageDirectory {

    private File directory = null;

    public StorageDirectory(File directory) {
        this.setDirectory(directory);
    }

    public File resolveFile(String fileName) {
        return new File(this.getDirectory(), StorageHelper.cleanupFileName(fileName));
    }

    File getDirectory() {
        return this.directory;
    }
    private void setDirectory(File directory) {
        this.directory = directory;
    }

    public void delete() {
        File directory = this.getDirectory();
        if (directory.exists()) {
            File[] directoryChildren = directory.listFiles();
            if (directoryChildren != null && directoryChildren.length > 0) {
                for (File directoryChild : directoryChildren) {
                    directoryChild.delete();
                }
            }
            directory.delete();
        }
    }

}
