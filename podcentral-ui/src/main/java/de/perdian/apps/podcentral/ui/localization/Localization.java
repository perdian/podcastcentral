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
package de.perdian.apps.podcentral.ui.localization;

public interface Localization {

    default String addFeed() {
        return "Add feed";
    }

    default String cannotLoadFeedFromUrl(String feedUrl) {
        return "Cannot load feed from URL: " + feedUrl;
    }

    default String feedUrl() {
        return "Feed URL";
    }

    default String loadFeed() {
        return "Load feed";
    }

    default String loadingFeedFrom() {
        return "Loading feed from:";
    }

    default String loadingLibrary() {
        return "Loading library...";
    }

    default String noFeedLoadedYet() {
        return "No feed loaded yet";
    }

    default String title() {
        return "Title";
    }

}
