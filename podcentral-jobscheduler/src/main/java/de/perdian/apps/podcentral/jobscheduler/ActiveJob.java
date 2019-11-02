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

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class ActiveJob {

    private JobScheduler owner = null;
    private AcceptedJob acceptedJob = null;
    private Instant startTime = null;
    private Instant endTime = null;
    private Instant cancelTime = null;
    private String cancelReason = null;
    private JobStatus status = null;
    private Exception exception = null;

    ActiveJob(JobScheduler owner) {
        this.setOwner(owner);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.NO_CLASS_NAME_STYLE);
    }

    public void cancel(String reason) {
        this.getOwner().cancelJob(this, reason);
    }

    JobScheduler getOwner() {
        return this.owner;
    }
    private void setOwner(JobScheduler owner) {
        this.owner = owner;
    }

    public AcceptedJob getAcceptedJob() {
        return this.acceptedJob;
    }
    void setAcceptedJob(AcceptedJob acceptedJob) {
        this.acceptedJob = acceptedJob;
    }

    public Instant getStartTime() {
        return this.startTime;
    }
    void setStartTime(Instant startTime) {
        this.startTime = startTime;
    }

    public Instant getEndTime() {
        return this.endTime;
    }
    void setEndTime(Instant endTime) {
        this.endTime = endTime;
    }

    public Instant getCancelTime() {
        return this.cancelTime;
    }
    void setCancelTime(Instant cancelTime) {
        this.cancelTime = cancelTime;
    }

    public String getCancelReason() {
        return this.cancelReason;
    }
    void setCancelReason(String cancelReason) {
        this.cancelReason = cancelReason;
    }

    public JobStatus getStatus() {
        return this.status;
    }
    void setStatus(JobStatus status) {
        this.status = status;
    }

    public Exception getException() {
        return this.exception;
    }
    void setException(Exception exception) {
        this.exception = exception;
    }

}
