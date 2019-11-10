package de.perdian.apps.podcentral.downloader.episodes;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import de.perdian.apps.podcentral.model.Episode;
import de.perdian.apps.podcentral.model.EpisodeDownloadState;
import de.perdian.apps.podcentral.preferences.Preferences;
import de.perdian.apps.podcentral.taskexecutor.Task;
import de.perdian.apps.podcentral.taskexecutor.TaskExecutor;
import de.perdian.apps.podcentral.taskexecutor.TaskListener;
import de.perdian.apps.podcentral.taskexecutor.TaskRequest;
import javafx.beans.property.IntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

class EpisodeDownloaderImpl implements EpisodeDownloader {

    private Object lock = null;
    private TaskExecutor taskExecutor = null;
    private ObservableList<Episode> scheduledEpisodes = null;
    private ObservableList<Episode> downloadingEpisodes = null;
    private IntegerProperty numberOfDownloadSlots = null;
    private Map<Episode, Task> episodeToTask = null;

    public EpisodeDownloaderImpl(Preferences preferences) {

        IntegerProperty numberOfDownloadSlots = preferences.getIntegerProperty("EpisodeDownloader.numberOfDownloadSlots", 5);
        TaskExecutor taskExecutor = new TaskExecutor(numberOfDownloadSlots.getValue().intValue());
        taskExecutor.addTaskListener(new TaskListenerImpl());
        taskExecutor.addTaskListener(new TaskListener() {
            @Override public void onProcessorCountUpdated(int newProcessorCount) {
                if (newProcessorCount != numberOfDownloadSlots.getValue().intValue()) {
                    numberOfDownloadSlots.setValue(newProcessorCount);
                }
            }
        });
        numberOfDownloadSlots.addListener((o, oldValue, newValue) -> {
            if (!Objects.equals(oldValue, newValue) ) {
                taskExecutor.setProcessorCount(newValue.intValue());
            }
        });

        this.setLock(new Object());
        this.setTaskExecutor(taskExecutor);
        this.setScheduledEpisodes(FXCollections.observableArrayList());
        this.setDownloadingEpisodes(FXCollections.observableArrayList());
        this.setNumberOfDownloadSlots(numberOfDownloadSlots);
        this.setEpisodeToTask(new HashMap<>());

    }

    @Override
    public void scheduleDownload(Episode episode) {
        synchronized (this.getLock()) {
            if (!this.getEpisodeToTask().containsKey(episode)) {
                EpisodeDownloaderJobRunnable jobRunnable = new EpisodeDownloaderJobRunnable(episode);
                TaskRequest taskRequest = new TaskRequest(episode.getTitle().getValue(), jobRunnable);
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

    @Override
    public IntegerProperty getNumberOfDownloadSlots() {
        return this.numberOfDownloadSlots;
    }
    private void setNumberOfDownloadSlots(IntegerProperty numberOfDownloadSlots) {
        this.numberOfDownloadSlots = numberOfDownloadSlots;
    }

}
