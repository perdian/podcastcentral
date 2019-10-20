package de.perdian.apps.podcentral.ui;

import de.perdian.apps.podcentral.core.tasks.TaskExecutor;
import de.perdian.apps.podcentral.ui.components.feeds.add.AddFeedAction;
import de.perdian.apps.podcentral.ui.localization.Localization;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.stage.Stage;

public class ComponentTest {

    public static class ComponentTestApplication extends Application {

        @Override
        public void start(Stage primaryStage) throws Exception {
            TaskExecutor taskExecutor = new TaskExecutor();
            AddFeedAction addFeedAction = new AddFeedAction(taskExecutor, new Localization() {});
            addFeedAction.handle(new ActionEvent(primaryStage, null));
        }

    }


    public static void main(String[] args) throws Exception {
        Application.launch(ComponentTestApplication.class);
    }

}
