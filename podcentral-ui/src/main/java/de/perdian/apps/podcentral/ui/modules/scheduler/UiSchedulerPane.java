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
    private Localization localization = null;

    public UiSchedulerPane(JobScheduler jobScheduler, Localization localization) {

        ProgressBar progressBar = new ProgressBar(0);
        progressBar.setMaxWidth(Double.MAX_VALUE);
        GridPane.setHgrow(progressBar, Priority.ALWAYS);
        GridPane.setMargin(progressBar, new Insets(0, 4, 0, 0));
        this.setProgressBar(progressBar);

        Label jobLabel = new Label(localization.noActivity());
        jobLabel.setFont(Font.font(jobLabel.getFont().getFamily(), jobLabel.getFont().getSize() * 0.8));
        jobLabel.setPrefWidth(150);
        GridPane.setHgrow(jobLabel, Priority.ALWAYS);
        GridPane.setMargin(jobLabel, new Insets(0, 0, 0, 4));
        this.setJobLabel(jobLabel);

        GridPane progressPane = new GridPane();
        progressPane.add(progressBar, 0, 0, 1, 1);
        progressPane.add(jobLabel, 1, 0, 1, 1);

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
            });
        }

        @Override
        public void onProgress(Double progress, String message) {
            if (progress != null) {
                Platform.runLater(() -> UiSchedulerPane.this.getProgressBar().setProgress(progress.doubleValue()));
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

    private Localization getLocalization() {
        return this.localization;
    }
    private void setLocalization(Localization localization) {
        this.localization = localization;
    }

}
