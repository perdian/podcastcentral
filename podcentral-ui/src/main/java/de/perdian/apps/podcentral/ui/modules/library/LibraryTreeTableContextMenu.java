/*
 * Copyright 2019 Christian Seifert
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.perdian.apps.podcentral.ui.modules.library;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import de.perdian.apps.podcentral.jobscheduler.JobScheduler;
import de.perdian.apps.podcentral.model.Episode;
import de.perdian.apps.podcentral.model.Feed;
import de.perdian.apps.podcentral.model.FeedInputOptions;
import de.perdian.apps.podcentral.model.Library;
import de.perdian.apps.podcentral.ui.localization.Localization;
import de.perdian.apps.podcentral.ui.modules.feeds.FeedDeleteEventHandler;
import de.perdian.apps.podcentral.ui.modules.feeds.FeedRefreshEventHandler;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;

class LibraryTreeTableContextMenu extends ContextMenu {

    LibraryTreeTableContextMenu(List<Feed> selectedFeeds, Map<Feed, List<Episode>> selectedEpisodes, Runnable clearSelectionCallback, JobScheduler jobScheduler, Library library, Localization localization) {

        List<String> feedUrls = selectedFeeds.stream().map(feed -> feed.getUrl().getValue()).collect(Collectors.toList());
        MenuItem refreshMenuItem = new MenuItem(localization.refresh(), new FontAwesomeIconView(FontAwesomeIcon.REFRESH));
        if (feedUrls.isEmpty()) {
            refreshMenuItem.setDisable(true);
        } else {
            refreshMenuItem.setOnAction(new FeedRefreshEventHandler(() -> feedUrls, clearSelectionCallback, jobScheduler, library, localization));
        }
        this.getItems().add(refreshMenuItem);
        MenuItem refreshRestoreEpisodesMenuItem = new MenuItem(localization.refreshRestoreDeletedEpisodes(), new FontAwesomeIconView(FontAwesomeIcon.REFRESH));
        if (feedUrls.isEmpty()) {
            refreshRestoreEpisodesMenuItem.setDisable(true);
        } else {
            FeedInputOptions feedInputOptions = new FeedInputOptions();
            feedInputOptions.setResetDeletedEpisodes(true);
            FeedRefreshEventHandler eventHandler = new FeedRefreshEventHandler(() -> feedUrls, clearSelectionCallback, jobScheduler, library, localization);
            eventHandler.setFeedInputOptions(feedInputOptions);
            refreshRestoreEpisodesMenuItem.setOnAction(eventHandler);
        }
        this.getItems().add(refreshRestoreEpisodesMenuItem);

        this.getItems().add(new SeparatorMenuItem());

        MenuItem deleteMenuItem = new MenuItem(localization.delete(), new FontAwesomeIconView(FontAwesomeIcon.REMOVE));
        if (selectedFeeds.isEmpty() && selectedEpisodes.isEmpty()) {
            deleteMenuItem.setDisable(true);
        } else {
            deleteMenuItem.setOnAction(new FeedDeleteEventHandler(selectedFeeds, selectedEpisodes, clearSelectionCallback, jobScheduler, library, localization));
        }
        this.getItems().add(deleteMenuItem);


    }

}
