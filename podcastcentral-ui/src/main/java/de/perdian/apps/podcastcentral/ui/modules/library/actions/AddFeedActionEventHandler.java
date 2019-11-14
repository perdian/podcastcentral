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
package de.perdian.apps.podcastcentral.ui.modules.library.actions;

import java.util.function.Consumer;

import de.perdian.apps.podcastcentral.model.Feed;
import de.perdian.apps.podcastcentral.model.FeedInput;
import de.perdian.apps.podcastcentral.model.FeedInputState;
import de.perdian.apps.podcastcentral.model.Library;
import de.perdian.apps.podcastcentral.ui.modules.library.components.feeds.AddFeedPane;
import de.perdian.apps.podcastcentral.ui.support.localization.Localization;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.DialogPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class AddFeedActionEventHandler implements EventHandler<ActionEvent> {

    private Library library = null;
    private Localization localization = null;

    public AddFeedActionEventHandler(Library library, Localization localization) {
        this.setLibrary(library);
        this.setLocalization(localization);
    }

    @Override
    public void handle(ActionEvent event) {

        Stage dialogStage = new Stage();
        dialogStage.sizeToScene();
        dialogStage.setResizable(false);
        dialogStage.setTitle(this.getLocalization().addFeed());
        dialogStage.initModality(Modality.APPLICATION_MODAL);

        Consumer<FeedInput> feedInputConsumer = feedInput -> {
            dialogStage.close();
            new Thread(() -> {
                Feed feed = this.getLibrary().addFeed(feedInput);
                feed.getInputState().setValue(FeedInputState.OKAY);
            }).start();
        };

        DialogPane dialogPane = new DialogPane();
        dialogPane.setContent(new AddFeedPane(feedInputConsumer, this.getLocalization()));
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
