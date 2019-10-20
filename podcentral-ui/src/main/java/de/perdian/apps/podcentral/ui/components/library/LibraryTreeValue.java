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
package de.perdian.apps.podcentral.ui.components.library;

import de.perdian.apps.podcentral.core.model.Channel;
import de.perdian.apps.podcentral.core.model.Episode;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

interface LibraryTreeValue {

    static class ChannelTreeValue implements LibraryTreeValue {

        private Channel channel = null;

        ChannelTreeValue(Channel channel) {
            this.setChannel(channel);
        }

        @Override
        public StringProperty getTitle() {
            return this.getChannel().getTitle();
        }

        private Channel getChannel() {
            return this.channel;
        }
        private void setChannel(Channel channel) {
            this.channel = channel;
        }

    }

    static class EpisodeTreeValue implements LibraryTreeValue {

        private Episode episode = null;

        EpisodeTreeValue(Episode episode) {
            this.setEpisode(episode);
        }

        @Override
        public StringProperty getTitle() {
            return new SimpleStringProperty("X");
        }

        private Episode getEpisode() {
            return this.episode;
        }
        private void setEpisode(Episode episode) {
            this.episode = episode;
        }

    }

    StringProperty getTitle();

}