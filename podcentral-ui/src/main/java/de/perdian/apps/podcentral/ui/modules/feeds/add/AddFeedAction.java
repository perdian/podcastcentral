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
package de.perdian.apps.podcentral.ui.modules.feeds.add;

import java.util.function.Consumer;

import de.perdian.apps.podcentral.model.FeedInput;
import de.perdian.apps.podcentral.model.FeedInputOptions;
import de.perdian.apps.podcentral.model.Library;
import de.perdian.apps.podcentral.ui.localization.Localization;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.DialogPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class AddFeedAction implements EventHandler<ActionEvent> {

    private Library library = null;
    private Localization localization = null;

    public AddFeedAction(Library library, Localization localization) {
        this.setLibrary(library);
        this.setLocalization(localization);
    }

    @Override
    public void handle(ActionEvent event) {

        Stage dialogStage = new Stage(StageStyle.UTILITY);
        dialogStage.sizeToScene();
        dialogStage.setTitle(this.getLocalization().addFeed());
        dialogStage.initModality(Modality.APPLICATION_MODAL);

        Consumer<FeedInput> feedInputConsumer = feedInput -> {
            dialogStage.close();
            FeedInputOptions feedInputOptions = new FeedInputOptions();
            new Thread(() -> this.getLibrary().updateFeedFromInput(feedInput, feedInputOptions)).start();
        };

        DialogPane dialogPane = new DialogPane();
        dialogPane.setContent(new AddFeedPane(this.getLocalization(), feedInputConsumer));
        dialogPane.setMinSize(640, 400);

        dialogStage.setScene(new Scene(dialogPane));
        dialogStage.show();

    }

    private Library getLibrary() {
        return this.library;
    }
    private void setLibrary(Library library) {
        this.library = library;
    }

    private Localization getLocalization() {
        return this.localization;
    }
    private void setLocalization(Localization localization) {
        this.localization = localization;
    }

}
