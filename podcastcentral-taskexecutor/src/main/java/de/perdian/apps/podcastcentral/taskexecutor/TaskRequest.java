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
package de.perdian.apps.podcastcentral.taskexecutor;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class TaskRequest {

    private String title = null;
    private String description = null;
    private String previewImageUrl = null;
    private TaskRunnable runnable = null;
    private int priority = 0;
    private List<TaskProgressListener> progressListeners = null;

    public TaskRequest() {
    }

    public TaskRequest(String title, TaskRunnable runnable) {
        this.setTitle(title);
        this.setRunnable(runnable);
        this.setProgressListeners(new CopyOnWriteArrayList<>());
    }

    @Override
    public String toString() {
        ToStringBuilder toStringBuilder = new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE);
        toStringBuilder.append("title", this.getTitle());
        toStringBuilder.append("runnable", this.getRunnable());
        toStringBuilder.append("priority", this.getPriority());
        return toStringBuilder.toString();
    }

    public String getTitle() {
        return this.title;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return this.description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    public String getPreviewImageUrl() {
        return this.previewImageUrl;
    }
    public void setPreviewImageUrl(String previewImageUrl) {
        this.previewImageUrl = previewImageUrl;
    }

    public TaskRunnable getRunnable() {
        return this.runnable;
    }
    public void setRunnable(TaskRunnable runnable) {
        this.runnable = runnable;
    }

    public int getPriority() {
        return this.priority;
    }
    public void setPriority(int priority) {
        this.priority = priority;
    }

    public boolean addProgressListener(TaskProgressListener progressListener) {
        return this.getProgressListeners().add(progressListener);
    }
    public boolean removeProgressListener(TaskProgressListener progressListener) {
        return this.getProgressListeners().remove(progressListener);
    }
    List<TaskProgressListener> getProgressListeners() {
        return this.progressListeners;
    }
    private void setProgressListeners(List<TaskProgressListener> progressListeners) {
        this.progressListeners = progressListeners;
    }

}
