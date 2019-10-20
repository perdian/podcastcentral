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
package de.perdian.apps.podcentral.ui.components.feeds.add;

import de.perdian.apps.podcentral.core.tasks.TaskExecutor;
import de.perdian.apps.podcentral.ui.localization.Localization;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.DialogPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class AddFeedAction implements EventHandler<ActionEvent> {

    private TaskExecutor taskExecutor = null;
    private Localization localization = null;

    public AddFeedAction(TaskExecutor taskExecutor, Localization localization) {
        this.setTaskExecutor(taskExecutor);
        this.setLocalization(localization);
    }

    @Override
    public void handle(ActionEvent event) {

        DialogPane dialogPane = new DialogPane();
        dialogPane.setContent(new AddFeedPane(this.getTaskExecutor(), this.getLocalization()));
        dialogPane.setMinSize(640, 480);

        Stage dialogStage = new Stage(StageStyle.UTILITY);
        dialogStage.setTitle(this.getLocalization().addFeed());
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        dialogStage.setScene(new Scene(dialogPane));
        dialogStage.show();

    }


    private TaskExecutor getTaskExecutor() {
        return this.taskExecutor;
    }
    private void setTaskExecutor(TaskExecutor taskExecutor) {
        this.taskExecutor = taskExecutor;
    }

    private Localization getLocalization() {
        return this.localization;
    }
    private void setLocalization(Localization localization) {
        this.localization = localization;
    }

}
