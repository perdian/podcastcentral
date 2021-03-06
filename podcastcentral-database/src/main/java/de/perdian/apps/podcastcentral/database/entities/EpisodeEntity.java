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
package de.perdian.apps.podcastcentral.database.entities;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.apache.commons.lang3.builder.ToStringBuilder;

import de.perdian.apps.podcastcentral.model.EpisodeData;
import de.perdian.apps.podcastcentral.model.EpisodeDownloadState;

@Entity
@Table(name = "episode")
public class EpisodeEntity implements Serializable {

    static final long serialVersionUID = 1L;

    private Long id = null;
    private FeedEntity feed = null;
    private EpisodeData data = null;
    private EpisodeDownloadState downloadState = EpisodeDownloadState.NEW;
    private String contentFileLocation = null;
    private Boolean deleted = Boolean.FALSE;
    private Boolean read = Boolean.FALSE;

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

    @Override
    public String toString() {
        ToStringBuilder toStringBuilder = new ToStringBuilder(this);
        toStringBuilder.append("id", this.getId());
        toStringBuilder.append("title", this.getData().getTitle());
        return toStringBuilder.toString();
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

    public EpisodeDownloadState getDownloadState() {
        return this.downloadState;
    }
    public void setDownloadState(EpisodeDownloadState downloadState) {
        this.downloadState = downloadState;
    }

    @Column(length = 1000)
    public String getContentFileLocation() {
        return this.contentFileLocation;
    }
    public void setContentFileLocation(String contentFileLocation) {
        this.contentFileLocation = contentFileLocation;
    }

    public Boolean getDeleted() {
        return this.deleted;
    }
    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    public Boolean getRead() {
        return this.read;
    }
    public void setRead(Boolean read) {
        this.read = read;
    }

}
