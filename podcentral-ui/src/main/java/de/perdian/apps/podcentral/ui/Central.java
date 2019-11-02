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

import java.util.ServiceLoader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.perdian.apps.podcentral.model.Library;
import de.perdian.apps.podcentral.model.LibraryBuilder;
import de.perdian.apps.podcentral.preferences.Preferences;
import de.perdian.apps.podcentral.preferences.PreferencesFactory;
import de.perdian.apps.podcentral.retrieval.FeedInputLoader;
import de.perdian.apps.podcentral.retrieval.FeedInputLoaderFactory;
import de.perdian.apps.podcentral.scheduler.Scheduler;
import de.perdian.apps.podcentral.scheduler.SchedulerFactory;
import de.perdian.apps.podcentral.storage.Storage;
import de.perdian.apps.podcentral.storage.StorageFactory;
import de.perdian.apps.podcentral.ui.localization.Localization;

public class Central {

    private static final Logger log = LoggerFactory.getLogger(Central.class);

    private Preferences preferences = null;
    private Library library = null;
    private FeedInputLoader feedInputLoader = null;
    private Scheduler scheduler = null;
    private Storage storage = null;

    public Central(Localization localization) {

        log.info("Loading preferences");
        Preferences preferences = PreferencesFactory.createPreferences();
        this.setPreferences(preferences);

        log.info("Creating scheduler");
        this.setScheduler(SchedulerFactory.createScheduler());

        log.info("Creating storage");
        Storage storage = StorageFactory.createStorage();
        this.setStorage(storage);

        log.info("Creating feed input loader");
        FeedInputLoader feedInputLoader = FeedInputLoaderFactory.createFeedInputLoader();
        this.setFeedInputLoader(feedInputLoader);

        log.info("Loading library");
        LibraryBuilder libraryBuilder = ServiceLoader.load(LibraryBuilder.class).findFirst().orElseThrow(() -> new IllegalArgumentException("Cannot find ServiceLoader for class: " + LibraryBuilder.class.getName()));
        this.setLibrary(libraryBuilder.buildLibrary(storage, preferences));

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

    public FeedInputLoader getFeedInputLoader() {
        return this.feedInputLoader;
    }
    private void setFeedInputLoader(FeedInputLoader feedInputLoader) {
        this.feedInputLoader = feedInputLoader;
    }

    public Scheduler getScheduler() {
        return this.scheduler;
    }
    private void setScheduler(Scheduler scheduler) {
        this.scheduler = scheduler;
    }

    public Storage getStorage() {
        return this.storage;
    }
    private void setStorage(Storage storage) {
        this.storage = storage;
    }

}
