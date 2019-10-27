package de.perdian.apps.podcentral.ui.components.library;

import de.perdian.apps.podcentral.core.model.Library;
import de.perdian.apps.podcentral.ui.components.feeds.add.AddFeedAction;
import de.perdian.apps.podcentral.ui.localization.Localization;
import de.perdian.apps.podcentral.ui.support.icons.IconFactory;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.layout.BorderPane;

public class LibraryToolbarPane extends BorderPane {

    public LibraryToolbarPane(Library library, Localization localization) {

        Button addFeedButton = new Button(localization.addFeed());
        addFeedButton.setOnAction(new AddFeedAction(library, localization));
        ButtonBar.setButtonData(addFeedButton, ButtonData.LEFT);

        ButtonBar buttonBar = new ButtonBar();
        buttonBar.getButtons().add(IconFactory.createButton("\uf001"));
        buttonBar.getButtons().add(IconFactory.createButton("\uf044"));

        buttonBar.getButtons().add(addFeedButton);
        this.setPadding(new Insets(2, 2, 2, 2));
        this.setLeft(buttonBar);

    }

}
