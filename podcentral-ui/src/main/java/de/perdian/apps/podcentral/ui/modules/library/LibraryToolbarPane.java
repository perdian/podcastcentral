package de.perdian.apps.podcentral.ui.modules.library;

import java.util.Set;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import de.perdian.apps.podcentral.model.Library;
import de.perdian.apps.podcentral.ui.modules.feeds.components.add.AddFeedAction;
import de.perdian.apps.podcentral.ui.modules.library.actions.ExportLibraryAsOpmlActionEventHandler;
import de.perdian.apps.podcentral.ui.modules.library.actions.ImportFeedCollectionIntoLibraryActionEventHandler;
import de.perdian.apps.podcentral.ui.modules.library.actions.RefreshFeedsActionEventHandler;
import de.perdian.apps.podcentral.ui.support.backgroundtasks.BackgroundTaskExecutor;
import de.perdian.apps.podcentral.ui.support.localization.Localization;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.BorderPane;

public class LibraryToolbarPane extends BorderPane {

    public LibraryToolbarPane(Library library, BackgroundTaskExecutor backgroundTaskExecutor, Localization localization) {

        Button addFeedButton = new Button(localization.addFeed(), new FontAwesomeIconView(FontAwesomeIcon.PLUS));
        addFeedButton.setOnAction(new AddFeedAction(library, localization));
        ButtonBar.setButtonData(addFeedButton, ButtonData.LEFT);

        Button refreshFeedsButton = new Button(localization.refreshFeeds(), new FontAwesomeIconView(FontAwesomeIcon.REFRESH));
        refreshFeedsButton.setOnAction(new RefreshFeedsActionEventHandler(() -> library.getFeeds(), Set.of(), backgroundTaskExecutor, localization));
        ButtonBar.setButtonData(refreshFeedsButton, ButtonData.LEFT);

        Button importButton = new Button(localization.import_(), new FontAwesomeIconView(FontAwesomeIcon.FILE));
        importButton.setOnAction(new ImportFeedCollectionIntoLibraryActionEventHandler(this, library, backgroundTaskExecutor, localization));
        ButtonBar.setButtonData(importButton, ButtonData.RIGHT);

        MenuItem exportOpmlMenuItem = new MenuItem("as OPML");
        exportOpmlMenuItem.setOnAction(new ExportLibraryAsOpmlActionEventHandler(this, library, backgroundTaskExecutor, localization));
        MenuButton exportButton = new MenuButton(localization.export(), new FontAwesomeIconView(FontAwesomeIcon.FILE));
        exportButton.getItems().add(exportOpmlMenuItem);
        ButtonBar.setButtonData(exportButton, ButtonData.RIGHT);

        ButtonBar buttonBar = new ButtonBar();
        buttonBar.getButtons().addAll(addFeedButton, refreshFeedsButton, importButton, exportButton);

        this.setCenter(buttonBar);

    }

}
