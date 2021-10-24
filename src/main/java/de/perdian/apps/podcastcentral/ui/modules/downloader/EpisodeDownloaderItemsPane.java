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
package de.perdian.apps.podcastcentral.ui.modules.downloader;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.function.Function;

import de.perdian.apps.podcastcentral.model.Episode;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

class EpisodeDownloaderItemsPane extends BorderPane {

    <T extends Pane & EpisodeDownloaderItemPane> EpisodeDownloaderItemsPane(ObservableList<Episode> episodes, Function<Episode, T> itemPaneFunction) {

        VBox episodePanesBox = new VBox(4);
        episodePanesBox.setPadding(new Insets(4, 0, 4, 0));

        ScrollPane scrollPane = new ScrollPane(episodePanesBox);
        scrollPane.setStyle("-fx-background-color: transparent;");
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollBarPolicy.AS_NEEDED);
        this.setCenter(scrollPane);

        Map<Episode, T> episodePanesByEpisode = new IdentityHashMap<>();
        Map<Episode, ChangeListener<Double>> progressChangeListenersByEpisode = new IdentityHashMap<>();
        episodes.addListener((ListChangeListener.Change<? extends Episode> change) -> {
            synchronized (EpisodeDownloaderItemsPane.this) {
                while (change.next()) {
                    change.getAddedSubList().forEach(episode -> {
                        if (!episodePanesByEpisode.containsKey(episode)) {
                            T episodePane = itemPaneFunction.apply(episode);
                            ChangeListener<Double> progressChangeListener = (o, oldValue, newValue) -> episodePane.updateProgress(newValue, episode);
                            episodePanesByEpisode.put(episode, episodePane);
                            progressChangeListenersByEpisode.put(episode, progressChangeListener);
                            episode.getDownloadProgress().addListener(progressChangeListener);
                            Platform.runLater(() -> episodePanesBox.getChildren().add(episodePane));
                        }
                    });
                    change.getRemoved().forEach(episode -> {
                        T episodePane = episodePanesByEpisode.remove(episode);
                        if (episodePane != null) {
                            Platform.runLater(() -> episodePanesBox.getChildren().remove(episodePane));
                        }
                        ChangeListener<Double> progressChangeListener = progressChangeListenersByEpisode.remove(episode);
                        if (progressChangeListener != null) {
                            episode.getDownloadProgress().removeListener(progressChangeListener);
                        }
                    });
                }
            }
        });

    }

}
