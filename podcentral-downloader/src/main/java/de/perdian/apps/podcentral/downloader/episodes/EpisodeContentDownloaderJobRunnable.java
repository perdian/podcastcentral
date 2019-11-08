package de.perdian.apps.podcentral.downloader.episodes;

import de.perdian.apps.podcentral.jobscheduler.JobProgress;
import de.perdian.apps.podcentral.jobscheduler.JobRunnable;
import de.perdian.apps.podcentral.jobscheduler.JobStatus;
import de.perdian.apps.podcentral.model.Episode;

class EpisodeContentDownloaderJobRunnable implements JobRunnable {

    private Episode episode = null;

    EpisodeContentDownloaderJobRunnable(Episode episode) {
        this.setEpisode(episode);
    }

    @Override
    public void run(JobProgress progress) throws Exception {
        int maxValue = 200;
        for (int i=0; i < maxValue && JobStatus.ACTIVE.equals(progress.getStatus()); i++) {
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
