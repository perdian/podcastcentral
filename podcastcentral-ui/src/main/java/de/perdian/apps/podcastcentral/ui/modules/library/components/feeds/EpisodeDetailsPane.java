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
import java.util.Locale;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DurationFormatUtils;

import de.perdian.apps.podcastcentral.model.Episode;
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

public class EpisodeDetailsPane extends BorderPane {

    public EpisodeDetailsPane(Episode episode, Localization localization) {

        Label titleLabel = new Label(localization.title());
        TextField titleField = new TextField(episode.getTitle().getValue());
        titleField.setFocusTraversable(false);
        titleField.setEditable(false);
        GridPane.setHgrow(titleField, Priority.ALWAYS);

        Label descriptionLabel = new Label(localization.description());
        TextArea descriptionArea = new TextArea(TextHelper.stripHtml(episode.getDescription().getValue()));
        descriptionArea.setFocusTraversable(false);
        descriptionArea.setWrapText(true);
        descriptionArea.setEditable(false);
        descriptionArea.setMinHeight(150);
        descriptionArea.setMaxHeight(150);
        GridPane.setHgrow(descriptionArea, Priority.ALWAYS);
        GridPane.setMargin(descriptionLabel, new Insets(8, 0, 0, 0));

        Label durationLabel = new Label(localization.duration());
        TextField durationField = new TextField(episode.getDuration().getValue() == null ? localization.unknown() : DurationFormatUtils.formatDuration(episode.getDuration().getValue().toMillis(), "HH:mm:ss"));
        durationField.setFocusTraversable(false);
        durationField.setEditable(false);
        GridPane.setHgrow(durationField, Priority.ALWAYS);
        GridPane.setMargin(durationLabel, new Insets(8, 0, 0, 0));

        Label publicationDateLabel = new Label(localization.date());
        TextField publicationDateField = new TextField(episode.getPublicationDate().getValue() == null ? localization.unknown() : DateTimeFormatter.ofPattern("dd.MM.yyyy").withLocale(Locale.getDefault()).format(episode.getPublicationDate().getValue().atZone(ZoneId.systemDefault())));
        publicationDateField.setFocusTraversable(false);
        publicationDateField.setEditable(false);
        GridPane.setHgrow(publicationDateField, Priority.ALWAYS);
        GridPane.setMargin(publicationDateLabel, new Insets(8, 0, 0, 0));

        GridPane detailsPane = new GridPane();
        detailsPane.add(titleLabel, 0, 0, 2, 1);
        detailsPane.add(titleField, 0, 1, 2, 1);
        detailsPane.add(descriptionLabel, 0, 3, 2, 1);
        detailsPane.add(descriptionArea, 0, 4, 2, 1);
        detailsPane.add(durationLabel, 0, 5, 1, 1);
        detailsPane.add(durationField, 0, 6, 1, 1);
        detailsPane.add(publicationDateLabel, 1, 5, 1, 1);
        detailsPane.add(publicationDateField, 1, 6, 1, 1);
        detailsPane.setHgap(8);
        detailsPane.setVgap(2);

        BorderPane detailsWrapperPane = new BorderPane(detailsPane);

        String imageUrl = StringUtils.defaultIfEmpty(episode.getImageUrl().getValue(), episode.getFeed().getImageUrl().getValue());
        if (StringUtils.isNotEmpty(imageUrl)) {
            Image image = new Image(imageUrl, 200, 200, true, true);
            if (image.getHeight() > 0 && image.getWidth() > 0) {
                BorderPane imageWrapperPane = new BorderPane();
                imageWrapperPane.setTop(new ImageView(image));
                imageWrapperPane.setPadding(new Insets(0, 0, 0, 12));
                detailsWrapperPane.setRight(imageWrapperPane);
            }
        }

        TitledPane detailsTitledPane = new TitledPane(localization.episode(), detailsWrapperPane);
        detailsTitledPane.setMaxHeight(Double.MAX_VALUE);
        detailsTitledPane.setCollapsible(false);
        this.setCenter(detailsTitledPane);

    }

}
