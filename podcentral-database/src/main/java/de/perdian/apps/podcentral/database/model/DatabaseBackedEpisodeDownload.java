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
package de.perdian.apps.podcentral.database.model;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.SessionFactory;

import de.perdian.apps.podcentral.core.model.EpisodeDownload;
import de.perdian.apps.podcentral.core.model.EpisodeDownloadState;
import de.perdian.apps.podcentral.database.entities.EpisodeEntity;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

class DatabaseBackedEpisodeDownload implements EpisodeDownload {

    private ObjectProperty<EpisodeDownloadState> state = null;
    private ObjectProperty<Instant> date = null;
    private ObjectProperty<Long> totalBytes = null;
    private ObjectProperty<Path> localPath = null;
    private ObjectProperty<Long> localBytes = null;
    private ObjectProperty<Double> progress = null;

    DatabaseBackedEpisodeDownload(EpisodeEntity episodeEntity, SessionFactory sessionFactory) {

        DatabasePropertyFactory<EpisodeEntity> propertyFactory = new DatabasePropertyFactory<>(episodeEntity, sessionFactory);
        this.setDate(propertyFactory.createProperty(e -> e.getDownloadDate(), (e, v) -> e.setDownloadDate(v), SimpleObjectProperty::new));
        this.setLocalBytes(new SimpleObjectProperty<>());
        this.setLocalPath(propertyFactory.createProperty(e -> StringUtils.isEmpty(e.getLocalPath()) ? null : Paths.get(e.getLocalPath()), (e, v) -> e.setLocalPath(v == null ? null : v.toString()), SimpleObjectProperty::new));
        this.setProgress(new SimpleObjectProperty<>());
        this.setState(new SimpleObjectProperty<>());
        this.setTotalBytes(propertyFactory.createProperty(e -> e.getData().getSize(), (e, v) -> e.getData().setSize(v), SimpleObjectProperty::new));

    }

    @Override
    public ObjectProperty<EpisodeDownloadState> getState() {
        return this.state;
    }
    private void setState(ObjectProperty<EpisodeDownloadState> state) {
        this.state = state;
    }

    @Override
    public ObjectProperty<Instant> getDate() {
        return this.date;
    }
    private void setDate(ObjectProperty<Instant> date) {
        this.date = date;
    }

    @Override
    public ObjectProperty<Long> getTotalBytes() {
        return this.totalBytes;
    }
    private void setTotalBytes(ObjectProperty<Long> totalBytes) {
        this.totalBytes = totalBytes;
    }

    @Override
    public ObjectProperty<Path> getLocalPath() {
        return this.localPath;
    }
    private void setLocalPath(ObjectProperty<Path> localPath) {
        this.localPath = localPath;
    }

    @Override
    public ObjectProperty<Long> getLocalBytes() {
        return this.localBytes;
    }
    private void setLocalBytes(ObjectProperty<Long> localBytes) {
        this.localBytes = localBytes;
    }

    @Override
    public ObjectProperty<Double> getProgress() {
        return this.progress;
    }
    private void setProgress(ObjectProperty<Double> progress) {
        this.progress = progress;
    }

}
