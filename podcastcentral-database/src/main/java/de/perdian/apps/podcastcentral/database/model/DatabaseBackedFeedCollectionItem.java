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
package de.perdian.apps.podcastcentral.database.model;

import de.perdian.apps.podcastcentral.model.FeedCollectionItem;
import javafx.beans.property.StringProperty;

class DatabaseBackedFeedCollectionItem implements FeedCollectionItem {

    private DatabaseBackedFeed feed = null;

    DatabaseBackedFeedCollectionItem(DatabaseBackedFeed feed) {
        this.setFeed(feed);
    }

    @Override
    public StringProperty getTitle() {
        return this.getFeed().getTitle();
    }

    @Override
    public StringProperty getFeedUrl() {
        return this.getFeed().getUrl();
    }

    @Override
    public StringProperty getWebsiteUrl() {
        return this.getFeed().getWebsiteUrl();
    }

    DatabaseBackedFeed getFeed() {
        return this.feed;
    }
    private void setFeed(DatabaseBackedFeed feed) {
        this.feed = feed;
    }

}
