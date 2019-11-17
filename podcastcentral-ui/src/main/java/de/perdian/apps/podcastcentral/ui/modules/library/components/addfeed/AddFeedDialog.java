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
package de.perdian.apps.podcastcentral.ui.modules.library.components.addfeed;

import de.perdian.apps.podcastcentral.model.FeedInput;
import de.perdian.apps.podcastcentral.ui.support.localization.Localization;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class AddFeedDialog {

    public static FeedInput requestFeedInput(Node parentNode, Localization localization) {

        Stage dialogStage = new Stage();
        dialogStage.setTitle(localization.addFeed());
        dialogStage.initModality(Modality.APPLICATION_MODAL);

        ObjectProperty<FeedInput> feedInputProperty = new SimpleObjectProperty<>(null);
        Pane dialogPane = new AddFeedPane(feedInput -> {
            feedInputProperty.setValue(feedInput);
            dialogStage.close();
        }, localization);
        dialogPane.setMinSize(800, 700);

        dialogStage.setScene(new Scene(dialogPane));
        dialogStage.sizeToScene();
        dialogStage.setResizable(false);
        dialogStage.showAndWait();

        return feedInputProperty.getValue();

    }

}
