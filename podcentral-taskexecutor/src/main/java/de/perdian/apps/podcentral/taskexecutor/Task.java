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
package de.perdian.apps.podcentral.taskexecutor;

import java.time.Instant;
import java.util.Comparator;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class Task {

    private TaskExecutor owner = null;
    private TaskRequest request = null;
    private TaskStatus status = null;
    private Instant submitTime = null;
    private Instant startTime = null;
    private Instant endTime = null;
    private Instant cancelTime = null;
    private String cancelReason = null;
    private Exception exception = null;

    Task(TaskExecutor owner) {
        this.setOwner(owner);
    }

    @Override
    public String toString() {
        ToStringBuilder toStringBuilder = new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE);
        toStringBuilder.append("request", this.getRequest());
        toStringBuilder.append("status", this.getStatus());
        return toStringBuilder.toString();
    }

    public void cancel(String reason) {
        this.getOwner().cancelTask(this, reason);
    }

    static class PriorityComparator implements Comparator<Task> {

        @Override
        public int compare(Task o1, Task o2) {
            return Integer.compare(o1.getRequest().getPriority(), o2.getRequest().getPriority());
        }

    }

    public void forceStart() {
        this.getOwner().executeTask(this, true);
    }

    private TaskExecutor getOwner() {
        return this.owner;
    }
    private void setOwner(TaskExecutor owner) {
        this.owner = owner;
    }

    public TaskRequest getRequest() {
        return this.request;
    }
    void setRequest(TaskRequest request) {
        this.request = request;
    }

    public TaskStatus getStatus() {
        return this.status;
    }
    void setStatus(TaskStatus status) {
        this.status = status;
    }

    public Instant getSubmitTime() {
        return this.submitTime;
    }
    void setSubmitTime(Instant submitTime) {
        this.submitTime = submitTime;
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

    public Exception getException() {
        return this.exception;
    }
    void setException(Exception exception) {
        this.exception = exception;
    }

}
