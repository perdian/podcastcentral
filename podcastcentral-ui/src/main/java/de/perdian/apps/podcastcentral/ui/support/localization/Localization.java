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
package de.perdian.apps.podcastcentral.ui.support.localization;

import java.text.DecimalFormat;
import java.text.NumberFormat;

public interface Localization {

    default String activeDownloads() {
        return "Active downloads";
    }

    default String addFeed() {
        return "Add feed";
    }

    default String addingFeed() {
        return "Adding feed";
    }

    default String bytesOfBytesTransfered(long bytesWritten, long bytesTotal) {
        NumberFormat fileSizeFormat = new DecimalFormat("#,##0");
        StringBuilder progressMessage = new StringBuilder();
        progressMessage.append(fileSizeFormat.format((double)bytesWritten / 1024)).append(" KiB");
        progressMessage.append(" / ").append(fileSizeFormat.format((double)bytesTotal / 1024)).append(" KiB");
        progressMessage.append(" transfered");
        return progressMessage.toString();
    }

    default String cancel() {
        return "Cancel";
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

    default String cannotLoadFeedFromUrl() {
        return "Cannot load feed from URL:";
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

    default String deletingEpisodesFromFeed(int numberOfEpisodes, String feedName) {
        return "Deleting " + numberOfEpisodes + " episodes from feed: " + feedName;
    }

    default String deletingFeed(String feedName) {
        return "Deleting feed: " + feedName;
    }

    default String description() {
        return "Description";
    }

    default String download() {
        return "Download";
    }

    default String downloadEpisodes() {
        return "Download episodes";
    }

    default String downloadNewEpisodes() {
        return "Download new episodes";
    }

    default String downloading() {
        return "Downloading";
    }

    default String downloads() {
        return "Downloads";
    }

    default String downloads(int value) {
        return value + " downloads";
    }

    default String downloadsScheduled(int value) {
        return value + " downloads scheduled";
    }

    default String duration() {
        return "Duration";
    }

    default String episode() {
        return "Episode";
    }

    default String episodes() {
        return "Episodes";
    }

    default String errored() {
        return "Errored";
    }

    default String export() {
        return "Export";
    }

    default String exportOpml() {
        return "Export OPML";
    }

    default String exportingLibraryAsOpml() {
        return "Exporting library as OPML";
    }

    default String feed() {
        return "Feed";
    }

    default String feedDetails() {
        return "Feed details";
    }

    default String feeds() {
        return "Feeds";
    }

    default String feedSource() {
        return "Feed source";
    }

    default String feedUrl() {
        return "Feed URL";
    }

    default String importOpml() {
        return "Import OPML";
    }

    default String importingFeedsFromOpmlFile() {
        return "Importing feeds from OPML file";
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

    default String loading() {
        return "Loading...";
    }

    default String loadingApplicationData() {
        return "Loading application data...";
    }

    default String loadingFeedFrom() {
        return "Loading feed from: ";
    }

    default String loadingLibrary() {
        return "Loading library...";
    }

    default String markAsRead() {
        return "Mark as read";
    }

    default String markAsUnRead() {
        return "Mark as unread";
    }

    default String markingEpisodes() {
        return "Marking episodes";
    }

    default String missing() {
        return "Missing";
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

    default String noImage() {
        return "No image";
    }

    default String numberOfParallelDownloads() {
        return "Number of parallel downloads";
    }

    default String openEpisode() {
        return "Open episode";
    }

    default String owner() {
        return "Owner";
    }

    default String podcastCentral() {
        return "PodcastCentral";
    }

    default String progress() {
        return "Progress";
    }

    default String reallyDeleteFeeds(int numberOfFeeds) {
        return "Really delete " + numberOfFeeds + " feeds?";
    }

    default String refresh() {
        return "Refresh";
    }

    default String refreshAllFeeds() {
        return "Refresh all feeds";
    }

    default String refreshingFeed(String feedName) {
        return "Refreshing feed: " + feedName;
    }

    default String refreshingFeeds() {
        return "Refreshing feeds";
    }

    default String refreshRestoreDeletedEpisodes() {
        return "Refresh (restore deleted episodes)";
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

    default String selectTargetFile() {
        return "Select target file";
    }

    default String settings() {
        return "Settings";
    }

    default String subtitle() {
        return "Subtitle";
    }

    default String title() {
        return "Title";
    }

    default String unknown() {
        return "Unknown";
    }

}
