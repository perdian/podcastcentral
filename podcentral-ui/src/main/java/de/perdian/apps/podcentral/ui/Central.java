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
package de.perdian.apps.podcentral.ui;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.perdian.apps.podcentral.downloader.episodes.EpisodeDownloader;
import de.perdian.apps.podcentral.model.Episode;
import de.perdian.apps.podcentral.model.Feed;
import de.perdian.apps.podcentral.model.Library;
import de.perdian.apps.podcentral.model.LibraryBuilder;
import de.perdian.apps.podcentral.model.LibraryListener;
import de.perdian.apps.podcentral.preferences.Preferences;
import de.perdian.apps.podcentral.preferences.PreferencesFactory;
import de.perdian.apps.podcentral.storage.Storage;
import de.perdian.apps.podcentral.storage.StorageFactory;
import de.perdian.apps.podcentral.ui.support.backgroundtasks.BackgroundTaskExecutor;
import de.perdian.apps.podcentral.ui.support.localization.Localization;

public class Central {

    private static final Logger log = LoggerFactory.getLogger(Central.class);

    private Preferences preferences = null;
    private Library library = null;
    private BackgroundTaskExecutor backgroundTaskExecutor = null;
    private EpisodeDownloader episodeDownloader = null;
    private Storage storage = null;

    public Central(Localization localization) {

        log.info("Loading preferences");
        Preferences preferences = PreferencesFactory.createPreferences();
        this.setPreferences(preferences);

        log.info("Creating background task executor");
        this.setBackgroundTaskExecutor(BackgroundTaskExecutor.createInstance(preferences));

        log.info("Creating storage");
        Storage storage = StorageFactory.createStorage(preferences);
        this.setStorage(storage);

        log.info("Loading library");
        LibraryBuilder libraryBuilder = LibraryBuilder.createInstance();
        Library library = libraryBuilder.buildLibrary(storage, preferences);
        this.setLibrary(library);

        log.info("Creating episode content downloader");
        EpisodeDownloader episodeDownloader = EpisodeDownloader.createInstance();
        this.setEpisodeContentDownloader(episodeDownloader);
        library.addListener(new LibraryListener() {
            @Override public void onFeedDeleted(Feed feed) {
                feed.getEpisodes().forEach(episode -> episodeDownloader.cancelDownload(episode));
            }
            @Override public void onEpisodeDeleted(Feed feed, Episode episode) {
                episodeDownloader.cancelDownload(episode);
            }
        });

    }

    public Preferences getPreferences() {
        return this.preferences;
    }
    private void setPreferences(Preferences preferences) {
        this.preferences = preferences;
    }

    public Library getLibrary() {
        return this.library;
    }
    private void setLibrary(Library library) {
        this.library = library;
    }

    public BackgroundTaskExecutor getBackgroundTaskExecutor() {
        return this.backgroundTaskExecutor;
    }
    private void setBackgroundTaskExecutor(BackgroundTaskExecutor backgroundTaskExecutor) {
        this.backgroundTaskExecutor = backgroundTaskExecutor;
    }

    public EpisodeDownloader getEpisodeContentDownloader() {
        return this.episodeDownloader;
    }
    private void setEpisodeContentDownloader(EpisodeDownloader episodeDownloader) {
        this.episodeDownloader = episodeDownloader;
    }

    public Storage getStorage() {
        return this.storage;
    }
    private void setStorage(Storage storage) {
        this.storage = storage;
    }

}
