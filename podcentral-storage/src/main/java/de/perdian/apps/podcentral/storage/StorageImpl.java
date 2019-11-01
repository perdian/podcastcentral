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

class StorageImpl implements Storage {

    private File storageDirectory = null;

    StorageImpl(File storageDirectory) {
        this.setStorageDirectory(storageDirectory);
    }

    @Override
    public File resolveStorageFile(String feedName, String episodeName, String type) {
        throw new UnsupportedOperationException();
    }

    private File getStorageDirectory() {
        return this.storageDirectory;
    }
    private void setStorageDirectory(File storageDirectory) {
        this.storageDirectory = storageDirectory;
    }

}
