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

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DurationFormatUtils;

import de.perdian.apps.podcastcentral.model.EpisodeData;
import de.perdian.apps.podcastcentral.model.FeedData;
import de.perdian.apps.podcastcentral.ui.support.localization.Localization;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
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
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

public class FeedDataDetailsPane extends GridPane {

    public FeedDataDetailsPane(FeedData feedData, List<EpisodeData> episodes, Localization localization) {

        Label titleLabel = new Label(localization.title());
        TextField titleField = new TextField(feedData.getTitle());
        titleField.setEditable(false);

        Label subtitleLabel = new Label(localization.subtitle());
        TextField subtitleField = new TextField(feedData.getSubtitle());
        subtitleField.setEditable(false);
        VBox.setMargin(subtitleLabel, new Insets(8, 0, 0, 0));

        Label ownerLabel = new Label(localization.owner());
        TextField ownerField = new TextField(feedData.getOwner());
        ownerField.setEditable(false);
        VBox.setMargin(ownerLabel, new Insets(8, 0, 0, 0));

        VBox leftBox = new VBox(2);
        leftBox.setMaxWidth(275);
        leftBox.getChildren().addAll(titleLabel, titleField);
        leftBox.getChildren().addAll(subtitleLabel, subtitleField);
        leftBox.getChildren().addAll(ownerLabel, ownerField);
        GridPane.setHgrow(leftBox, Priority.ALWAYS);
        GridPane.setVgrow(leftBox, Priority.ALWAYS);

        Image image = StringUtils.isEmpty(feedData.getImageUrl()) ? null : new Image(feedData.getImageUrl(), 275, 275, true, true);
        ImageView imageView = image == null || image.getHeight() <= 0 || image.getWidth() <= 0 ? null : new ImageView(image);
        if (imageView != null) {
            Label imageLabel = new Label("", imageView);
            imageLabel.setBorder(new Border(new BorderStroke(Color.LIGHTGRAY, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
            VBox.setMargin(imageLabel, new Insets(8, 0, 0, 0));
            Label separatorLabel = new Label("");
            separatorLabel.setMaxHeight(Double.MAX_VALUE);
            VBox.setVgrow(separatorLabel, Priority.ALWAYS);
            leftBox.getChildren().addAll(separatorLabel, imageLabel);
        }

        VBox rightBox = new VBox(2);
        GridPane.setVgrow(rightBox, Priority.ALWAYS);

        Label descriptionLabel = new Label(localization.description());
        TextArea descriptionArea = new TextArea(feedData.getDescription());
        descriptionArea.setWrapText(true);
        descriptionArea.setEditable(false);
        descriptionArea.setMaxHeight(100);
        VBox.setVgrow(descriptionArea, Priority.ALWAYS);
        rightBox.getChildren().addAll(descriptionLabel, descriptionArea);

        TableColumn<EpisodeData, String> dateColumn = new TableColumn<>(localization.date());
        dateColumn.setReorderable(false);
        dateColumn.setResizable(false);
        dateColumn.setMinWidth(85);
        dateColumn.setMaxWidth(85);
        dateColumn.setCellValueFactory(cell -> new ReadOnlyStringWrapper(cell.getValue().getPublicationDate() == null ? "" :  DateTimeFormatter.ofPattern("dd.MM.yyyy").format(cell.getValue().getPublicationDate().atZone(ZoneId.systemDefault()))));
        TableColumn<EpisodeData, String> durationColumn = new TableColumn<>(localization.duration());
        durationColumn.setReorderable(false);
        durationColumn.setResizable(false);
        durationColumn.setMinWidth(75);
        durationColumn.setMaxWidth(75);
        durationColumn.setCellValueFactory(cell -> new ReadOnlyStringWrapper(cell.getValue().getDuration() == null ? "" : DurationFormatUtils.formatDuration(cell.getValue().getDuration().toMillis(), "HH:mm:ss")));
        TableColumn<EpisodeData, String> titleColumn = new TableColumn<>(localization.title());
        titleColumn.setCellValueFactory(cell -> new ReadOnlyStringWrapper(cell.getValue().getTitle()));
        titleColumn.setMaxWidth(Double.MAX_VALUE);
        titleColumn.setReorderable(false);
        TableView<EpisodeData> episodeTable = new TableView<>(FXCollections.observableArrayList(episodes));
        episodeTable.getColumns().addAll(List.of(dateColumn, durationColumn, titleColumn));
        episodeTable.setEditable(false);
        episodeTable.setColumnResizePolicy(f -> false);
        episodeTable.setMinHeight(100);
        episodeTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        VBox.setVgrow(episodeTable, Priority.ALWAYS);
        VBox.setMargin(episodeTable, new Insets(8, 0, 0, 0));

        rightBox.getChildren().add(episodeTable);

        this.setHgap(8);
        this.add(leftBox, 0, 0, 1, 1);
        this.add(rightBox, 1, 0, 1, 1);

    }

}
