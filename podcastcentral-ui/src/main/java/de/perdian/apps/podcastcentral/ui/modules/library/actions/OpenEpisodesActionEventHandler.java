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
package de.perdian.apps.podcastcentral.ui.modules.library.actions;

import java.io.File;
import java.util.List;
import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.perdian.apps.podcastcentral.model.Episode;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;

public class OpenEpisodesActionEventHandler implements EventHandler<ActionEvent> {

    private static final Logger log = LoggerFactory.getLogger(OpenEpisodesActionEventHandler.class);

    private Supplier<List<Episode>> episodesSupplier = null;

    public OpenEpisodesActionEventHandler(Supplier<List<Episode>> episodesSupplier) {
        this.setEpisodesSupplier(episodesSupplier);
    }

    @Override
    public void handle(ActionEvent event) {
        List<Episode> selectedEpisodes = this.getEpisodesSupplier().get();
        if (selectedEpisodes.size() == 1) {
            Episode selectedEpisode = selectedEpisodes.get(0);
            File selectedFile = selectedEpisode.getContentFile().getValue();
            if (selectedFile != null && selectedFile.exists()) {
                try {
                    log.debug("Opening file: {}", selectedFile.getAbsolutePath());
                    java.awt.Desktop.getDesktop().open(selectedFile);
                } catch (Exception e) {
                    log.warn("Cannot open file: {}", selectedFile.getAbsolutePath(), e);
                }
            }
        }
    }

    private Supplier<List<Episode>> getEpisodesSupplier() {
        return this.episodesSupplier;
    }
    private void setEpisodesSupplier(Supplier<List<Episode>> episodesSupplier) {
        this.episodesSupplier = episodesSupplier;
    }

}
