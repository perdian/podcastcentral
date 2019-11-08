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

    default String activeDownloads() {
        return "Active downloads";
    }

    default String addFeed() {
        return "Add feed";
    }

    default String cancelAllDownloads() {
        return "Cancel all downloads";
    }

    default String cancelDownloads() {
        return "Cancel downloads";
    }

    default String cancelled() {
        return "Cancelled";
    }

    default String cancellingEpisodeDownloads() {
        return "Cancelling episode downloads";
    }

    default String cannotLoadFeedFromUrl(String feedUrl) {
        return "Cannot load feed from URL: " + feedUrl;
    }

    default String completed() {
        return "Completed";
    }

    default String date() {
        return "Date";
    }

    default String delete() {
        return "Delete";
    }

    default String deletingEntries() {
        return "Deleting entries";
    }

    default String description() {
        return "Description";
    }

    default String downloadSelectedEpisodes() {
        return "Download selected episodes";
    }

    default String downloadAllEpisodesFromFeed() {
        return "Download all episodes from feed";
    }

    default String downloading() {
        return "Downloading";
    }

    default String downloads() {
        return "Downloads";
    }

    default String downloadsActive(int value) {
        return value + " downloads active";
    }

    default String downloadsScheduled(int value) {
        return value + " downloads scheduled";
    }

    default String duration() {
        return "Duration";
    }

    default String errored() {
        return "Errored";
    }

    default String feedUrl() {
        return "Feed URL";
    }

    default String language() {
        return "Language";
    }

    default String library() {
        return "Library";
    }

    default String loadFeed() {
        return "Load feed";
    }

    default String loadingApplicationData() {
        return "Loading application data...";
    }

    default String loadingFeedFrom() {
        return "Loading feed from:";
    }

    default String loadingLibrary() {
        return "Loading library...";
    }

    default String new_() {
        return "New";
    }

    default String noDownloadsActive() {
        return "No downloads active";
    }

    default String noFeedLoadedYet() {
        return "No feed loaded yet";
    }

    default String owner() {
        return "Owner";
    }

    default String podCentral() {
        return "PodCentral";
    }

    default String refresh() {
        return "Refresh";
    }

    default String refreshRestoreDeletedEpisodes() {
        return "Refresh (restore deleted episodes)";
    }

    default String refreshFeeds() {
        return "Refresh feeds";
    }

    default String refreshingFeeds() {
        return "Refreshing feeds";
    }

    default String scheduled() {
        return "Scheduled";
    }

    default String scheduledDownloads() {
        return "Scheduled downloads";
    }

    default String schedulingEpisodeDownloads() {
        return "Scheduling episode downloads";
    }

    default String storage() {
        return "Storage";
    }

    default String title() {
        return "Title";
    }

}
