package de.perdian.apps.podcentral.downloader.episodes;

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
    public void run(TaskProgress progress) throws Exception {
        int maxValue = 200;
        for (int i=0; i < maxValue && TaskStatus.ACTIVE.equals(progress.getStatus()); i++) {
            Thread.sleep(75);
            progress.updateProgress((double)i / (double)maxValue, null);
        }
    }

    Episode getEpisode() {
        return this.episode;
    }
    private void setEpisode(Episode episode) {
        this.episode = episode;
    }

}
