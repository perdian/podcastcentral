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
package de.perdian.apps.podcentral.ui.modules.episodes;

import java.util.List;
import java.util.function.Supplier;

import de.perdian.apps.podcentral.jobscheduler.Job;
import de.perdian.apps.podcentral.jobscheduler.JobScheduler;
import de.perdian.apps.podcentral.model.Episode;
import de.perdian.apps.podcentral.ui.localization.Localization;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;

public class DownloadEpisodesActionEventHandler implements EventHandler<ActionEvent> {

    private Supplier<List<Episode>> episodesSupplier = null;
    private JobScheduler uiJobScheduler = null;
    private JobScheduler downloadJobScheduler = null;
    private Localization localization = null;

    public DownloadEpisodesActionEventHandler(Supplier<List<Episode>> episodesSupplier, JobScheduler uiJobScheduler, JobScheduler downloadJobScheduler, Localization localization) {
        this.setEpisodesSupplier(episodesSupplier);
        this.setUiJobScheduler(uiJobScheduler);
        this.setDownloadJobScheduler(downloadJobScheduler);
        this.setLocalization(localization);
    }

    @Override
    public void handle(ActionEvent event) {
        List<Episode> episodes = this.getEpisodesSupplier().get();
        if (!episodes.isEmpty()) {
            this.getUiJobScheduler().submitJob(new Job(this.getLocalization().schedulingEpisodeDownloads(), progress -> {

                throw new UnsupportedOperationException();

            }));
        }
    }

    private Supplier<List<Episode>> getEpisodesSupplier() {
        return this.episodesSupplier;
    }
    private void setEpisodesSupplier(Supplier<List<Episode>> episodesSupplier) {
        this.episodesSupplier = episodesSupplier;
    }

    private JobScheduler getUiJobScheduler() {
        return this.uiJobScheduler;
    }
    private void setUiJobScheduler(JobScheduler uiJobScheduler) {
        this.uiJobScheduler = uiJobScheduler;
    }

    private JobScheduler getDownloadJobScheduler() {
        return this.downloadJobScheduler;
    }
    private void setDownloadJobScheduler(JobScheduler downloadJobScheduler) {
        this.downloadJobScheduler = downloadJobScheduler;
    }

    private Localization getLocalization() {
        return this.localization;
    }
    private void setLocalization(Localization localization) {
        this.localization = localization;
    }

}
