package de.perdian.apps.podcentral.downloader.episodes;

import de.perdian.apps.podcentral.model.Episode;
import javafx.collections.ObservableList;

public interface EpisodeDownloader {

    static EpisodeDownloader createInstance() {
        return new EpisodeDownloaderImpl();
    }

    void scheduleDownload(Episode episode);
    void forceDownload(Episode episode);
    void cancelDownload(Episode episode);

    ObservableList<Episode> getScheduledEpisodes();
    ObservableList<Episode> getDownloadingEpisodes();

}
