/*
 * Copyright 2019 Christian Seifert
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.perdian.apps.podcentral.retrieval;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.LinkedList;
import java.util.List;
import java.util.ServiceLoader;
import java.util.ServiceLoader.Provider;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.perdian.apps.podcentral.model.FeedInput;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

class FeedInputLoaderImpl implements FeedInputLoader {

    private static final Logger log = LoggerFactory.getLogger(FeedInputLoader.class);

    private Object lock = null;
    private List<FeedInputLoaderTask> newTasks = null;
    private List<FeedInputLoaderTask> completedTasks = null;
    private List<FeedInputLoaderListener> listeners = null;
    private Thread currentLoaderThread = null;
    private OkHttpClient okHttpClient = null;
    private BooleanProperty busy = null;
    private DoubleProperty overallProgress = null;

    FeedInputLoaderImpl() {
        this.setLock(new Object());
        this.setNewTasks(new LinkedList<>());
        this.setCompletedTasks(new LinkedList<>());
        this.setListeners(new CopyOnWriteArrayList<>());
        this.setOkHttpClient(new OkHttpClient.Builder().build());
        this.setBusy(new SimpleBooleanProperty());
        this.setOverallProgress(new SimpleDoubleProperty(1));
    }

    @Override
    public Future<FeedInput> submitFeedUrl(String feedUrl) {
        FeedInputLoaderTask loaderTask = new FeedInputLoaderTask(feedUrl);
        this.addTask(loaderTask);
        return loaderTask.getFuture();
    }

    private void addTask(FeedInputLoaderTask task) {
        synchronized (this.getLock()) {
            this.getNewTasks().add(task);
            Thread loaderThread = this.getCurrentLoaderThread();
            if (loaderThread == null) {
                loaderThread = new Thread(() -> this.processTasks());
                loaderThread.setName(this.getClass().getName() + "[LoaderThread]");
                this.setCurrentLoaderThread(loaderThread);
                loaderThread.start();
            }
        }
    }

    private void processTasks() {
        while (true) {
            FeedInputLoaderTask nextLoaderTask = null;
            synchronized (this.getLock()) {
                this.updateProgress();
                if (this.getNewTasks().isEmpty()) {
                    this.getBusy().setValue(false);
                    this.updateProgress();
                    this.getCompletedTasks().clear();
                    this.setCurrentLoaderThread(null);
                    return;
                } else {
                    this.getBusy().setValue(true);
                    nextLoaderTask = this.getNewTasks().remove(0);
                    this.updateProgress();
                }
            }
            this.processTask(nextLoaderTask);
        }
    }

    private void processTask(FeedInputLoaderTask task) {
        try {
            FeedInput feedInput = this.loadFeedInputFromUrl(task.getUrl());
            task.getFuture().complete(feedInput);
            synchronized (this.getLock()) {
                this.getCompletedTasks().add(task);
                this.updateProgress();
            }
            this.getListeners().forEach(listener -> listener.onFeedInputLoaded(task.getUrl(), feedInput));
        } catch (Exception e) {
            task.getFuture().completeExceptionally(e);
            this.getListeners().forEach(listener -> listener.onFeedInputFailure(task.getUrl(), e));
        }
    }

    private void updateProgress() {
        int numberOfWaitingTasks = this.getNewTasks().size();
        int numberOfCompletedTasks = this.getCompletedTasks().size();
        int numberOfTotalTasks = numberOfCompletedTasks + numberOfWaitingTasks;
        if (numberOfTotalTasks > 0) {
            this.getOverallProgress().setValue((double)numberOfCompletedTasks / (double)numberOfTotalTasks);
        }
    }

    private FeedInput loadFeedInputFromUrl(String feedUrl) throws Exception {
        try {
            Request httpRequest = new Request.Builder().get().url(feedUrl).build();
            Instant startTime = Instant.now();
            try (Response httpResponse = this.getOkHttpClient().newCall(httpRequest).execute()) {
                log.debug("Loaded content (took {}) from feed URL: {}", Duration.between(startTime, Instant.now()), feedUrl);
                List<Provider<FeedInputSource>> feedInputSources = ServiceLoader.load(FeedInputSource.class).stream().collect(Collectors.toList());
                log.debug("Processing {} feed input sources for feed from URL: {}", feedInputSources.size(), feedUrl);
                for (Provider<FeedInputSource> feedInputSource : feedInputSources) {
                    FeedInput feedInput = feedInputSource.get().loadFeedInput(httpResponse);
                    if (feedInput == null) {
                        log.debug("Cannot load feed input using source {} from feed URL: {}", feedInputSource.get(), feedUrl);
                    } else {
                        log.info("Loaded feed input using source {} from feed URL: {}", feedInputSource.get(), feedUrl);
                        return feedInput;
                    }
                }
                throw new IllegalArgumentException("Cannot analyze response for content type '" + httpResponse.body().contentType() + "' from feed URL: " + feedUrl);
            }
        } catch (IOException e) {
            throw new IOException("Cannot load feed from feed URL: " + feedUrl, e);
        }
    }

    private Object getLock() {
        return this.lock;
    }
    private void setLock(Object lock) {
        this.lock = lock;
    }

    private Thread getCurrentLoaderThread() {
        return this.currentLoaderThread;
    }
    private void setCurrentLoaderThread(Thread currentLoaderThread) {
        this.currentLoaderThread = currentLoaderThread;
    }

    private List<FeedInputLoaderTask> getNewTasks() {
        return this.newTasks;
    }
    private void setNewTasks(List<FeedInputLoaderTask> newTasks) {
        this.newTasks = newTasks;
    }

    private List<FeedInputLoaderTask> getCompletedTasks() {
        return this.completedTasks;
    }
    private void setCompletedTasks(List<FeedInputLoaderTask> completedTasks) {
        this.completedTasks = completedTasks;
    }

    @Override
    public boolean addListener(FeedInputLoaderListener listener) {
        return this.getListeners().add(listener);
    }
    @Override
    public boolean removeListener(FeedInputLoaderListener listener) {
        return this.getListeners().remove(listener);
    }
    private List<FeedInputLoaderListener> getListeners() {
        return this.listeners;
    }
    private void setListeners(List<FeedInputLoaderListener> listeners) {
        this.listeners = listeners;
    }

    private OkHttpClient getOkHttpClient() {
        return this.okHttpClient;
    }
    private void setOkHttpClient(OkHttpClient okHttpClient) {
        this.okHttpClient = okHttpClient;
    }

    @Override
    public BooleanProperty getBusy() {
        return this.busy;
    }
    private void setBusy(BooleanProperty busy) {
        this.busy = busy;
    }

    @Override
    public DoubleProperty getOverallProgress() {
        return this.overallProgress;
    }
    private void setOverallProgress(DoubleProperty overallProgress) {
        this.overallProgress = overallProgress;
    }

}
