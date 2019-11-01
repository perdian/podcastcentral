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

import de.perdian.apps.podcentral.core.LibraryBuilderFactory;
import de.perdian.apps.podcentral.core.model.Library;
import de.perdian.apps.podcentral.preferences.Preferences;
import de.perdian.apps.podcentral.preferences.PreferencesFactory;
import de.perdian.apps.podcentral.scheduler.Scheduler;
import de.perdian.apps.podcentral.scheduler.SchedulerFactory;
import de.perdian.apps.podcentral.ui.localization.Localization;

public class Central {

    private static final Logger log = LoggerFactory.getLogger(Central.class);

    private Preferences preferences = null;
    private Library library = null;
    private Scheduler scheduler = null;

    public Central(Localization localization) {

        log.info("Loading preferences");
        Preferences preferences = PreferencesFactory.loadPreferences();
        this.setPreferences(preferences);

        log.info("Loading library");
        LibraryBuilderFactory libraryBuilderFactory = ServiceLoader.load(LibraryBuilderFactory.class).findFirst().orElseThrow(() -> new IllegalArgumentException("Cannot find ServiceLoader for class: " + LibraryBuilderFactory.class.getName()));
        this.setLibrary(libraryBuilderFactory.createLibraryBuilder().buildLibrary(preferences.toProperties()));

        log.info("Creating scheduler");
        this.setScheduler(SchedulerFactory.createScheduler());

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

    public Scheduler getScheduler() {
        return this.scheduler;
    }
    private void setScheduler(Scheduler scheduler) {
        this.scheduler = scheduler;
    }

}
