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
package de.perdian.apps.podcastcentral.ui.modules.library.components.feeds;

import org.apache.commons.lang3.StringUtils;

import de.perdian.apps.podcastcentral.model.Feed;
import de.perdian.apps.podcastcentral.ui.support.localization.Localization;
import de.perdian.apps.podcastcentral.ui.support.text.TextHelper;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

public class FeedDetailsPane extends BorderPane {

    public FeedDetailsPane(Feed feed, Localization localization) {

        Label titleLabel = new Label(localization.title());
        TextField titleField = new TextField(feed.getTitle().getValue());
        titleField.setFocusTraversable(false);
        titleField.setEditable(false);
        GridPane.setHgrow(titleField, Priority.ALWAYS);

        Label descriptionLabel = new Label(localization.description());
        TextArea descriptionArea = new TextArea(TextHelper.stripHtml(feed.getDescription().getValue()));
        descriptionArea.setFocusTraversable(false);
        descriptionArea.setWrapText(true);
        descriptionArea.setEditable(false);
        descriptionArea.setMinHeight(150);
        descriptionArea.setMaxHeight(150);
        GridPane.setHgrow(descriptionArea, Priority.ALWAYS);
        GridPane.setMargin(descriptionLabel, new Insets(8, 0, 0, 0));

        GridPane detailsPane = new GridPane();
        detailsPane.add(titleLabel, 0, 0, 1, 1);
        detailsPane.add(titleField, 0, 1, 1, 1);
        detailsPane.add(descriptionLabel, 0, 3, 1, 1);
        detailsPane.add(descriptionArea, 0, 4, 1, 1);
        detailsPane.setHgap(8);
        detailsPane.setVgap(2);

        BorderPane detailsWrapperPane = new BorderPane(detailsPane);

        if (StringUtils.isNotEmpty(feed.getImageUrl().getValue())) {
            Image image = new Image(feed.getImageUrl().getValue(), 200, 200, true, true);
            if (image.getHeight() > 0 && image.getWidth() > 0) {
                BorderPane imageWrapperPane = new BorderPane();
                imageWrapperPane.setTop(new ImageView(image));
                imageWrapperPane.setPadding(new Insets(0, 0, 0, 12));
                detailsWrapperPane.setRight(imageWrapperPane);
            }
        }

        TitledPane detailsTitledPane = new TitledPane(localization.feed(), detailsWrapperPane);
        detailsTitledPane.setMaxHeight(Double.MAX_VALUE);
        detailsTitledPane.setCollapsible(false);
        this.setCenter(detailsTitledPane);

    }

}
