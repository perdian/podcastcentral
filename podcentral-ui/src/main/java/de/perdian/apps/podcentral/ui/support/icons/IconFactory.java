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
package de.perdian.apps.podcentral.ui.support.icons;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

/**
 * Retreive icons from the FontAwesome icon library
 *
 * @author Christian Seifert
 */

public class IconFactory {

//    private static final Font REGULAR_FONT = Font.loadFont(IconFactory.class.getClassLoader().getResourceAsStream("META-INF/fonts/font-awesome/Font Awesome 5 Free-Regular-400.otf"), 12);

    public static Button createButton(String iconName) {
        Button button = new Button();
        try {
//            Font.loadFont(new BufferedInputStream(new FileInputStream("/Users/perdian/Downloads/FontAwesome.ttf")), 20);
            button.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.VENUS_MARS, "20px"));
//            button.setFont();
//            button.setText("X: \uf023");
//            button.setFont(REGULAR_FONT);
//            button.setStyle("-fx-font-size: 12px; -fx-font-family: FontAwesome; -icons-color: rgb(61,114,144)");
//            button.setStyle("-fx-font-size: 12px;");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return button;
    }

    public static Label createLabel(String iconName) {
        Label label = new Label();
//        label.setFont(REGULAR_FONT);
        label.setStyle("-fx-background-color: red; -fx-font-size: 12px");
        return label;
    }

}
