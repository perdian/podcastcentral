package de.perdian.apps.podcastcentral.database.model;

import de.perdian.apps.podcastcentral.database.model.DatabaseBackedLibrary;
import de.perdian.apps.podcastcentral.database.model.DatabaseBackedLibraryBuilder;
import de.perdian.apps.podcastcentral.model.Feed;
import de.perdian.apps.podcastcentral.preferences.Preferences;
import de.perdian.apps.podcastcentral.preferences.PreferencesFactory;
import de.perdian.apps.podcastcentral.sources.feeds.FeedInputLoader;
import de.perdian.apps.podcastcentral.storage.Storage;

public class ResetDatabaseExample {

    public static void main(String[] args) throws Exception {

        Preferences preferences = PreferencesFactory.createPreferences();
        try (DatabaseBackedLibrary library = new DatabaseBackedLibraryBuilder().buildLibrary(Storage.createInstance(preferences), preferences)) {
            library.addFeed(FeedInputLoader.loadFeedInputFromUrl("https://podcasts.files.bbci.co.uk/w13xttx2.rss"), Feed.RefreshOption.OVERWRITE_CHANGED_VALUES);
            library.addFeed(FeedInputLoader.loadFeedInputFromUrl("http://omegataupodcast.net/category/podcast/feed"), Feed.RefreshOption.OVERWRITE_CHANGED_VALUES);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.exit(0);
    }

}
