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
package de.perdian.apps.podcentral.ui.modules.scheduler;

import de.perdian.apps.podcentral.jobscheduler.JobScheduler;
import de.perdian.apps.podcentral.ui.localization.Localization;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.BorderPane;

public class UiSchedulerPane extends BorderPane {

    public UiSchedulerPane(JobScheduler jobScheduler, Localization localization) {

        ProgressBar jobProgressBar = new ProgressBar(0);
        this.setLeft(jobProgressBar);

    }

}
