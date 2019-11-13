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
import javax.persistence.Table;

import org.apache.commons.lang3.builder.ToStringBuilder;

import de.perdian.apps.podcentral.model.FeedData;
import de.perdian.apps.podcentral.model.FeedInputState;

@Entity
@Table(name = "feed")
public class FeedEntity implements Serializable {

    static final long serialVersionUID = 1L;

    private Long id = null;
    private FeedData data = null;
    private FeedInputState inputState = null;
    private Boolean expanded = Boolean.TRUE;
    private Instant refreshTime = null;

    @Override
    public int hashCode() {
        return this.getId() == null ? 0 : this.getId().hashCode();
    }

    @Override
    public boolean equals(Object that) {
        if (this == that) {
            return true;
        } else if (that instanceof FeedEntity) {
            return this.getId() != null && this.getId().equals(((FeedEntity)that).getId());
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

    @Embedded
    public FeedData getData() {
        return this.data;
    }
    public void setData(FeedData data) {
        this.data = data;
    }

    public FeedInputState getInputState() {
        return this.inputState;
    }
    public void setInputState(FeedInputState inputState) {
        this.inputState = inputState;
    }

    public Boolean getExpanded() {
        return this.expanded;
    }
    public void setExpanded(Boolean expanded) {
        this.expanded = expanded;
    }

    public Instant getRefreshTime() {
        return this.refreshTime;
    }
    public void setRefreshTime(Instant refreshTime) {
        this.refreshTime = refreshTime;
    }

}
