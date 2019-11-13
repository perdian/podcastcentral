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
package de.perdian.apps.podcentral.sources.feedcollections.impl;

import java.util.List;

import de.perdian.apps.podcentral.model.FeedCollection;
import de.perdian.apps.podcentral.model.FeedCollectionItem;

class OpmlFeedCollection implements FeedCollection {

    private List<? extends FeedCollectionItem> items = null;

    OpmlFeedCollection(List<? extends FeedCollectionItem> items) {
        this.setItems(items);
    }

    @Override
    public List<? extends FeedCollectionItem> getItems() {
        return this.items;
    }
    private void setItems(List<? extends FeedCollectionItem> items) {
        this.items = items;
    }

}
