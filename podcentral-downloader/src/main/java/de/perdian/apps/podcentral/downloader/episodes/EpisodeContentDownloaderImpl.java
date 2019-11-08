package de.perdian.apps.podcentral.downloader.episodes;

import java.util.HashMap;
import java.util.Map;

import de.perdian.apps.podcentral.jobscheduler.AcceptedJob;
import de.perdian.apps.podcentral.jobscheduler.ActiveJob;
import de.perdian.apps.podcentral.jobscheduler.Job;
import de.perdian.apps.podcentral.jobscheduler.JobListener;
import de.perdian.apps.podcentral.jobscheduler.JobScheduler;
import de.perdian.apps.podcentral.jobscheduler.JobStatus;
import de.perdian.apps.podcentral.model.Episode;
import de.perdian.apps.podcentral.model.EpisodeContentDownloadState;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

class EpisodeContentDownloaderImpl implements EpisodeContentDownloader {

    private Object lock = null;
    private JobScheduler jobScheduler = null;
    private ObservableList<Episode> scheduledEpisodes = null;
    private ObservableList<Episode> downloadingEpisodes = null;
    private Map<Episode, AcceptedJob> episodeToAcceptedJob = null;

    public EpisodeContentDownloaderImpl() {
        JobScheduler jobScheduler = new JobScheduler(5);
        jobScheduler.addJobListener(new JobListenerImpl());
        this.setLock(new Object());
        this.setJobScheduler(jobScheduler);
        this.setScheduledEpisodes(FXCollections.observableArrayList());
        this.setDownloadingEpisodes(FXCollections.observableArrayList());
        this.setEpisodeToAcceptedJob(new HashMap<>());
    }

    @Override
    public void scheduleDownload(Episode episode) {
        synchronized (this.getLock()) {
            if (!this.getEpisodeToAcceptedJob().containsKey(episode)) {
                EpisodeContentDownloaderJobRunnable jobRunnable = new EpisodeContentDownloaderJobRunnable(episode);
                Job job = new Job(episode.getTitle().getValue(), jobRunnable);
                job.addProgressListener((progress, text) -> episode.getContentDownloadProgress().setValue(progress));
                this.getJobScheduler().submitJob(job);
            }
        }
    }

    @Override
    public void cancelDownload(Episode episode) {
        synchronized (this.getLock()) {
            AcceptedJob acceptedJob = this.getEpisodeToAcceptedJob().get(episode);
            if (acceptedJob != null) {
                acceptedJob.cancel(null);
            }
        }
    }

    class JobListenerImpl implements JobListener {
        @Override public void onJobAccepted(AcceptedJob acceptedJob) {
            synchronized (EpisodeContentDownloaderImpl.this.getLock()) {
                EpisodeContentDownloaderJobRunnable jobRunnable = (EpisodeContentDownloaderJobRunnable)acceptedJob.getJob().getRunnable();
                EpisodeContentDownloaderImpl.this.getScheduledEpisodes().add(jobRunnable.getEpisode());
                EpisodeContentDownloaderImpl.this.getEpisodeToAcceptedJob().put(jobRunnable.getEpisode(), acceptedJob);
                jobRunnable.getEpisode().getContentDownloadState().setValue(EpisodeContentDownloadState.SCHEDULED);
            }
        }
        @Override public void onJobCancelled(AcceptedJob acceptedJob) {
            synchronized (EpisodeContentDownloaderImpl.this.getLock()) {
                EpisodeContentDownloaderJobRunnable jobRunnable = (EpisodeContentDownloaderJobRunnable)acceptedJob.getJob().getRunnable();
                EpisodeContentDownloaderImpl.this.getScheduledEpisodes().remove(jobRunnable.getEpisode());
                EpisodeContentDownloaderImpl.this.getEpisodeToAcceptedJob().remove(jobRunnable.getEpisode());
                jobRunnable.getEpisode().getContentDownloadState().setValue(EpisodeContentDownloadState.CANCELLED);
            }
        }
        @Override public void onJobStarting(ActiveJob activeJob) {
            synchronized (EpisodeContentDownloaderImpl.this.getLock()) {
                EpisodeContentDownloaderJobRunnable jobRunnable = (EpisodeContentDownloaderJobRunnable)activeJob.getAcceptedJob().getJob().getRunnable();
                EpisodeContentDownloaderImpl.this.getScheduledEpisodes().remove(jobRunnable.getEpisode());
                EpisodeContentDownloaderImpl.this.getDownloadingEpisodes().add(jobRunnable.getEpisode());
                EpisodeContentDownloaderImpl.this.getEpisodeToAcceptedJob().put(jobRunnable.getEpisode(), activeJob.getAcceptedJob());
                jobRunnable.getEpisode().getContentDownloadState().setValue(EpisodeContentDownloadState.DOWNLOADING);
            }
        }
        @Override public void onJobCompleted(ActiveJob activeJob) {
            synchronized (EpisodeContentDownloaderImpl.this.getLock()) {
                EpisodeContentDownloaderJobRunnable jobRunnable = (EpisodeContentDownloaderJobRunnable)activeJob.getAcceptedJob().getJob().getRunnable();
                EpisodeContentDownloaderImpl.this.getDownloadingEpisodes().remove(jobRunnable.getEpisode());
                EpisodeContentDownloaderImpl.this.getEpisodeToAcceptedJob().remove(jobRunnable.getEpisode());
                jobRunnable.getEpisode().getContentDownloadState().setValue(JobStatus.CANCELLED.equals(activeJob.getStatus()) ? EpisodeContentDownloadState.CANCELLED : EpisodeContentDownloadState.COMPLETED);
            }
        }
        @Override public void onJobCancelled(ActiveJob activeJob) {
            synchronized (EpisodeContentDownloaderImpl.this.getLock()) {
                EpisodeContentDownloaderJobRunnable jobRunnable = (EpisodeContentDownloaderJobRunnable)activeJob.getAcceptedJob().getJob().getRunnable();
                EpisodeContentDownloaderImpl.this.getScheduledEpisodes().remove(jobRunnable.getEpisode());
                EpisodeContentDownloaderImpl.this.getEpisodeToAcceptedJob().remove(jobRunnable.getEpisode());
                jobRunnable.getEpisode().getContentDownloadState().setValue(EpisodeContentDownloadState.CANCELLED);
            }
        }
    }

    private Object getLock() {
        return this.lock;
    }
    private void setLock(Object lock) {
        this.lock = lock;
    }

    private JobScheduler getJobScheduler() {
        return this.jobScheduler;
    }
    private void setJobScheduler(JobScheduler jobScheduler) {
        this.jobScheduler = jobScheduler;
    }

    private Map<Episode, AcceptedJob> getEpisodeToAcceptedJob() {
        return this.episodeToAcceptedJob;
    }
    private void setEpisodeToAcceptedJob(Map<Episode, AcceptedJob> episodeToAcceptedJob) {
        this.episodeToAcceptedJob = episodeToAcceptedJob;
    }

    @Override
    public ObservableList<Episode> getScheduledEpisodes() {
        return this.scheduledEpisodes;
    }
    private void setScheduledEpisodes(ObservableList<Episode> scheduledEpisodes) {
        this.scheduledEpisodes = scheduledEpisodes;
    }

    @Override
    public ObservableList<Episode> getDownloadingEpisodes() {
        return this.downloadingEpisodes;
    }
    private void setDownloadingEpisodes(ObservableList<Episode> downloadingEpisodes) {
        this.downloadingEpisodes = downloadingEpisodes;
    }

}
