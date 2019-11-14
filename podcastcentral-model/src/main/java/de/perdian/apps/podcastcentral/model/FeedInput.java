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

import java.io.Serializable;
import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class FeedInput implements Serializable {

    static final long serialVersionUID = 1L;

    private FeedData data = null;
    private List<EpisodeData> episodes = null;

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

    public FeedData getData() {
        return this.data;
    }
    public void setData(FeedData data) {
        this.data = data;
    }

    public List<EpisodeData> getEpisodes() {
        return this.episodes;
    }
    public void setEpisodes(List<EpisodeData> episodes) {
        this.episodes = episodes;
    }

}
