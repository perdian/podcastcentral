package de.perdian.apps.podcentral.ui.components.feeds.add;

import de.perdian.apps.podcentral.ui.localization.Localization;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class AddFeedPaneExample {

    public static void main(String[] args) throws Exception {
        Application.launch(AddFeedPaneExampleApplication.class);
    }

    public static class AddFeedPaneExampleApplication extends Application {

        @Override
        public void start(Stage primaryStage) throws Exception {

            AddFeedPane pane = new AddFeedPane(new Localization() {}, feedInput -> {});
            pane.setPadding(new Insets(8, 8, 8, 8));
            pane.getFeedUrlProperty().setValue("https://podcasts.files.bbci.co.uk/w13xttx2.rss");

            primaryStage.setScene(new Scene(pane));
            primaryStage.setOnCloseRequest(event -> System.exit(0));
            primaryStage.setMinWidth(800);
            primaryStage.setMinHeight(600);
            primaryStage.show();

        }

    }


}
