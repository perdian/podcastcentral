package de.perdian.apps.podcastcentral.downloader.episodes;

import de.perdian.apps.podcastcentral.model.Episode;
import de.perdian.apps.podcastcentral.preferences.Preferences;
import javafx.beans.property.IntegerProperty;
import javafx.collections.ObservableList;

public interface EpisodeDownloader {

    static EpisodeDownloader createInstance(Preferences preferences) {
        return new EpisodeDownloaderImpl(preferences);
    }

    void scheduleDownload(Episode episode);
    void forceDownload(Episode episode);
    void cancelDownload(Episode episode);

    ObservableList<Episode> getScheduledEpisodes();
    ObservableList<Episode> getDownloadingEpisodes();
    IntegerProperty getNumberOfDownloadSlots();

}
