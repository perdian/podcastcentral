package de.perdian.apps.podcentral.ui.modules.library;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import de.perdian.apps.podcentral.jobscheduler.JobScheduler;
import de.perdian.apps.podcentral.model.Library;
import de.perdian.apps.podcentral.ui.localization.Localization;
import de.perdian.apps.podcentral.ui.modules.feeds.FeedRefreshEventHandler;
import de.perdian.apps.podcentral.ui.modules.feeds.add.AddFeedAction;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.layout.BorderPane;

public class LibraryToolbarPane extends BorderPane {

    public LibraryToolbarPane(JobScheduler jobScheduler, Library library, Localization localization) {

        Button addFeedButton = new Button(localization.addFeed(), new FontAwesomeIconView(FontAwesomeIcon.PLUS));
        addFeedButton.setOnAction(new AddFeedAction(library, localization));
        ButtonBar.setButtonData(addFeedButton, ButtonData.LEFT);

        Button refreshFeedsButton = new Button(localization.refreshFeeds(), new FontAwesomeIconView(FontAwesomeIcon.REFRESH));
        refreshFeedsButton.setOnAction(new FeedRefreshEventHandler(() -> library.getFeeds(), () -> {}, jobScheduler, library, localization));
        ButtonBar.setButtonData(refreshFeedsButton, ButtonData.LEFT);

        ButtonBar buttonBar = new ButtonBar();
        buttonBar.getButtons().addAll(addFeedButton, refreshFeedsButton);

        this.setLeft(buttonBar);

    }

}
