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
package de.perdian.apps.podcastcentral.ui.support.errors;

import org.apache.commons.lang3.exception.ExceptionUtils;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Dialog;
import javafx.scene.control.TextArea;
import javafx.scene.text.Font;

public class ExceptionDialogBuilder {

    private String title = null;
    private Throwable exception = null;

    public ExceptionDialogBuilder() {
    }

    public Dialog<?> createDialog() {
        TextArea textarea = new TextArea(ExceptionUtils.getStackTrace(this.getException()));
        textarea.setEditable(false);
        textarea.setFont(Font.font("Monospace"));
        Alert alert = new Alert(AlertType.ERROR);
        alert.getDialogPane().getScene().getStylesheets().add("META-INF/stylesheets/podcastcentral.css");
        alert.getDialogPane().setContent(textarea);
        alert.setHeaderText(this.getTitle());
        alert.setTitle("Error");
        alert.setHeaderText(this.getTitle());
        return alert;
    }

    public ExceptionDialogBuilder withTitle(String title) {
        this.setTitle(title);
        return this;
    }

    public ExceptionDialogBuilder withException(Throwable exception) {
        this.setException(exception);
        return this;
    }

    String getTitle() {
        return this.title;
    }
    private void setTitle(String title) {
        this.title = title;
    }

    Throwable getException() {
        return this.exception;
    }
    private void setException(Throwable exception) {
        this.exception = exception;
    }

}
