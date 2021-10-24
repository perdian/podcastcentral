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
package de.perdian.apps.podcastcentral.ui.modules.library.components.treetable;

import java.io.File;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import de.perdian.apps.podcastcentral.model.Episode;
import de.perdian.apps.podcastcentral.model.EpisodeDownloadState;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;

class LibraryTreeTableDragMouseEventHandler implements EventHandler<MouseEvent> {

    private Supplier<List<Episode>> episodesSupplier = null;

    public LibraryTreeTableDragMouseEventHandler(Supplier<List<Episode>> episodesSupplier) {
        this.setEpisodesSupplier(episodesSupplier);
    }

    @Override
    public void handle(MouseEvent event) {

        List<File> completedFiles = this.getEpisodesSupplier().get().stream()
            .filter(episode -> EpisodeDownloadState.COMPLETED.equals(episode.getDownloadState().getValue()))
            .map(episode -> episode.getContentFile().getValue())
            .filter(file -> file != null && file.exists())
            .collect(Collectors.toList());

        if (!completedFiles.isEmpty()) {
            Dragboard dragBoard = ((Node)event.getSource()).startDragAndDrop(TransferMode.COPY);
            ClipboardContent clipboardContent = new ClipboardContent();
            clipboardContent.putFiles(completedFiles);
            dragBoard.setContent(clipboardContent);
            event.consume();
        }

    }

    private Supplier<List<Episode>> getEpisodesSupplier() {
        return this.episodesSupplier;
    }
    private void setEpisodesSupplier(Supplier<List<Episode>> episodesSupplier) {
        this.episodesSupplier = episodesSupplier;
    }

}
