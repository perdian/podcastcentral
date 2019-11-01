package de.perdian.apps.podcentral.database.model;

import java.util.Properties;

import de.perdian.apps.podcentral.retrieval.FeedInputFactory;

public class ResetDatabaseExample {

    public static void main(String[] args) throws Exception {
        try (DatabaseBackedLibrary library = new DatabaseBackedLibraryBuilder().buildLibrary(new Properties())) {

            library.addFeedForInput(FeedInputFactory.getFeedInput("https://podcasts.files.bbci.co.uk/w13xttx2.rss"));
            library.addFeedForInput(FeedInputFactory.getFeedInput("http://omegataupodcast.net/category/podcast/feed"));

        }
    }

}
