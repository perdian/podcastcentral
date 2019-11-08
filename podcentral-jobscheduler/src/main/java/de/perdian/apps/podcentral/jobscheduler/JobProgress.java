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
package de.perdian.apps.podcentral.jobscheduler;

import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

public class JobProgress {

    private List<JobProgressListener> progressListeners = null;
    private Supplier<JobStatus> statusSupplier = null;

    JobProgress(List<JobProgressListener> progressListeners, Supplier<JobStatus> statusSupplier) {
        this.setProgressListeners(progressListeners == null ? Collections.emptyList() : progressListeners);
        this.setStatusSupplier(statusSupplier);
    }

    public void updateProgress(Double progress, String message) {
        this.getProgressListeners().forEach(l -> l.onProgress(progress, message));
    }

    private List<JobProgressListener> getProgressListeners() {
        return this.progressListeners;
    }
    private void setProgressListeners(List<JobProgressListener> progressListeners) {
        this.progressListeners = progressListeners;
    }

    public JobStatus getStatus() {
        return this.getStatusSupplier().get();
    }
    private Supplier<JobStatus> getStatusSupplier() {
        return this.statusSupplier;
    }
    private void setStatusSupplier(Supplier<JobStatus> statusSupplier) {
        this.statusSupplier = statusSupplier;
    }

}
