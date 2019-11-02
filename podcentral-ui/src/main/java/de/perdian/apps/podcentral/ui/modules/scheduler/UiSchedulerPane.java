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
package de.perdian.apps.podcentral.ui.modules.scheduler;

import org.apache.commons.lang3.StringUtils;

import de.perdian.apps.podcentral.jobscheduler.ActiveJob;
import de.perdian.apps.podcentral.jobscheduler.JobListener;
import de.perdian.apps.podcentral.jobscheduler.JobProgressListener;
import de.perdian.apps.podcentral.jobscheduler.JobScheduler;
import de.perdian.apps.podcentral.ui.localization.Localization;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.text.Font;

public class UiSchedulerPane extends GridPane {

    private Label jobLabel = null;
    private ProgressBar progressBar = null;
    private Label progressLabel = null;
    private Localization localization = null;

    public UiSchedulerPane(JobScheduler jobScheduler, Localization localization) {

        Label jobLabel = new Label(localization.noActivity());
        jobLabel.setFont(Font.font(jobLabel.getFont().getFamily(), jobLabel.getFont().getSize() * 0.8));
        jobLabel.setPrefWidth(200);
        GridPane.setHgrow(jobLabel, Priority.ALWAYS);
        this.setJobLabel(jobLabel);

        Label progressLabel = new Label(" ");
        progressLabel.setFont(Font.font(progressLabel.getFont().getFamily(), progressLabel.getFont().getSize() * 0.8));
        progressLabel.setPrefWidth(200);
        GridPane.setHgrow(progressLabel, Priority.ALWAYS);
        this.setProgressLabel(progressLabel);

        ProgressBar progressBar = new ProgressBar(0);
        progressBar.setMaxWidth(Double.MAX_VALUE);
        GridPane.setHgrow(progressBar, Priority.ALWAYS);
        GridPane.setMargin(progressBar, new Insets(1, 0, 1, 0));
        this.setProgressBar(progressBar);

        GridPane progressPane = new GridPane();
        progressPane.add(jobLabel, 0, 0, 1, 1);
        progressPane.add(progressLabel, 1, 0, 1, 1);
        progressPane.add(progressBar, 0, 1, 2, 1);

        this.add(progressPane, 0, 0, 1, 1);
        this.setLocalization(localization);

        jobScheduler.addJobListener(new JobListenerImpl());

    }

    class JobListenerImpl implements JobListener, JobProgressListener {

        @Override
        public void onJobStarting(ActiveJob job) {
            job.getProgressListeners().add(this);
            Platform.runLater(() -> {
                UiSchedulerPane.this.getJobLabel().setText(job.getAcceptedJob().getJob().getTitle());
                UiSchedulerPane.this.getProgressBar().setProgress(ProgressBar.INDETERMINATE_PROGRESS);
            });
        }

        @Override
        public void onJobCompleted(ActiveJob job) {
            job.getProgressListeners().remove(this);
            Platform.runLater(() -> {
                UiSchedulerPane.this.getJobLabel().setText(UiSchedulerPane.this.getLocalization().noActivity());
                UiSchedulerPane.this.getProgressBar().setProgress(0d);
                UiSchedulerPane.this.getProgressLabel().setText(" ");
            });
        }

        @Override
        public void onProgress(Double progress, String message) {
            if (progress != null) {
                Platform.runLater(() -> UiSchedulerPane.this.getProgressBar().setProgress(progress.doubleValue()));
            }
            if (StringUtils.isNotEmpty(message)) {
                Platform.runLater(() -> UiSchedulerPane.this.getProgressLabel().setText(message));
            }
        }

    }

    private Label getJobLabel() {
        return this.jobLabel;
    }
    private void setJobLabel(Label jobLabel) {
        this.jobLabel = jobLabel;
    }

    private ProgressBar getProgressBar() {
        return this.progressBar;
    }
    private void setProgressBar(ProgressBar progressBar) {
        this.progressBar = progressBar;
    }

    private Label getProgressLabel() {
        return this.progressLabel;
    }
    private void setProgressLabel(Label progressLabel) {
        this.progressLabel = progressLabel;
    }

    private Localization getLocalization() {
        return this.localization;
    }
    private void setLocalization(Localization localization) {
        this.localization = localization;
    }

}
