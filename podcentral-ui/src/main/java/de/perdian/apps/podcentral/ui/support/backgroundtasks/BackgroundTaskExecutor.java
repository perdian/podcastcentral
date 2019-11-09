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
package de.perdian.apps.podcentral.ui.support.backgroundtasks;

import de.perdian.apps.podcentral.preferences.Preferences;
import javafx.beans.value.ObservableDoubleValue;
import javafx.beans.value.ObservableStringValue;

public interface BackgroundTaskExecutor {

    static BackgroundTaskExecutor createInstance(Preferences preferences) {
        return new BackgroundTaskExecutorImpl();
    }

    void execute(String title, BackgroundTask task);

    default void execute(String title, Runnable runnable) {
        this.execute(title, progress -> runnable.run());
    }

    ObservableDoubleValue getProgress();
    ObservableStringValue getText();

}
