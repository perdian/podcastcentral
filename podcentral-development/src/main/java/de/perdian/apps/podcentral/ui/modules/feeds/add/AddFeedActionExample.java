package de.perdian.apps.podcentral.ui.modules.feeds.add;

import de.perdian.apps.podcentral.ui.Central;
import de.perdian.apps.podcentral.ui.localization.Localization;
import de.perdian.apps.podcentral.ui.modules.feeds.add.AddFeedAction;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.stage.Stage;

public class AddFeedActionExample {

    public static class AddFeedActionExampleApplication extends Application {

        @Override
        public void start(Stage primaryStage) throws Exception {
            Central central = new Central(new Localization() {});
            AddFeedAction addFeedAction = new AddFeedAction(central.getLibrary(), new Localization() {});
            addFeedAction.handle(new ActionEvent(primaryStage, null));
        }

    }

    public static void main(String[] args) throws Exception {
        Application.launch(AddFeedActionExampleApplication.class);
    }

}
