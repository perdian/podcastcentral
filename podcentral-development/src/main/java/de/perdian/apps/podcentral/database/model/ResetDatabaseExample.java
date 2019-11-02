package de.perdian.apps.podcentral.database.model;

import de.perdian.apps.podcentral.model.FeedInputOptions;
import de.perdian.apps.podcentral.preferences.Preferences;
import de.perdian.apps.podcentral.preferences.PreferencesFactory;
import de.perdian.apps.podcentral.retrieval.FeedInputLoader;
import de.perdian.apps.podcentral.retrieval.FeedInputLoaderFactory;
import de.perdian.apps.podcentral.storage.StorageFactory;

public class ResetDatabaseExample {

    public static void main(String[] args) throws Exception {

        FeedInputLoader feedInputLoader = FeedInputLoaderFactory.createFeedInputLoader();
        feedInputLoader.getBusy().addListener((o, oldValue, newValue) -> {
            System.err.println("Busy set to: " + newValue);
        });
        feedInputLoader.getOverallProgress().addListener((o, oldValue, newValue) -> {
            System.err.println("Progress set to: " + newValue);
        });

        FeedInputOptions feedInputOptions = new FeedInputOptions();
        Preferences preferences = PreferencesFactory.createPreferences();
        try (DatabaseBackedLibrary library = new DatabaseBackedLibraryBuilder().buildLibrary(StorageFactory.createStorage(), preferences)) {
            library.updateFeedFromInput(feedInputLoader.submitFeedUrl("https://podcasts.files.bbci.co.uk/w13xttx2.rss").get(), feedInputOptions);
            library.updateFeedFromInput(feedInputLoader.submitFeedUrl("http://omegataupodcast.net/category/podcast/feed").get(), feedInputOptions);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.exit(0);
    }

}
