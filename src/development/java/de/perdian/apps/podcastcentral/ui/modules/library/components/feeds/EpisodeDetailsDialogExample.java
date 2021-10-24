package de.perdian.apps.podcastcentral.ui.modules.library.components.feeds;

import java.io.File;

import de.perdian.apps.podcastcentral.model.Feed;
import de.perdian.apps.podcastcentral.model.FeedInput;
import de.perdian.apps.podcastcentral.preferences.PreferencesFactory;
import de.perdian.apps.podcastcentral.sources.feeds.FeedInputLoader;
import de.perdian.apps.podcastcentral.ui.Central;
import de.perdian.apps.podcastcentral.ui.support.localization.Localization;
import javafx.application.Application;
import javafx.stage.Stage;

public class EpisodeDetailsDialogExample {

    public static class FeedDetailsDialogExampleApplication extends Application {

        @Override
        public void start(Stage primaryStage) throws Exception {
            Localization localization = new Localization() {};
            Central central = new Central(localization);
            FeedInput feedInput = FeedInputLoader.loadFeedInputFromUrl("file:/Users/perdian/Downloads/6rWOpal0.rss");
            Feed feed = central.getLibrary().addFeed(feedInput);
            EpisodeDetailsDialog.showEpisodeDetails(feed.getEpisodes().get(0), true, localization);
        }

    }

    public static void main(String[] args) throws Exception {
        System.setProperty(PreferencesFactory.DOWNLOAD_DIRECTORY_KEY, new File("tmp/download").getCanonicalPath());
        Application.launch(FeedDetailsDialogExampleApplication.class);
    }

}
