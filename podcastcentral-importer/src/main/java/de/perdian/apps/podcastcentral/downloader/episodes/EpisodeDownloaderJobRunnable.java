package de.perdian.apps.podcastcentral.downloader.episodes;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.perdian.apps.podcastcentral.model.Episode;
import de.perdian.apps.podcastcentral.model.EpisodeDownloadState;
import de.perdian.apps.podcastcentral.taskexecutor.TaskProgress;
import de.perdian.apps.podcastcentral.taskexecutor.TaskRunnable;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

class EpisodeDownloaderJobRunnable implements TaskRunnable {

    private static final Logger log = LoggerFactory.getLogger(EpisodeDownloaderJobRunnable.class);
    private static final OkHttpClient HTTP_CLIENT = new OkHttpClient.Builder().build();

    private Episode episode = null;

    EpisodeDownloaderJobRunnable(Episode episode) {
        this.setEpisode(episode);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

    @Override
    public void run(TaskProgress progress) throws Exception {
        try {
            this.runDownload(progress);
            this.getEpisode().getDownloadState().setValue(EpisodeDownloadState.COMPLETED);
            this.getEpisode().getDownloadError().setValue(null);
        } catch (Exception e) {
            log.debug("Cannot execute download for episode: " + this.getEpisode(), e);
            this.getEpisode().getDownloadState().setValue(EpisodeDownloadState.ERRORED);
            this.getEpisode().getDownloadError().setValue(e);
        }
    }

    private void runDownload(TaskProgress progress) throws Exception {
        String downloadUrl = this.getEpisode().getContentUrl().getValue();
        if (StringUtils.isEmpty(downloadUrl)) {
            throw new IllegalArgumentException("No content URL available for episode '" + this.getEpisode().getTitle().getValue() + "'");
        } else {
            Request downloadRequest = new Request.Builder().get().url(downloadUrl).build();
            try (Response downloadResponse = HTTP_CLIENT.newCall(downloadRequest).execute()) {
                Long expectedLength = this.getEpisode().getContentSize().getValue();
                long actualLength = downloadResponse.body().contentLength();
                if (expectedLength == null || expectedLength.longValue() != actualLength) {
                    this.getEpisode().getContentSize().setValue(actualLength);
                }
                byte[] downloadBuffer = new byte[1024 * 32];
                try (InputStream downloadStream = new BufferedInputStream(downloadResponse.body().byteStream())) {
                    File targetFile = this.getEpisode().getContentFile().getValue();
                    log.debug("Starting download into target file '{}' for episode '{}'", targetFile, this.getEpisode());
                    if (!targetFile.getParentFile().exists()) {
                        log.debug("Creating download target directory: {}", targetFile.getParentFile().getAbsolutePath());
                        targetFile.getParentFile().mkdirs();
                    }
                    try (BufferedOutputStream targetStream = new BufferedOutputStream(new FileOutputStream(targetFile))) {
                        long totalBytesRead = 0;
                        for (int bytesRead = downloadStream.read(downloadBuffer); bytesRead > -1; bytesRead = downloadStream.read(downloadBuffer)) {
                            targetStream.write(downloadBuffer, 0, bytesRead);
                            totalBytesRead += bytesRead;
                            double downloadProgress = (double)totalBytesRead / (double)actualLength;
                            this.getEpisode().getDownloadedBytes().setValue(totalBytesRead);
                            this.getEpisode().getDownloadProgress().setValue(downloadProgress);
                            progress.updateProgress(downloadProgress, null);
                        }
                    }
                }
            }
        }
    }

    Episode getEpisode() {
        return this.episode;
    }
    private void setEpisode(Episode episode) {
        this.episode = episode;
    }

}
