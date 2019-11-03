package de.perdian.apps.podcentral.database.model;

import de.perdian.apps.podcentral.preferences.Preferences;
import de.perdian.apps.podcentral.preferences.PreferencesFactory;
import de.perdian.apps.podcentral.retrieval.FeedInputLoader;
import de.perdian.apps.podcentral.storage.StorageFactory;

public class ResetDatabaseExample {

    public static void main(String[] args) throws Exception {

        Preferences preferences = PreferencesFactory.createPreferences();
        try (DatabaseBackedLibrary library = new DatabaseBackedLibraryBuilder().buildLibrary(StorageFactory.createStorage(preferences), preferences)) {
            library.addFeed(FeedInputLoader.loadFeedInputFromUrl("https://podcasts.files.bbci.co.uk/w13xttx2.rss"));
            library.addFeed(FeedInputLoader.loadFeedInputFromUrl("http://omegataupodcast.net/category/podcast/feed"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.exit(0);
    }

}
