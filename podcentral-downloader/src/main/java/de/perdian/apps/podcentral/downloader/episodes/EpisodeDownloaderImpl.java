package de.perdian.apps.podcentral.downloader.episodes;

import java.util.HashMap;
import java.util.Map;

import de.perdian.apps.podcentral.model.Episode;
import de.perdian.apps.podcentral.model.EpisodeDownloadState;
import de.perdian.apps.podcentral.taskexecutor.Task;
import de.perdian.apps.podcentral.taskexecutor.TaskExecutor;
import de.perdian.apps.podcentral.taskexecutor.TaskListener;
import de.perdian.apps.podcentral.taskexecutor.TaskRequest;
import de.perdian.apps.podcentral.taskexecutor.TaskStatus;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

class EpisodeDownloaderImpl implements EpisodeDownloader {

    private Object lock = null;
    private TaskExecutor taskExecutor = null;
    private ObservableList<Episode> scheduledEpisodes = null;
    private ObservableList<Episode> downloadingEpisodes = null;
    private Map<Episode, Task> episodeToTask = null;

    public EpisodeDownloaderImpl() {
        TaskExecutor taskExecutor = new TaskExecutor(5);
        taskExecutor.addTaskListener(new TaskListenerImpl());
        this.setLock(new Object());
        this.setTaskExecutor(taskExecutor);
        this.setScheduledEpisodes(FXCollections.observableArrayList());
        this.setDownloadingEpisodes(FXCollections.observableArrayList());
        this.setEpisodeToTask(new HashMap<>());
    }

    @Override
    public void scheduleDownload(Episode episode) {
        synchronized (this.getLock()) {
            if (!this.getEpisodeToTask().containsKey(episode)) {
                EpisodeDownloaderJobRunnable jobRunnable = new EpisodeDownloaderJobRunnable(episode);
                TaskRequest taskRequest = new TaskRequest(episode.getTitle().getValue(), jobRunnable);
                taskRequest.addProgressListener((progress, text) -> episode.getDownloadProgress().setValue(progress));
                this.getTaskExecutor().submitTask(taskRequest);
            }
        }
    }

    @Override
    public void cancelDownload(Episode episode) {
        synchronized (this.getLock()) {
            Task task = this.getEpisodeToTask().get(episode);
            if (task != null) {
                task.cancel(null);
            }
        }
    }

    @Override
    public void forceDownload(Episode episode) {
        synchronized (this.getLock()) {
            Task task = this.getEpisodeToTask().get(episode);
            if (task != null) {
                task.forceStart();
            }
        }
    }

    class TaskListenerImpl implements TaskListener {
        @Override public void onTaskScheduled(Task task) {
            synchronized (EpisodeDownloaderImpl.this.getLock()) {
                EpisodeDownloaderJobRunnable jobRunnable = (EpisodeDownloaderJobRunnable)task.getRequest().getRunnable();
                EpisodeDownloaderImpl.this.getScheduledEpisodes().add(jobRunnable.getEpisode());
                EpisodeDownloaderImpl.this.getEpisodeToTask().put(jobRunnable.getEpisode(), task);
                jobRunnable.getEpisode().getDownloadState().setValue(EpisodeDownloadState.SCHEDULED);
            }
        }
        @Override public void onTaskCancelled(Task task) {
            synchronized (EpisodeDownloaderImpl.this.getLock()) {
                EpisodeDownloaderJobRunnable jobRunnable = (EpisodeDownloaderJobRunnable)task.getRequest().getRunnable();
                EpisodeDownloaderImpl.this.getScheduledEpisodes().remove(jobRunnable.getEpisode());
                EpisodeDownloaderImpl.this.getEpisodeToTask().remove(jobRunnable.getEpisode());
                jobRunnable.getEpisode().getDownloadState().setValue(EpisodeDownloadState.CANCELLED);
            }
        }
        @Override public void onTaskStarted(Task task) {
            synchronized (EpisodeDownloaderImpl.this.getLock()) {
                EpisodeDownloaderJobRunnable jobRunnable = (EpisodeDownloaderJobRunnable)task.getRequest().getRunnable();
                EpisodeDownloaderImpl.this.getScheduledEpisodes().remove(jobRunnable.getEpisode());
                EpisodeDownloaderImpl.this.getDownloadingEpisodes().add(jobRunnable.getEpisode());
                EpisodeDownloaderImpl.this.getEpisodeToTask().put(jobRunnable.getEpisode(), task);
                jobRunnable.getEpisode().getDownloadState().setValue(EpisodeDownloadState.DOWNLOADING);
            }
        }
        @Override public void onTaskCompleted(Task task) {
            synchronized (EpisodeDownloaderImpl.this.getLock()) {
                EpisodeDownloaderJobRunnable jobRunnable = (EpisodeDownloaderJobRunnable)task.getRequest().getRunnable();
                EpisodeDownloaderImpl.this.getDownloadingEpisodes().remove(jobRunnable.getEpisode());
                EpisodeDownloaderImpl.this.getEpisodeToTask().remove(jobRunnable.getEpisode());
                jobRunnable.getEpisode().getDownloadState().setValue(TaskStatus.CANCELLED.equals(task.getStatus()) ? EpisodeDownloadState.CANCELLED : EpisodeDownloadState.COMPLETED);
            }
        }
    }

    private Object getLock() {
        return this.lock;
    }
    private void setLock(Object lock) {
        this.lock = lock;
    }

    private TaskExecutor getTaskExecutor() {
        return this.taskExecutor;
    }
    private void setTaskExecutor(TaskExecutor taskExecutor) {
        this.taskExecutor = taskExecutor;
    }

    private Map<Episode, Task> getEpisodeToTask() {
        return this.episodeToTask;
    }
    private void setEpisodeToTask(Map<Episode, Task> episodeToTask) {
        this.episodeToTask = episodeToTask;
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
