package de.perdian.apps.podcentral.ui.components.toolbar;

import de.perdian.apps.podcentral.ui.Central;
import de.perdian.apps.podcentral.ui.components.feeds.add.AddFeedAction;
import de.perdian.apps.podcentral.ui.localization.Localization;
import de.perdian.apps.podcentral.ui.support.icons.IconFactory;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.layout.BorderPane;

public class ToolbarPane extends BorderPane {

    public ToolbarPane(Central central, Localization localization) {

        Button addFeedButton = new Button(localization.addFeed());
        addFeedButton.setOnAction(new AddFeedAction(central, localization));
        ButtonBar.setButtonData(addFeedButton, ButtonData.LEFT);

        ButtonBar buttonBar = new ButtonBar();
        buttonBar.getButtons().add(IconFactory.createButton("\uf001"));
        buttonBar.getButtons().add(IconFactory.createButton("\uf044"));

        buttonBar.getButtons().add(addFeedButton);
        this.setPadding(new Insets(4, 8, 4, 8));
        this.setLeft(buttonBar);

    }

}
