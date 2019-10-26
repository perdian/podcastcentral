package de.perdian.apps.podcentral.ui.components.feeds.add;

import java.util.ServiceLoader;

import de.perdian.apps.podcentral.core.model.Library;
import de.perdian.apps.podcentral.core.model.LibraryFactory;
import de.perdian.apps.podcentral.ui.localization.Localization;
import de.perdian.apps.podcentral.ui.support.tasks.TaskExecutor;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.stage.Stage;

public class AddFeedActionExample {

    public static class AddFeedActionExampleApplication extends Application {

        @Override
        public void start(Stage primaryStage) throws Exception {
            TaskExecutor taskExecutor = new TaskExecutor();
            LibraryFactory libraryFactory = ServiceLoader.load(LibraryFactory.class).findFirst().orElseThrow(() -> new IllegalArgumentException("Cannot find ServiceLoader for class: " + LibraryFactory.class.getName()));
            Library library = libraryFactory.createLibrary();
            AddFeedAction addFeedAction = new AddFeedAction(library, taskExecutor, new Localization() {});
            addFeedAction.handle(new ActionEvent(primaryStage, null));
        }

    }


    public static void main(String[] args) throws Exception {
        Application.launch(AddFeedActionExampleApplication.class);
    }

}
