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
package de.perdian.apps.podcentral.ui.modules.feeds.input;

import de.perdian.apps.podcentral.core.model.FeedData;
import de.perdian.apps.podcentral.ui.localization.Localization;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;

public class FeedDataPane extends GridPane {

    public FeedDataPane(FeedData feedData, Localization localization) {

        TextField titleField = new TextField(feedData.getTitle());
        titleField.setEditable(false);
        GridPane.setHgrow(titleField, Priority.ALWAYS);
        GridPane.setMargin(titleField, new Insets(2, 0, 8, 0));

        TextField ownerField = new TextField(feedData.getOwner());
        ownerField.setEditable(false);
        GridPane.setHgrow(ownerField, Priority.ALWAYS);
        GridPane.setMargin(ownerField, new Insets(2, 0, 8, 0));

        TextArea descriptionArea = new TextArea(feedData.getDescription());
        descriptionArea.setWrapText(true);
        descriptionArea.setPrefHeight(75);
        descriptionArea.setEditable(false);
        GridPane.setHgrow(descriptionArea, Priority.ALWAYS);
        GridPane.setVgrow(descriptionArea, Priority.ALWAYS);
        GridPane.setMargin(descriptionArea, new Insets(2, 0, 0, 0));

        Image image = new Image(feedData.getImageUrl(), 150, 150, true, true);
        Label imageLabel = new Label();
        imageLabel.setBorder(new Border(new BorderStroke(Color.LIGHTGRAY, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
        imageLabel.setGraphic(image.getWidth() > 0 && image.getHeight() > 0 ? new ImageView(image) : null);
        imageLabel.setText(image.getWidth() > 0 && image.getHeight() > 0 ? null : " ");
        GridPane.setMargin(imageLabel, new Insets(0, 0, 0, 12));
        GridPane.setValignment(imageLabel, VPos.TOP);
        GridPane.setVgrow(imageLabel, Priority.ALWAYS);

        this.add(new Label(localization.title()), 0, 0, 1, 1);
        this.add(titleField, 0, 1, 1, 1);
        this.add(new Label(localization.owner()), 0, 2, 1, 1);
        this.add(ownerField, 0, 3, 1, 1);
        this.add(new Label(localization.description()), 0, 4, 1, 1);
        this.add(descriptionArea, 0, 5, 1, 1);
        this.add(imageLabel, 1, 0, 1, 6);

    }

}
