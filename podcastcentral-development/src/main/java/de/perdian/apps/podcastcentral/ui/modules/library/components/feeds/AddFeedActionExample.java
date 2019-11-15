package de.perdian.apps.podcastcentral.ui.modules.library.components.feeds;

import de.perdian.apps.podcastcentral.ui.Central;
import de.perdian.apps.podcastcentral.ui.modules.library.actions.AddFeedActionEventHandler;
import de.perdian.apps.podcastcentral.ui.support.localization.Localization;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.stage.Stage;

public class AddFeedActionExample {

    public static class AddFeedActionExampleApplication extends Application {

        @Override
        public void start(Stage primaryStage) throws Exception {
            Localization localization = new Localization() {};
            Central central = new Central(localization);
            AddFeedActionEventHandler addFeedActionEventHandler = new AddFeedActionEventHandler(() -> AddFeedDialog.requestFeedInput(null, localization), central.getLibrary(), central.getBackgroundTaskExecutor(), localization);
            addFeedActionEventHandler.handle(new ActionEvent(primaryStage, null));
        }

    }

    public static void main(String[] args) throws Exception {
        Application.launch(AddFeedActionExampleApplication.class);
    }

}
