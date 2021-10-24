/*
 * Copyright 2013-2018 Christian Seifert
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
package de.perdian.apps.podcastcentral.ui.support.fx.components;

import org.apache.commons.lang3.StringUtils;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

public class ProgressPane extends GridPane {

    private Label titleLabel = null;
    private Label subtitleLabel = null;
    private ProgressBar progressBar = null;
    private Label messageLabel = null;

    public ProgressPane() {

        Label titleLabel = new Label(" ");
        titleLabel.setMaxWidth(Double.MAX_VALUE);
        GridPane.setHgrow(titleLabel, Priority.ALWAYS);
        this.setTitleLabel(titleLabel);

        Label subtitleLabel = new Label(" ");
        subtitleLabel.setMaxWidth(Double.MAX_VALUE);
        GridPane.setHgrow(subtitleLabel, Priority.ALWAYS);
        this.setSubtitleLabel(subtitleLabel);

        ProgressBar progressBar = new ProgressBar();
        progressBar.setMaxWidth(Double.MAX_VALUE);
        progressBar.setMaxHeight(Double.MAX_VALUE);
        GridPane.setHgrow(progressBar, Priority.ALWAYS);
        GridPane.setVgrow(progressBar, Priority.ALWAYS);
        GridPane.setMargin(progressBar, new Insets(4, 0, 0, 0));
        this.setProgressBar(progressBar);

        Label messageLabel = new Label(" ");
        messageLabel.setMaxWidth(Double.MAX_VALUE);
        GridPane.setMargin(messageLabel, new Insets(4, 0, 0, 0));
        this.setMessageLabel(messageLabel);

        this.add(titleLabel, 0, 0, 1, 1);
        this.add(subtitleLabel, 0, 1, 1, 1);
        this.add(progressBar, 0, 2, 1, 1);
        this.add(messageLabel, 0, 3, 1, 1);

    }

    public void updateProgress(Double progress, String message) {
        if (progress != null) {
            Platform.runLater(() -> this.getProgressBar().setProgress(progress));
        } else {
            Platform.runLater(() -> this.getProgressBar().setProgress(ProgressBar.INDETERMINATE_PROGRESS));
        }
        if (!StringUtils.isEmpty(message)) {
            Platform.runLater(() -> this.getMessageLabel().setText(message));
        }
    }

    public void setTitle(String title) {
        this.getTitleLabel().setText(title);
        this.getTitleLabel().setTooltip(new Tooltip(title));
    }
    public String getTitle() {
        return this.getTitleLabel().getText();
    }

    private Label getTitleLabel() {
        return this.titleLabel;
    }
    private void setTitleLabel(Label titleLabel) {
        this.titleLabel = titleLabel;
    }

    public void setSubtitle(String subtitle) {
        this.getSubtitleLabel().setText(subtitle);
        this.getSubtitleLabel().setTooltip(new Tooltip(subtitle));
    }
    public String getSubtitle() {
        return this.getSubtitleLabel().getText();
    }

    public Label getSubtitleLabel() {
        return this.subtitleLabel;
    }
    public void setSubtitleLabel(Label subtitleLabel) {
        this.subtitleLabel = subtitleLabel;
    }

    private ProgressBar getProgressBar() {
        return this.progressBar;
    }
    private void setProgressBar(ProgressBar progressBar) {
        this.progressBar = progressBar;
    }

    private Label getMessageLabel() {
        return this.messageLabel;
    }
    private void setMessageLabel(Label messageLabel) {
        this.messageLabel = messageLabel;
    }

}
