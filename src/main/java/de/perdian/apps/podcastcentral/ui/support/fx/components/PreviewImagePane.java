/*
 * Copyright 2013-2018 Christian Seifert
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
package de.perdian.apps.podcastcentral.ui.support.fx.components;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.URI;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.perdian.apps.podcastcentral.ui.support.localization.Localization;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;

public class PreviewImagePane extends BorderPane {

    static final Logger log = LoggerFactory.getLogger(PreviewImagePane.class);
    static final ExecutorService IMAGE_LOADER_THREADPOOL = Executors.newFixedThreadPool(3);

    public PreviewImagePane(String imageUrl, int width, int height, Localization localization) {

        Label imageLabel = new Label(StringUtils.isEmpty(imageUrl) ? localization.noImage() : localization.loading());
        imageLabel.setAlignment(Pos.CENTER);
        imageLabel.setMinSize(width, height);
        imageLabel.setMaxSize(width, height);

        this.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
        this.setCenter(imageLabel);

        if (StringUtils.isNotEmpty(imageUrl)) {
            IMAGE_LOADER_THREADPOOL.execute(() -> {
                try (InputStream imageStream = new BufferedInputStream(URI.create(imageUrl).toURL().openStream())) {
                    Image previewImage = new Image(imageStream, width, height, true, true);
                    Platform.runLater(() -> {
                        imageLabel.setText(null);
                        imageLabel.setGraphic(new ImageView(previewImage));
                    });
                } catch (Exception e) {
                    log.warn("Cannot load preview image from URL: {}", imageUrl, e);
                }
            });
        }

    }

}
