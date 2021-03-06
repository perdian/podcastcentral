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

import java.util.function.Supplier;

import de.perdian.apps.podcastcentral.model.Episode;
import de.perdian.apps.podcastcentral.ui.modules.library.components.feeds.EpisodeDetailsDialog;
import de.perdian.apps.podcastcentral.ui.support.localization.Localization;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;

public class ShowEpisodeDetailsActionEventHandler implements EventHandler<ActionEvent> {

    private Supplier<Episode> episodeSupplier = null;
    private Localization localization = null;

    public ShowEpisodeDetailsActionEventHandler(Supplier<Episode> episodeSupplier, Localization localization) {
        this.setEpisodeSupplier(episodeSupplier);
        this.setLocalization(localization);
    }

    @Override
    public void handle(ActionEvent event) {
        Episode episode = this.getEpisodeSupplier().get();
        if (episode != null) {
            EpisodeDetailsDialog.showEpisodeDetails(episode, true, this.getLocalization());
        }
    }

    private Supplier<Episode> getEpisodeSupplier() {
        return this.episodeSupplier;
    }
    private void setEpisodeSupplier(Supplier<Episode> episodeSupplier) {
        this.episodeSupplier = episodeSupplier;
    }

    private Localization getLocalization() {
        return this.localization;
    }
    private void setLocalization(Localization localization) {
        this.localization = localization;
    }

}
