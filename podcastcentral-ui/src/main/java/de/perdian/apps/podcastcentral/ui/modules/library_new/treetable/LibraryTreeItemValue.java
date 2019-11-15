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
package de.perdian.apps.podcastcentral.ui.modules.library_new.treetable;

import de.perdian.apps.podcastcentral.model.Episode;
import de.perdian.apps.podcastcentral.model.Feed;
import javafx.beans.value.ObservableValue;

interface LibraryTreeItemValue {

    ObservableValue<String> getTitle();

    static class FeedItemValue implements LibraryTreeItemValue {

        private Feed feed = null;

        FeedItemValue(Feed feed) {
            this.setFeed(feed);
        }

        @Override
        public ObservableValue<String> getTitle() {
            return this.getFeed().getTitle();
        }

        Feed getFeed() {
            return this.feed;
        }
        private void setFeed(Feed feed) {
            this.feed = feed;
        }

    }

    static class EpisodeItemValue implements LibraryTreeItemValue {

        private Feed feed = null;
        private Episode episode = null;

        EpisodeItemValue(Feed feed, Episode episode) {
            this.setFeed(feed);
            this.setEpisode(episode);
        }

        @Override
        public ObservableValue<String> getTitle() {
            return this.getEpisode().getTitle();
        }

        Feed getFeed() {
            return this.feed;
        }
        private void setFeed(Feed feed) {
            this.feed = feed;
        }

        Episode getEpisode() {
            return this.episode;
        }
        private void setEpisode(Episode episode) {
            this.episode = episode;
        }

    }

}
