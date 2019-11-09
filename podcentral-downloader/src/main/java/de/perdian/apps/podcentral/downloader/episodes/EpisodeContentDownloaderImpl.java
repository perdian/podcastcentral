package de.perdian.apps.podcentral.downloader.episodes;

import java.util.HashMap;
import java.util.Map;

import de.perdian.apps.podcentral.model.Episode;
import de.perdian.apps.podcentral.model.EpisodeContentDownloadState;
import de.perdian.apps.podcentral.taskexecutor.Task;
import de.perdian.apps.podcentral.taskexecutor.TaskExecutor;
import de.perdian.apps.podcentral.taskexecutor.TaskListener;
import de.perdian.apps.podcentral.taskexecutor.TaskRequest;
import de.perdian.apps.podcentral.taskexecutor.TaskStatus;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

class EpisodeContentDownloaderImpl implements EpisodeContentDownloader {

    private Object lock = null;
    private TaskExecutor taskExecutor = null;
    private ObservableList<Episode> scheduledEpisodes = null;
    private ObservableList<Episode> downloadingEpisodes = null;
    private Map<Episode, Task> episodeToTask = null;

    public EpisodeContentDownloaderImpl() {
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
                EpisodeContentDownloaderJobRunnable jobRunnable = new EpisodeContentDownloaderJobRunnable(episode);
                TaskRequest taskRequest = new TaskRequest(episode.getTitle().getValue(), jobRunnable);
                taskRequest.addProgressListener((progress, text) -> episode.getContentDownloadProgress().setValue(progress));
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

    class TaskListenerImpl implements TaskListener {
        @Override public void onTaskScheduled(Task task) {
            synchronized (EpisodeContentDownloaderImpl.this.getLock()) {
                EpisodeContentDownloaderJobRunnable jobRunnable = (EpisodeContentDownloaderJobRunnable)task.getRequest().getRunnable();
                EpisodeContentDownloaderImpl.this.getScheduledEpisodes().add(jobRunnable.getEpisode());
                EpisodeContentDownloaderImpl.this.getEpisodeToTask().put(jobRunnable.getEpisode(), task);
                jobRunnable.getEpisode().getContentDownloadState().setValue(EpisodeContentDownloadState.SCHEDULED);
            }
        }
        @Override public void onTaskCancelled(Task task) {
            synchronized (EpisodeContentDownloaderImpl.this.getLock()) {
                EpisodeContentDownloaderJobRunnable jobRunnable = (EpisodeContentDownloaderJobRunnable)task.getRequest().getRunnable();
                EpisodeContentDownloaderImpl.this.getScheduledEpisodes().remove(jobRunnable.getEpisode());
                EpisodeContentDownloaderImpl.this.getEpisodeToTask().remove(jobRunnable.getEpisode());
                jobRunnable.getEpisode().getContentDownloadState().setValue(EpisodeContentDownloadState.CANCELLED);
            }
        }
        @Override public void onTaskStarted(Task task) {
            synchronized (EpisodeContentDownloaderImpl.this.getLock()) {
                EpisodeContentDownloaderJobRunnable jobRunnable = (EpisodeContentDownloaderJobRunnable)task.getRequest().getRunnable();
                EpisodeContentDownloaderImpl.this.getScheduledEpisodes().remove(jobRunnable.getEpisode());
                EpisodeContentDownloaderImpl.this.getDownloadingEpisodes().add(jobRunnable.getEpisode());
                EpisodeContentDownloaderImpl.this.getEpisodeToTask().put(jobRunnable.getEpisode(), task);
                jobRunnable.getEpisode().getContentDownloadState().setValue(EpisodeContentDownloadState.DOWNLOADING);
            }
        }
        @Override public void onTaskCompleted(Task task) {
            synchronized (EpisodeContentDownloaderImpl.this.getLock()) {
                EpisodeContentDownloaderJobRunnable jobRunnable = (EpisodeContentDownloaderJobRunnable)task.getRequest().getRunnable();
                EpisodeContentDownloaderImpl.this.getDownloadingEpisodes().remove(jobRunnable.getEpisode());
                EpisodeContentDownloaderImpl.this.getEpisodeToTask().remove(jobRunnable.getEpisode());
                jobRunnable.getEpisode().getContentDownloadState().setValue(TaskStatus.CANCELLED.equals(task.getStatus()) ? EpisodeContentDownloadState.CANCELLED : EpisodeContentDownloadState.COMPLETED);
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
