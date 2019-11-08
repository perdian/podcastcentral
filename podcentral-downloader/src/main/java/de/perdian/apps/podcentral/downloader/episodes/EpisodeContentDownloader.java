package de.perdian.apps.podcentral.downloader.episodes;

import de.perdian.apps.podcentral.model.Episode;
import javafx.collections.ObservableList;

public interface EpisodeContentDownloader {

    static EpisodeContentDownloader createInstance() {
        return new EpisodeContentDownloaderImpl();
    }

    void scheduleDownload(Episode episode);
    void cancelDownload(Episode episode);

    ObservableList<Episode> getScheduledEpisodes();
    ObservableList<Episode> getDownloadingEpisodes();

}
