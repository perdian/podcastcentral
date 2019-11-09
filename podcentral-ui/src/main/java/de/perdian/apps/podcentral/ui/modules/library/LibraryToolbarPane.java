package de.perdian.apps.podcentral.ui.modules.library;

import java.util.Set;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import de.perdian.apps.podcentral.model.Library;
import de.perdian.apps.podcentral.ui.modules.feeds.RefreshFeedsActionEventHandler;
import de.perdian.apps.podcentral.ui.modules.feeds.components.add.AddFeedAction;
import de.perdian.apps.podcentral.ui.support.backgroundtasks.BackgroundTaskExecutor;
import de.perdian.apps.podcentral.ui.support.localization.Localization;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.layout.BorderPane;

public class LibraryToolbarPane extends BorderPane {

    public LibraryToolbarPane(BackgroundTaskExecutor backgroundTaskExecutor, Library library, Localization localization) {

        Button addFeedButton = new Button(localization.addFeed(), new FontAwesomeIconView(FontAwesomeIcon.PLUS));
        addFeedButton.setOnAction(new AddFeedAction(library, localization));
        ButtonBar.setButtonData(addFeedButton, ButtonData.LEFT);

        Button refreshFeedsButton = new Button(localization.refreshFeeds(), new FontAwesomeIconView(FontAwesomeIcon.REFRESH));
        refreshFeedsButton.setOnAction(new RefreshFeedsActionEventHandler(() -> library.getFeeds(), Set.of(), () -> {}, backgroundTaskExecutor, localization));
        ButtonBar.setButtonData(refreshFeedsButton, ButtonData.LEFT);

        ButtonBar buttonBar = new ButtonBar();
        buttonBar.getButtons().addAll(addFeedButton, refreshFeedsButton);

        this.setLeft(buttonBar);

    }

}
