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

import java.util.List;
import java.util.function.Supplier;

import de.perdian.apps.podcastcentral.model.Episode;
import de.perdian.apps.podcastcentral.model.Feed;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;

public class MarkReadEpisodesActionEventHandler implements EventHandler<ActionEvent> {

    private Supplier<List<Feed>> feedsSupplier = null;
    private Supplier<List<Episode>> episodesSupplier = null;
    private Boolean newState = null;

    public MarkReadEpisodesActionEventHandler(Supplier<List<Feed>> feedsSupplier, Supplier<List<Episode>> episodesSupplier, Boolean newState) {
        this.setFeedsSupplier(feedsSupplier);
        this.setEpisodesSupplier(episodesSupplier);
        this.setNewState(newState);
    }

    @Override
    public void handle(ActionEvent event) {
        this.getFeedsSupplier().get().stream().flatMap(feed -> feed.getEpisodes().stream()).forEach(episode -> episode.getRead().setValue(this.getNewState()));
        this.getEpisodesSupplier().get().stream().forEach(episode -> episode.getRead().setValue(this.getNewState()));
    }

    private Supplier<List<Feed>> getFeedsSupplier() {
        return this.feedsSupplier;
    }
    private void setFeedsSupplier(Supplier<List<Feed>> feedsSupplier) {
        this.feedsSupplier = feedsSupplier;
    }

    private Supplier<List<Episode>> getEpisodesSupplier() {
        return this.episodesSupplier;
    }
    private void setEpisodesSupplier(Supplier<List<Episode>> episodesSupplier) {
        this.episodesSupplier = episodesSupplier;
    }

    private Boolean getNewState() {
        return this.newState;
    }
    private void setNewState(Boolean newState) {
        this.newState = newState;
    }

}
