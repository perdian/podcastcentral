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
package de.perdian.apps.podcastcentral.ui.modules.library.components.feeds;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.function.Consumer;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import de.perdian.apps.podcastcentral.model.FeedInput;
import de.perdian.apps.podcastcentral.sources.feeds.FeedInputLoader;
import de.perdian.apps.podcastcentral.ui.support.localization.Localization;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.input.Clipboard;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class AddFeedPane extends GridPane {

    private static final Logger log = LoggerFactory.getLogger(AddFeedPane.class);

    private BooleanProperty busyProperty = null;
    private Localization localization = null;
    private TargetAreaPane targetAreaPane = null;

    public AddFeedPane(Consumer<FeedInput> feedInputConsumer, Localization localization) {

        BooleanProperty busyProperty = new SimpleBooleanProperty(false);
        this.setBusyProperty(busyProperty);
        this.setLocalization(localization);

        SourceSelectionPane sourceSelectionPane = new SourceSelectionPane(busyProperty, localization);
        TitledPane sourceSelectionTitledPane = new TitledPane(localization.feedSource(), sourceSelectionPane);
        sourceSelectionTitledPane.setFocusTraversable(false);
        sourceSelectionTitledPane.setCollapsible(false);
        GridPane.setHgrow(sourceSelectionTitledPane, Priority.ALWAYS);

        TargetAreaPane targetAreaPane = new TargetAreaPane(localization, feedInputConsumer);
        TitledPane targetAreaTitledPane = new TitledPane(localization.feedDetails(), targetAreaPane);
        targetAreaTitledPane.setFocusTraversable(false);
        targetAreaTitledPane.setCollapsible(false);
        targetAreaTitledPane.setMaxHeight(Double.MAX_VALUE);
        GridPane.setHgrow(targetAreaTitledPane, Priority.ALWAYS);
        GridPane.setVgrow(targetAreaTitledPane, Priority.ALWAYS);
        this.setTargetAreaPane(targetAreaPane);

        this.add(sourceSelectionTitledPane, 0, 0, 1, 1);
        this.add(targetAreaTitledPane, 0, 1, 1, 1);
        this.setPadding(new Insets(8, 8, 8, 8));
        this.setVgap(8);

    }

    private synchronized void loadFeedFromUrl(String feedUrl) {
        log.debug("Loading feed data from URL: {}", feedUrl);
        this.getBusyProperty().setValue(Boolean.TRUE);
        new Thread(() -> {
            try {
                this.getTargetAreaPane().updateLoadingProgress(feedUrl);
                this.getTargetAreaPane().updateFeed(FeedInputLoader.loadFeedInputFromUrl(feedUrl));
            } catch (Exception e) {
                this.getTargetAreaPane().updateError(e, feedUrl);
            } finally {
                Platform.runLater(() -> this.getBusyProperty().setValue(Boolean.FALSE));
            }
        }).start();
    }

    private class SourceSelectionPane extends GridPane {

        private SourceSelectionPane(BooleanProperty busy, Localization localization) {

            Label feedUrlLabel = new Label(localization.feedUrl());
            TextField feedUrlField = new TextField();
            GridPane.setHgrow(feedUrlField, Priority.ALWAYS);
            GridPane.setMargin(feedUrlField, new Insets(2, 0, 0, 0));

            Clipboard clipboard = Clipboard.getSystemClipboard();
            String clipboardContent = clipboard.getString();
            try {
                URL clipboardUrl = new URL(clipboardContent);
                feedUrlField.setText(clipboardUrl.toString());
            } catch (MalformedURLException e) {
                // Ignore invalID URL
            }

            Button loadFeedButton = new Button(localization.loadFeed(), new FontAwesomeIconView(FontAwesomeIcon.REFRESH));
            loadFeedButton.disableProperty().bind(Bindings.isEmpty(feedUrlField.textProperty()).or(busy));
            loadFeedButton.setOnAction(event -> loadFeedFromUrl(feedUrlField.getText()));
            GridPane.setMargin(loadFeedButton, new Insets(0, 0, 0, 4));
            feedUrlField.setOnKeyPressed(keyEvent -> {
                if (keyEvent.getCode() == KeyCode.ENTER) {
                    loadFeedButton.fire();
                }
            });

            this.setPadding(new Insets(8, 8, 8, 8));
            this.add(feedUrlLabel, 0, 0, 2, 1);
            this.add(feedUrlField, 0, 1, 1, 1);
            this.add(loadFeedButton, 1, 1, 1, 1);

        }

    }

    private class TargetAreaPane extends BorderPane {

        private Consumer<FeedInput> feedInputConsumer = null;

        private TargetAreaPane(Localization localization, Consumer<FeedInput> feedInputConsumer) {
            this.setFeedInputConsumer(feedInputConsumer);
            this.setCenter(new Label(localization.noFeedLoadedYet()));
            this.setPadding(new Insets(8, 8, 8, 8));
        }

        private void updateLoadingProgress(String feedUrl) {
            Label titleLabel = new Label(getLocalization().loadingFeedFrom());
            Label urlLabel = new Label(feedUrl);
            ProgressBar progressBar = new ProgressBar(ProgressBar.INDETERMINATE_PROGRESS);
            VBox.setMargin(progressBar, new Insets(8, 0, 0, 0));
            VBox progressPane = new VBox(titleLabel, urlLabel, progressBar);
            progressPane.setAlignment(Pos.CENTER);
            progressPane.setMaxWidth(Double.MAX_VALUE);
            Platform.runLater(() -> this.setCenter(progressPane));
        }

        private void updateFeed(FeedInput feedInput) {
            FeedDataPane feedDataPane = new FeedDataPane(feedInput.getData(), feedInput.getEpisodes(), getLocalization());
            Button addFeedButton = new Button(getLocalization().addFeed());
            addFeedButton.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.PLUS));
            addFeedButton.setOnAction(event -> this.getFeedInputConsumer().accept(feedInput));
            BorderPane buttonPane = new BorderPane(addFeedButton);
            buttonPane.setPadding(new Insets(9, 0, 0, 0));
            BorderPane updateFeedPane = new BorderPane();
            updateFeedPane.setCenter(feedDataPane);
            updateFeedPane.setBottom(buttonPane);
            Platform.runLater(() -> this.setCenter(updateFeedPane));
        }

        private void updateError(Exception e, String feedUrl) {
            Label iconLabel = new Label("", new FontAwesomeIconView(FontAwesomeIcon.EXCLAMATION_TRIANGLE));
            VBox.setMargin(iconLabel, new Insets(0, 0, 2, 0));
            Label titleLabel = new Label(getLocalization().cannotLoadFeedFromUrl());
            VBox.setMargin(titleLabel, new Insets(2, 0, 2, 0));
            Label feedUrlLabel = new Label(feedUrl);
            TextArea errorMessageArea = new TextArea(ExceptionUtils.getStackTrace(e));
            errorMessageArea.setEditable(false);
            VBox.setMargin(errorMessageArea, new Insets(8, 0, 0, 0));
            VBox.setVgrow(errorMessageArea, Priority.ALWAYS);
            VBox errorPane = new VBox(iconLabel, titleLabel, feedUrlLabel, errorMessageArea);
            errorPane.setAlignment(Pos.CENTER);
            Platform.runLater(() -> this.setCenter(errorPane));
        }

        private Consumer<FeedInput> getFeedInputConsumer() {
            return this.feedInputConsumer;
        }
        private void setFeedInputConsumer(Consumer<FeedInput> feedInputConsumer) {
            this.feedInputConsumer = feedInputConsumer;
        }

    }

    private BooleanProperty getBusyProperty() {
        return this.busyProperty;
    }
    private void setBusyProperty(BooleanProperty busyProperty) {
        this.busyProperty = busyProperty;
    }

    private Localization getLocalization() {
        return this.localization;
    }
    private void setLocalization(Localization localization) {
        this.localization = localization;
    }

    private TargetAreaPane getTargetAreaPane() {
        return this.targetAreaPane;
    }
    private void setTargetAreaPane(TargetAreaPane targetAreaPane) {
        this.targetAreaPane = targetAreaPane;
    }

}
