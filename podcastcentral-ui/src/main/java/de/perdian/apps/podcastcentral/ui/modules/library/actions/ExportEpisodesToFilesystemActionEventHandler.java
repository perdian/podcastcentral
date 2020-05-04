/*
 * Copyright 2020 Christian Seifert
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
package de.perdian.apps.podcastcentral.ui.modules.library.actions;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.function.Supplier;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.perdian.apps.podcastcentral.model.Episode;
import de.perdian.apps.podcastcentral.preferences.Preferences;
import de.perdian.apps.podcastcentral.ui.support.errors.ExceptionDialogBuilder;
import de.perdian.apps.podcastcentral.ui.support.localization.Localization;
import javafx.application.Platform;
import javafx.beans.property.Property;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.DirectoryChooser;
import javafx.stage.Window;

public class ExportEpisodesToFilesystemActionEventHandler implements EventHandler<ActionEvent> {

    private static final Logger log = LoggerFactory.getLogger(ExportEpisodesToFilesystemActionEventHandler.class);

    private Supplier<Window> ownerSupplier = null;
    private Supplier<List<Episode>> episodesSupplier = null;
    private Property<String> targetDirectoryValueProperty = null;
    private Localization localization = null;

    public ExportEpisodesToFilesystemActionEventHandler(Supplier<Window> ownerSupplier, Supplier<List<Episode>> episodesSupplier, Preferences preferences, Localization localization) {
        this.setOwnerSupplier(ownerSupplier);
        this.setEpisodesSupplier(episodesSupplier);
        this.setTargetDirectoryValueProperty(preferences.getStringProperty(this.getClass().getName() + ".targetDirectoryValue", null));
        this.setLocalization(localization);
    }

    @Override
    public void handle(ActionEvent event) {
        List<Episode> episodes = this.getEpisodesSupplier().get();
        if (!episodes.isEmpty()) {
            String targetDirectoryValue = this.getTargetDirectoryValueProperty().getValue();
            File targetDirectory = StringUtils.isEmpty(targetDirectoryValue) ? null : new File(targetDirectoryValue);
            DirectoryChooser directoryChooser = new DirectoryChooser();
            directoryChooser.setInitialDirectory(targetDirectory != null && targetDirectory.exists() ? targetDirectory : new File(System.getProperty("user.home")));
            directoryChooser.setTitle(this.getLocalization().selectTargetDirectory());
            File directory = directoryChooser.showDialog(this.getOwnerSupplier().get());
            if (directory != null) {
                try {
                    new Thread(() -> {
                        try {
                            this.exportEpisodesToDirectory(episodes, directory);
                            Platform.runLater(() -> {
                                Alert informationAlert = new Alert(AlertType.INFORMATION);
                                informationAlert.initOwner(this.getOwnerSupplier().get());
                                informationAlert.setTitle(this.getLocalization().exportToFilesystem());
                                informationAlert.setHeaderText(null);
                                informationAlert.setContentText(this.getLocalization().exportToFilesystemCompleted());
                                informationAlert.getDialogPane().getScene().getStylesheets().add("META-INF/stylesheets/podcastcentral.css");
                                informationAlert.show();
                            });
                        } catch (Exception e) {
                            Platform.runLater(() -> {
                                new ExceptionDialogBuilder()
                                .withTitle(this.getLocalization().cannotExportEpisodesToFilesystem())
                                .withException(e)
                                .createDialog()
                                .show();
                            });
                        }
                    }).start();
                } finally {
                    this.getTargetDirectoryValueProperty().setValue(directory.getAbsolutePath());
                }
            }
        }
    }

    private void exportEpisodesToDirectory(List<Episode> episodes, File targetDirectory) throws Exception {
        if (!targetDirectory.exists()) {
            log.debug("Creating file system export target directory at: {}", targetDirectory.getAbsolutePath());
            targetDirectory.mkdirs();
        }
        for (Episode episode : episodes) {
            File sourceFile = episode.getContentFile().get();
            File targetFile = new File(targetDirectory, sourceFile.getName());
            log.debug("Creating file system export file at: {}", targetFile.getAbsolutePath());
            Files.copy(sourceFile.toPath(), targetFile.toPath(), StandardCopyOption.COPY_ATTRIBUTES, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    private Supplier<Window> getOwnerSupplier() {
        return this.ownerSupplier;
    }
    private void setOwnerSupplier(Supplier<Window> ownerSupplier) {
        this.ownerSupplier = ownerSupplier;
    }

    private Supplier<List<Episode>> getEpisodesSupplier() {
        return this.episodesSupplier;
    }
    private void setEpisodesSupplier(Supplier<List<Episode>> episodesSupplier) {
        this.episodesSupplier = episodesSupplier;
    }

    private Property<String> getTargetDirectoryValueProperty() {
        return this.targetDirectoryValueProperty;
    }
    private void setTargetDirectoryValueProperty(Property<String> targetDirectoryValueProperty) {
        this.targetDirectoryValueProperty = targetDirectoryValueProperty;
    }

    private Localization getLocalization() {
        return this.localization;
    }
    private void setLocalization(Localization localization) {
        this.localization = localization;
    }

}
