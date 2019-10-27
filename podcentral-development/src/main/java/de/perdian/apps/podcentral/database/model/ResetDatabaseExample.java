package de.perdian.apps.podcentral.database.model;

import java.util.Properties;

import de.perdian.apps.podcentral.core.model.FeedInput;
import de.perdian.apps.podcentral.retrieval.FeedInputFactory;

public class ResetDatabaseExample {

    public static void main(String[] args) throws Exception {
        try (DatabaseBackedLibrary library = new DatabaseBackedLibraryFactory().createLibrary(new Properties())) {

            FeedInput feedInput = FeedInputFactory.getFeedInput("https://podcasts.files.bbci.co.uk/w13xttx2.rss");
            library.addFeedForInput(feedInput);

        }
    }

}
