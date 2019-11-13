package de.perdian.apps.podcentral.ui.modules.library.components.feeds;

import de.perdian.apps.podcentral.ui.Central;
import de.perdian.apps.podcentral.ui.modules.library.actions.AddFeedActionEventHandler;
import de.perdian.apps.podcentral.ui.support.localization.Localization;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.stage.Stage;

public class AddFeedActionExample {

    public static class AddFeedActionExampleApplication extends Application {

        @Override
        public void start(Stage primaryStage) throws Exception {
            Central central = new Central(new Localization() {});
            AddFeedActionEventHandler addFeedActionEventHandler = new AddFeedActionEventHandler(central.getLibrary(), new Localization() {});
            addFeedActionEventHandler.handle(new ActionEvent(primaryStage, null));
        }

    }

    public static void main(String[] args) throws Exception {
        Application.launch(AddFeedActionExampleApplication.class);
    }

}
