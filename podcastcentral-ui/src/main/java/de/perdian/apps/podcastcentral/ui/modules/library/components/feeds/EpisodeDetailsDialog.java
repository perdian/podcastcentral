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

import de.perdian.apps.podcastcentral.model.Episode;
import de.perdian.apps.podcastcentral.ui.support.localization.Localization;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class EpisodeDetailsDialog {

    public static void showEpisodeDetails(Episode episode, boolean attachKeyListener, Localization localization) {

        Stage dialogStage = new Stage();
        dialogStage.setTitle(localization.episode());
        dialogStage.initModality(Modality.APPLICATION_MODAL);

        EpisodeDetailsPane episodeDetailsPane = new EpisodeDetailsPane(episode, localization);
        episodeDetailsPane.setMinSize(800, 100);
        episodeDetailsPane.setPadding(new Insets(8, 8, 8, 8));
        if (attachKeyListener) {
            episodeDetailsPane.setOnKeyTyped(keyEvent -> {
                if (" ".equalsIgnoreCase(keyEvent.getCharacter())) {
                    dialogStage.close();
                }
            });
            episodeDetailsPane.setOnKeyPressed(keyEvent -> {
                if (keyEvent.getCode() == KeyCode.ESCAPE) {
                    dialogStage.close();
                }
            });
        }

        dialogStage.setScene(new Scene(episodeDetailsPane));
        dialogStage.sizeToScene();
        dialogStage.setResizable(false);
        dialogStage.showAndWait();

    }

}
