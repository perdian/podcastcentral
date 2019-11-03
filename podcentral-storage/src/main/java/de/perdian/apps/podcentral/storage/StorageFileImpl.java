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

import java.time.Instant;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.StringProperty;

class StorageFileImpl implements StorageFile {

    private StorageDirectory directory = null;
    private ObjectProperty<StorageState> state = null;
    private ObjectProperty<Long> totalBytes = null;
    private ObjectProperty<Long> downloadedBytes = null;
    private ObjectProperty<String> downloadLocation = null;
    private ObjectProperty<Double> downloadProgress = null;
    private ObjectProperty<Instant> downloadDate = null;

    StorageFileImpl(StorageDirectory directory, StringProperty fileNameProperty) {
        this.setDirectory(directory);
    }

    private StorageDirectory getDirectory() {
        return this.directory;
    }
    private void setDirectory(StorageDirectory directory) {
        this.directory = directory;
    }

    @Override
    public ObjectProperty<StorageState> getState() {
        return this.state;
    }
    private void setState(ObjectProperty<StorageState> state) {
        this.state = state;
    }

    @Override
    public ObjectProperty<Long> getTotalBytes() {
        return this.totalBytes;
    }
    private void setTotalBytes(ObjectProperty<Long> totalBytes) {
        this.totalBytes = totalBytes;
    }

    @Override
    public ObjectProperty<Long> getDownloadedBytes() {
        return this.downloadedBytes;
    }
    private void setDownloadedBytes(ObjectProperty<Long> downloadedBytes) {
        this.downloadedBytes = downloadedBytes;
    }

    @Override
    public ObjectProperty<String> getDownloadLocation() {
        return this.downloadLocation;
    }
    private void setDownloadLocation(ObjectProperty<String> downloadLocation) {
        this.downloadLocation = downloadLocation;
    }

    @Override
    public ObjectProperty<Double> getDownloadProgress() {
        return this.downloadProgress;
    }
    private void setDownloadProgress(ObjectProperty<Double> downloadProgress) {
        this.downloadProgress = downloadProgress;
    }

    @Override
    public ObjectProperty<Instant> getDownloadDate() {
        return this.downloadDate;
    }
    private void setDownloadDate(ObjectProperty<Instant> downloadDate) {
        this.downloadDate = downloadDate;
    }

}
