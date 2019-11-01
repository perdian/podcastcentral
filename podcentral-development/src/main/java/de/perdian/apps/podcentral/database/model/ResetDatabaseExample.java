package de.perdian.apps.podcentral.database.model;

import de.perdian.apps.podcentral.core.model.FeedInputOptions;
import de.perdian.apps.podcentral.preferences.Preferences;
import de.perdian.apps.podcentral.preferences.PreferencesFactory;
import de.perdian.apps.podcentral.retrieval.FeedInputFactory;
import de.perdian.apps.podcentral.storage.StorageFactory;

public class ResetDatabaseExample {

    public static void main(String[] args) throws Exception {
        FeedInputOptions feedInputOptions = new FeedInputOptions();
        Preferences preferences = PreferencesFactory.createPreferences();
        try (DatabaseBackedLibrary library = new DatabaseBackedLibraryBuilder().buildLibrary(StorageFactory.createStorage(), preferences)) {
            library.updateFeedFromInput(FeedInputFactory.getFeedInput("https://podcasts.files.bbci.co.uk/w13xttx2.rss"), feedInputOptions);
            library.updateFeedFromInput(FeedInputFactory.getFeedInput("http://omegataupodcast.net/category/podcast/feed"), feedInputOptions);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.exit(0);
    }

}
