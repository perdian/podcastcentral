package de.perdian.apps.podcentral.ui.components.feeds.add;

import de.perdian.apps.podcentral.ui.Central;
import de.perdian.apps.podcentral.ui.CentralFactory;
import de.perdian.apps.podcentral.ui.localization.Localization;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.stage.Stage;

public class AddFeedActionExample {

    public static class AddFeedActionExampleApplication extends Application {

        @Override
        public void start(Stage primaryStage) throws Exception {
            Central central = CentralFactory.createCentral(new Localization() {});
            AddFeedAction addFeedAction = new AddFeedAction(central, new Localization() {});
            addFeedAction.handle(new ActionEvent(primaryStage, null));
        }

    }


    public static void main(String[] args) throws Exception {
        Application.launch(AddFeedActionExampleApplication.class);
    }

}
