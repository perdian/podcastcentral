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
package de.perdian.apps.podcentral.jobscheduler;

import java.util.ArrayList;
import java.util.List;

public class Job {

    private String title = null;
    private JobRunnable runnable = null;
    private int priority = 0;
    private List<JobProgressListener> progressListeners = new ArrayList<>();

    public Job() {
    }

    public Job(String title, JobRunnable runnable) {
        this.setTitle(title);
        this.setRunnable(runnable);
    }

    public String getTitle() {
        return this.title;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    public JobRunnable getRunnable() {
        return this.runnable;
    }
    public void setRunnable(JobRunnable runnable) {
        this.runnable = runnable;
    }

    public int getPriority() {
        return this.priority;
    }
    public void setPriority(int priority) {
        this.priority = priority;
    }

    public boolean addProgressListener(JobProgressListener jobProgressListener) {
        return this.getProgressListeners().add(jobProgressListener);
    }
    public boolean removeProgressListener(JobProgressListener jobProgressListener) {
        return this.getProgressListeners().remove(jobProgressListener);
    }
    public List<JobProgressListener> getProgressListeners() {
        return this.progressListeners;
    }
    public void setProgressListeners(List<JobProgressListener> progressListeners) {
        this.progressListeners = progressListeners;
    }

}
