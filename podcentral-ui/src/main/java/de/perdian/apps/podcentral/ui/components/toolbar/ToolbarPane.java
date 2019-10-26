package de.perdian.apps.podcentral.ui.components.toolbar;

import de.perdian.apps.podcentral.core.model.Library;
import de.perdian.apps.podcentral.ui.components.feeds.add.AddFeedAction;
import de.perdian.apps.podcentral.ui.localization.Localization;
import de.perdian.apps.podcentral.ui.support.tasks.TaskExecutor;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.layout.BorderPane;

public class ToolbarPane extends BorderPane {

    public ToolbarPane(Library library, TaskExecutor taskExecutor, Localization localization) {

        Button addFeedButton = new Button(localization.addFeed());
        addFeedButton.setOnAction(new AddFeedAction(library, taskExecutor, localization));
        ButtonBar.setButtonData(addFeedButton, ButtonData.LEFT);

        ButtonBar buttonBar = new ButtonBar();
        buttonBar.getButtons().add(addFeedButton);
        this.setPadding(new Insets(4, 8, 4, 8));
        this.setLeft(buttonBar);

    }

}
