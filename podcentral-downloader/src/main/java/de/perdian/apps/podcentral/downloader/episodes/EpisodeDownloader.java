package de.perdian.apps.podcentral.downloader.episodes;

import de.perdian.apps.podcentral.model.Episode;
import de.perdian.apps.podcentral.preferences.Preferences;
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
