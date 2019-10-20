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
package de.perdian.apps.podcentral.ui.components.errors;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Dialog;

public class ExceptionDialogBuilder {

    private String title = null;
    private Throwable exception = null;

    public ExceptionDialogBuilder() {
    }

    public Dialog<?> createDialog() {
        return new Alert(AlertType.ERROR);
    }

    public ExceptionDialogBuilder withTitle(String title) {
        this.setTitle(title);
        return this;
    }

    public ExceptionDialogBuilder withException(Throwable exception) {
        this.setException(exception);
        return this;
    }

    private String getTitle() {
        return this.title;
    }
    private void setTitle(String title) {
        this.title = title;
    }

    private Throwable getException() {
        return this.exception;
    }
    private void setException(Throwable exception) {
        this.exception = exception;
    }

}
