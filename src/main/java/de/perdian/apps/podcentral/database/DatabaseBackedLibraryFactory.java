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
package de.perdian.apps.podcentral.database;

import de.perdian.apps.podcentral.core.model.Library;
import de.perdian.apps.podcentral.core.web.ChannelLoader;

public class DatabaseBackedLibraryFactory {

    public static Library createLibrary() {

        ChannelLoader channelLoader = new ChannelLoader();

        DatabaseBackedLibrary library = new DatabaseBackedLibrary();
try {
    library.getChannels().add(channelLoader.loadChannel("https://podcasts.files.bbci.co.uk/w13xttx2.rss"));
    library.getChannels().add(channelLoader.loadChannel("https://www1.wdr.de/mediathek/audio/zeitzeichen/zeitzeichen-podcast-100.podcast"));
} catch (Exception e) {
    e.printStackTrace();
}
        return library;

    }

}
