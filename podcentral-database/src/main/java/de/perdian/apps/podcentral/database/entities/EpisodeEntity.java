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
package de.perdian.apps.podcentral.database.entities;

import java.io.Serializable;
import java.time.Instant;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import de.perdian.apps.podcentral.core.model.EpisodeData;
import de.perdian.apps.podcentral.core.model.EpisodeLocalState;

@Entity
@Table(name = "episode")
public class EpisodeEntity implements Serializable {

    static final long serialVersionUID = 1L;

    private Long id = null;
    private FeedEntity feed = null;
    private EpisodeData data = null;
    private Instant downloadDate = null;
    private EpisodeLocalState localState = null;

    @Override
    public int hashCode() {
        return this.getId() == null ? 0 : this.getId().hashCode();
    }

    @Override
    public boolean equals(Object that) {
        if (this == that) {
            return true;
        } else if (that instanceof EpisodeEntity) {
            return this.getId() != null && this.getId().equals(((EpisodeEntity)that).getId());
        } else {
            return false;
        }
    }

    @Id @GeneratedValue
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    @ManyToOne
    public FeedEntity getFeed() {
        return this.feed;
    }
    public void setFeed(FeedEntity feed) {
        this.feed = feed;
    }

    @Embedded
    public EpisodeData getData() {
        return this.data;
    }
    public void setData(EpisodeData data) {
        this.data = data;
    }

    public Instant getDownloadDate() {
        return this.downloadDate;
    }
    public void setDownloadDate(Instant downloadDate) {
        this.downloadDate = downloadDate;
    }

    public EpisodeLocalState getLocalState() {
        return this.localState;
    }
    public void setLocalState(EpisodeLocalState localState) {
        this.localState = localState;
    }


}
