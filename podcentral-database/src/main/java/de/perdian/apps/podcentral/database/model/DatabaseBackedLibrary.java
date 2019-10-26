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

import de.perdian.apps.podcentral.core.model.Feed;
import de.perdian.apps.podcentral.core.model.FeedInput;
import de.perdian.apps.podcentral.core.model.Library;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

class DatabaseBackedLibrary implements Library {

    private ObservableList<Feed> feeds = FXCollections.observableArrayList();

    @Override
    public void addFeedForInput(FeedInput feedInput) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ObservableList<Feed> getFeeds() {
        return this.feeds;
    }
    void setFeeds(ObservableList<Feed> feeds) {
        this.feeds = feeds;
    }

}
