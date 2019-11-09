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
package de.perdian.apps.podcentral.ui.support.tasks;

import org.apache.commons.lang3.StringUtils;

import de.perdian.apps.podcentral.taskexecutor.TaskExecutor;
import de.perdian.apps.podcentral.taskexecutor.TaskProgress;
import de.perdian.apps.podcentral.taskexecutor.TaskRequest;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

class BackgroundTaskExecutorImpl implements BackgroundTaskExecutor {

    private TaskExecutor taskExecutor = null;
    private DoubleProperty progress = null;
    private StringProperty text = null;

    public BackgroundTaskExecutorImpl() {
        this.setTaskExecutor(new TaskExecutor(1));
        this.setProgress(new SimpleDoubleProperty(0));
        this.setText(new SimpleStringProperty());
    }

    @Override
    public void execute(String title, BackgroundTask backgroundTask) {
        this.getTaskExecutor().submitTask(new TaskRequest(title, progress -> {
            try {
                this.getProgress().setValue(0d);
                this.getText().setValue(title);
                backgroundTask.execute(new BackgroundProgressImpl(progress));
            } finally {
                this.getProgress().setValue(0d);
                this.getText().setValue(null);
            }
        }));
    }

    class BackgroundProgressImpl implements BackgroundProgress {

        private TaskProgress taskProgress = null;

        BackgroundProgressImpl(TaskProgress taskProgress) {
            this.setTaskProgress(taskProgress);
        }

        @Override
        public void updateProgress(Double progress, String text) {
            BackgroundTaskExecutorImpl.this.getProgress().setValue(Double.valueOf(progress == null ? 0d : progress.doubleValue()));
            if (StringUtils.isNotEmpty(text)) {
                BackgroundTaskExecutorImpl.this.getText().setValue(text);
            }
            this.getTaskProgress().updateProgress(progress, text);
        }

        private TaskProgress getTaskProgress() {
            return this.taskProgress;
        }
        private void setTaskProgress(TaskProgress taskProgress) {
            this.taskProgress = taskProgress;
        }

    }

    private TaskExecutor getTaskExecutor() {
        return this.taskExecutor;
    }
    private void setTaskExecutor(TaskExecutor taskExecutor) {
        this.taskExecutor = taskExecutor;
    }

    @Override
    public DoubleProperty getProgress() {
        return this.progress;
    }
    private void setProgress(DoubleProperty progress) {
        this.progress = progress;
    }

    @Override
    public StringProperty getText() {
        return this.text;
    }
    private void setText(StringProperty text) {
        this.text = text;
    }

}
