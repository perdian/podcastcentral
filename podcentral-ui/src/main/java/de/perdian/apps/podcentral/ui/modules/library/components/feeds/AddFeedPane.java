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
package de.perdian.apps.podcentral.ui.modules.library.components.feeds;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.perdian.apps.podcentral.model.FeedInput;
import de.perdian.apps.podcentral.sources.feeds.FeedInputLoader;
import de.perdian.apps.podcentral.ui.support.errors.ExceptionDialogBuilder;
import de.perdian.apps.podcentral.ui.support.localization.Localization;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableBooleanValue;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.input.Clipboard;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;

public class AddFeedPane extends GridPane {

    private static final Logger log = LoggerFactory.getLogger(AddFeedPane.class);

    private Consumer<FeedInput> feedInputConsumer = null;
    private Localization localization = null;
    private BooleanProperty busyProperty = null;
    private StringProperty feedUrlProperty = null;
    private BorderPane detailsWrapperPane = null;

    public AddFeedPane(Consumer<FeedInput> feedInputConsumer, Localization localization) {

        this.setBusyProperty(new SimpleBooleanProperty(false));
        this.setFeedInputConsumer(feedInputConsumer);
        this.setLocalization(localization);

        Label feedUrlLabel = new Label(localization.feedUrl());
        TextField feedUrlField = new TextField();
        GridPane.setHgrow(feedUrlField, Priority.ALWAYS);
        GridPane.setMargin(feedUrlField, new Insets(2, 0, 0, 0));
        this.setFeedUrlProperty(feedUrlField.textProperty());

        Button loadFeedButton = new Button(localization.loadFeed());
        loadFeedButton.disableProperty().bind(Bindings.isEmpty(feedUrlField.textProperty()).or(this.getBusy()));
        loadFeedButton.setOnAction(event -> this.loadFeedUrl(feedUrlField.getText()));
        GridPane.setMargin(loadFeedButton, new Insets(0, 0, 0, 4));
        feedUrlField.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ENTER) {
                loadFeedButton.fire();
            }
        });

        Separator separator = new Separator();
        GridPane.setMargin(separator, new Insets(8, 0, 8, 0));

        Label feedDetailsWrapperLabel = new Label(localization.noFeedLoadedYet());
        BorderPane feedDetailsWrapperPane = new BorderPane(feedDetailsWrapperLabel);
        GridPane.setHgrow(feedDetailsWrapperPane, Priority.ALWAYS);
        GridPane.setVgrow(feedDetailsWrapperPane, Priority.ALWAYS);
        this.setDetailsWrapperPane(feedDetailsWrapperPane);

        this.add(feedUrlLabel, 0, 0, 2, 1);
        this.add(feedUrlField, 0, 1, 1, 1);
        this.add(loadFeedButton, 1, 1, 1, 1);
        this.add(separator, 0, 2, 2, 1);
        this.add(feedDetailsWrapperPane, 0, 3, 2, 1);

        Clipboard clipboard = Clipboard.getSystemClipboard();
        String clipboardContent = clipboard.getString();
        try {
            URL clipboardUrl = new URL(clipboardContent);
            feedUrlField.setText(clipboardUrl.toString());
            this.loadFeedUrl(clipboardUrl.toString());
        } catch (MalformedURLException e) {
            // Ignore invalID URL
        }

    }

    private synchronized void loadFeedUrl(String feedUrl) {
        Platform.runLater(() -> this.getBusyProperty().setValue(Boolean.TRUE));
        new Thread(() -> {
            try {

                Platform.runLater(() -> AddFeedPane.this.getDetailsWrapperPane().setCenter(AddFeedPane.this.createLoadFeedBusyPane(feedUrl)));
                FeedInput feedInput = FeedInputLoader.loadFeedInputFromUrl(feedUrl);
                FeedDataPane feedDataPane = new FeedDataPane(feedInput.getData(), AddFeedPane.this.getLocalization());
                GridPane.setHgrow(feedDataPane, Priority.ALWAYS);
                GridPane.setVgrow(feedDataPane, Priority.ALWAYS);

                Separator feedInputSeparator = new Separator();
                GridPane.setMargin(feedInputSeparator, new Insets(8, 0, 8, 0));

                Button addFeedButton = new Button(this.getLocalization().addFeed());
                addFeedButton.setOnAction(event -> this.getFeedInputConsumer().accept(feedInput));
                HBox buttonBox = new HBox(addFeedButton);
                buttonBox.setAlignment(Pos.CENTER);

                GridPane feedInputWrapperPane = new GridPane();
                feedInputWrapperPane.add(feedDataPane, 0, 0, 1, 1);
                feedInputWrapperPane.add(feedInputSeparator, 0, 1, 1, 1);
                feedInputWrapperPane.add(buttonBox, 0, 2, 1, 1);
                Platform.runLater(() -> AddFeedPane.this.getDetailsWrapperPane().setCenter(feedInputWrapperPane));

            } catch (Exception e) {
                log.info("Cannot load feed from URL: {}", feedUrl, e);
                Platform.runLater(() -> new ExceptionDialogBuilder().withException(e).withTitle(AddFeedPane.this.getLocalization().cannotLoadFeedFromUrl(feedUrl)).createDialog().showAndWait());
            } finally {
                Platform.runLater(() -> AddFeedPane.this.getBusyProperty().setValue(Boolean.FALSE));
            }
        }).start();
    }

    private Pane createLoadFeedBusyPane(String feedUrl) {

        Label loadingFeedTitleLabel = new Label(this.getLocalization().loadingFeedFrom());
        loadingFeedTitleLabel.setAlignment(Pos.CENTER);
        loadingFeedTitleLabel.setMaxWidth(Double.MAX_VALUE);
        GridPane.setHgrow(loadingFeedTitleLabel, Priority.ALWAYS);

        Label loadingFeedUrlLabel = new Label(feedUrl);
        loadingFeedUrlLabel.setAlignment(Pos.CENTER);
        loadingFeedUrlLabel.setMaxWidth(Double.MAX_VALUE);
        GridPane.setHgrow(loadingFeedUrlLabel, Priority.ALWAYS);

        ProgressBar progressBar = new ProgressBar(ProgressBar.INDETERMINATE_PROGRESS);
        progressBar.setMaxWidth(Double.MAX_VALUE);
        GridPane.setHgrow(progressBar, Priority.ALWAYS);
        GridPane.setMargin(progressBar, new Insets(16, 32, 0, 32));

        GridPane loadingPane = new GridPane();
        loadingPane.setAlignment(Pos.CENTER);
        loadingPane.add(loadingFeedTitleLabel, 0, 0, 1, 1);
        loadingPane.add(loadingFeedUrlLabel, 0, 1, 1, 1);
        loadingPane.add(progressBar, 0, 2, 1, 1);
        return loadingPane;

    }


    private Consumer<FeedInput> getFeedInputConsumer() {
        return this.feedInputConsumer;
    }
    private void setFeedInputConsumer(Consumer<FeedInput> feedInputConsumer) {
        this.feedInputConsumer = feedInputConsumer;
    }

    private Localization getLocalization() {
        return this.localization;
    }
    private void setLocalization(Localization localization) {
        this.localization = localization;
    }
    public ObservableBooleanValue getBusy() {
        return this.getBusyProperty();
    }
    public BooleanProperty getBusyProperty() {
        return this.busyProperty;
    }
    public void setBusyProperty(BooleanProperty busyProperty) {
        this.busyProperty = busyProperty;
    }

    public StringProperty getFeedUrl() {
        return this.getFeedUrlProperty();
    }
    private StringProperty getFeedUrlProperty() {
        return this.feedUrlProperty;
    }
    private void setFeedUrlProperty(StringProperty feedUrlProperty) {
        this.feedUrlProperty = feedUrlProperty;
    }

    BorderPane getDetailsWrapperPane() {
        return this.detailsWrapperPane;
    }
    private void setDetailsWrapperPane(BorderPane detailsWrapperPane) {
        this.detailsWrapperPane = detailsWrapperPane;
    }

}
