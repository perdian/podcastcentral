package de.perdian.apps.podcentral.downloader.episodes;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import de.perdian.apps.podcentral.model.Episode;
import de.perdian.apps.podcentral.taskexecutor.TaskProgress;
import de.perdian.apps.podcentral.taskexecutor.TaskRunnable;
import de.perdian.apps.podcentral.taskexecutor.TaskStatus;

class EpisodeDownloaderJobRunnable implements TaskRunnable {

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
        long bytesTotal = 1024 * 1024 * 50; // 50 MB
        long bytesPerStep = (long)(1024 * 1024 * 0.5); // 0.5 MB
        for (long bytesTransfered=0; bytesTransfered <= bytesTotal && TaskStatus.ACTIVE.equals(progress.getStatus()); bytesTransfered+=bytesPerStep) {
            Thread.sleep(75);
            this.getEpisode().getDownloadedBytes().setValue(bytesTransfered);
            progress.updateProgress((double)bytesTransfered / (double)bytesTotal, null);
        }
    }

    Episode getEpisode() {
        return this.episode;
    }
    private void setEpisode(Episode episode) {
        this.episode = episode;
    }

}
