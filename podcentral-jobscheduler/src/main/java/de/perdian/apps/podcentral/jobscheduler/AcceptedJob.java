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

import java.time.Instant;
import java.util.Comparator;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class AcceptedJob {

    private JobScheduler owner = null;
    private ActiveJob activeJob = null;
    private Job job = null;
    private Instant scheduledTime = null;

    AcceptedJob(JobScheduler owner) {
        this.setOwner(owner);
    }

    @Override
    public String toString() {
        ToStringBuilder toStringBuilder = new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE);
        toStringBuilder.append("job", this.getJob());
        return toStringBuilder.toString();
    }

    static class PriorityComparator implements Comparator<AcceptedJob> {

        @Override
        public int compare(AcceptedJob o1, AcceptedJob o2) {
            return Integer.compare(o1.getJob().getPriority(), o2.getJob().getPriority());
        }

    }

    public void forceStart() {
        this.getOwner().executeJob(this, true);
    }

    public void cancel(String reason) {
        this.getOwner().cancelJob(this, reason);
    }

    private JobScheduler getOwner() {
        return this.owner;
    }
    private void setOwner(JobScheduler owner) {
        this.owner = owner;
    }

    public ActiveJob getActiveJob() {
        return this.activeJob;
    }
    void setActiveJob(ActiveJob activeJob) {
        this.activeJob = activeJob;
    }

    public Job getJob() {
        return this.job;
    }
    void setJob(Job job) {
        this.job = job;
    }

    public Instant getScheduledTime() {
        return this.scheduledTime;
    }
    void setScheduledTime(Instant scheduledTime) {
        this.scheduledTime = scheduledTime;
    }

}
