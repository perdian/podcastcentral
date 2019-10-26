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
package de.perdian.apps.podcentral.ui.components.images;

import de.perdian.apps.podcentral.ui.localization.Localization;
import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class LoadImageIntoLabelRunnable implements Runnable {

    private String imageUrl = null;
    private int width = 0;
    private int height = 0;
    private Label targetLabel = null;
    private Localization localization = null;

    public LoadImageIntoLabelRunnable(String imageUrl, int width, int height, Label targetLabel, Localization localization) {
        this.setImageUrl(imageUrl);
        this.setTargetLabel(targetLabel);
        this.setLocalization(localization);
    }

    @Override
    public void run() {
        Image image = new Image(this.getImageUrl(), this.getWidth(), this.getHeight(), true, true);
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(this.getWidth());
        imageView.setFitHeight(this.getHeight());
        Platform.runLater(() -> {
            this.getTargetLabel().setText("");
            this.getTargetLabel().setGraphic(imageView);
        });
    }

    private String getImageUrl() {
        return this.imageUrl;
    }
    private void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    private int getWidth() {
        return this.width;
    }
    private void setWidth(int width) {
        this.width = width;
    }

    private int getHeight() {
        return this.height;
    }
    private void setHeight(int height) {
        this.height = height;
    }

    private Label getTargetLabel() {
        return this.targetLabel;
    }
    private void setTargetLabel(Label targetLabel) {
        this.targetLabel = targetLabel;
    }

    private Localization getLocalization() {
        return this.localization;
    }
    private void setLocalization(Localization localization) {
        this.localization = localization;
    }

}
