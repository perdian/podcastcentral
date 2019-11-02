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

import java.time.Clock;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JobScheduler {

    private static final Logger log = LoggerFactory.getLogger(JobScheduler.class);

    private Clock clock = Clock.systemDefaultZone();
    private ExecutorService executorService = null;
    private Queue<AcceptedJob> acceptedJobs = null;
    private List<ActiveJob> activeJobs = null;
    private List<JobListener> jobListeners = null;
    private List<JobSchedulerListener> jobSchedulerListeners = null;
    private int processorCount = 1;

    public JobScheduler(int processorCount) {
        this.setExecutorService(Executors.newCachedThreadPool());
        this.setJobListeners(new CopyOnWriteArrayList<>());
        this.setJobSchedulerListeners(new CopyOnWriteArrayList<>());
        this.setScheduledJobs(new PriorityQueue<>(10));
        this.setActiveJobs(new ArrayList<>());
        this.setProcessorCount(1);
    }

    public synchronized boolean isBusy() {
        return !this.getScheduledJobs().isEmpty() || !this.getActiveJobs().isEmpty();
    }

    public AcceptedJob submitJob(Job job) {
        if (job == null) {
            throw new NullPointerException("Parameter 'job' must not be null!");
        } else if (job.getTitle() == null) {
            throw new NullPointerException("Property 'title' of job must not be null!");
        } else if (job.getRunnable() == null) {
            throw new NullPointerException("Property 'runnable' of job must not be null!");
        } else {

            log.info("Accepting job: {}", job);
            AcceptedJob acceptedJob = new AcceptedJob(this);
            acceptedJob.setJob(job);
            acceptedJob.setScheduledTime(this.getClock().instant());

            synchronized (this) {
                if (!this.executeJob(acceptedJob, false)) {
                    this.getScheduledJobs().add(acceptedJob);
                    this.getJobListeners().forEach(l -> l.onJobAccepted(acceptedJob));
                }
            }
            return acceptedJob;

        }
    }

    synchronized boolean executeJob(AcceptedJob job, boolean ignoreSlots) {
        if (ignoreSlots || this.getProcessorCount() > this.getActiveJobs().size()) {

            // Make sure we remove the operation from the waiting list, so no matter from where we
            // come we always leave the waiting queue in a consistent state
            this.getScheduledJobs().remove(job);

            boolean jobAlreadyActive = this.getActiveJobs().stream()
                .filter(activeJob -> Objects.equals(activeJob.getAcceptedJob(), job))
                .findAny()
                .isPresent();

            if (!jobAlreadyActive) {

                ActiveJob activeJob = new ActiveJob(this);
                activeJob.setStartTime(this.getClock().instant());
                activeJob.setStatus(JobStatus.ACTIVE);
                activeJob.setAcceptedJob(job);
                job.setActiveJob(activeJob);

                this.getActiveJobs().add(activeJob);
                this.getExecutorService().submit(() -> this.startJob(activeJob));

            }
            return true;

        } else {
            return false;
        }
    }

    private void startJob(ActiveJob activeJob) {
        try {

            log.debug("Starting job: {}", activeJob);
            this.getJobListeners().forEach(l -> l.onJobStarting(activeJob));
            this.startJobCallable(activeJob);

            activeJob.setEndTime(this.getClock().instant());
            activeJob.setStatus(JobStatus.COMPLETED);
            log.info("Job completed: {} in {}", activeJob, Duration.between(activeJob.getStartTime(), activeJob.getEndTime()));

        } catch (Exception e) {

            activeJob.setEndTime(this.getClock().instant());
            activeJob.setException(e);
            activeJob.setStatus(JobStatus.COMPLETED);
            log.info("Exception occured during job execution: " + activeJob, e);

        } finally {
            try {
                synchronized (this) {

                    // Make sure the operation is removed from the list of currently active operations
                    this.getActiveJobs().remove(activeJob);

                    // After the current processor is finished we want to make sure that the next
                    // item in the queue get's picked up
                    this.checkWaitingRequests();

                }
            } finally {
                this.getJobListeners().forEach(l -> l.onJobCompleted(activeJob));
            }
        }

    }

    private void startJobCallable(ActiveJob activeJob) throws Exception {
        activeJob.getAcceptedJob().getJob().getRunnable().run(new JobProgress(activeJob.getAcceptedJob().getJob().getProgressListeners()));
    }

    private synchronized void checkWaitingRequests() {
        Queue<AcceptedJob> queue = this.getScheduledJobs();
        int maxJobsToRemove = this.getProcessorCount() - this.getActiveJobs().size();
        if (maxJobsToRemove > 0) {
            List<AcceptedJob> removedJobs = new ArrayList<>(maxJobsToRemove);
            for (int i = 0; i < maxJobsToRemove && !queue.isEmpty(); i++) {
                removedJobs.add(queue.remove());
            }
            for (AcceptedJob acceptedJob : removedJobs) {
                this.executeJob(acceptedJob, false);
            }
        }
    }

    synchronized boolean cancelJob(ActiveJob activeJob, String reason) {
        if (JobStatus.CANCELLED.equals(activeJob.getStatus())) {
            return true;
        } else if (!this.getActiveJobs().contains(activeJob)) {
            return false;
        } else {

            log.debug("Cancelling job {} with reason: {}", activeJob, reason);
            activeJob.setStatus(JobStatus.CANCELLED);
            activeJob.setCancelTime(this.getClock().instant());
            activeJob.setCancelReason(reason);
            this.getJobListeners().forEach(l -> l.onJobCancelled(activeJob));
            this.checkWaitingRequests();
            return true;

        }
    }

    synchronized boolean cancelJob(AcceptedJob job, String reason) {
        if (this.getScheduledJobs().remove(job)) {
            this.getJobListeners().forEach(l -> l.onJobCancelled(job));
            return true;
        } else if (job.getActiveJob() != null) {
            return this.cancelJob(job.getActiveJob(), reason);
        } else {
            return false;
        }
    }

    Clock getClock() {
        return this.clock;
    }
    void setClock(Clock clock) {
        this.clock = clock;
    }

    private ExecutorService getExecutorService() {
        return this.executorService;
    }
    private void setExecutorService(ExecutorService executorService) {
        this.executorService = executorService;
    }

    private Queue<AcceptedJob> getScheduledJobs() {
        return this.acceptedJobs;
    }
    private void setScheduledJobs(Queue<AcceptedJob> acceptedJobs) {
        this.acceptedJobs = acceptedJobs;
    }

    private List<ActiveJob> getActiveJobs() {
        return this.activeJobs;
    }
    private void setActiveJobs(List<ActiveJob> activeJobs) {
        this.activeJobs = activeJobs;
    }

    public boolean addJobListener(JobListener listener) {
        return this.getJobListeners().add(listener);
    }
    public boolean removeJobListener(JobListener listener) {
        return this.getJobListeners().remove(listener);
    }
    private List<JobListener> getJobListeners() {
        return this.jobListeners;
    }
    private void setJobListeners(List<JobListener> jobListeners) {
        this.jobListeners = jobListeners;
    }

    public boolean addJobSchedulerListener(JobSchedulerListener listener) {
        return this.getJobSchedulerListeners().add(listener);
    }
    public boolean removeJobSchedulerListener(JobSchedulerListener listener) {
        return this.getJobSchedulerListeners().remove(listener);
    }
    private List<JobSchedulerListener> getJobSchedulerListeners() {
        return this.jobSchedulerListeners;
    }
    private void setJobSchedulerListeners(List<JobSchedulerListener> jobSchedulerListeners) {
        this.jobSchedulerListeners = jobSchedulerListeners;
    }

    public int getProcessorCount() {
        return this.processorCount;
    }
    public void setProcessorCount(int processorCount) {
        if (processorCount <= 0) {
            throw new IllegalArgumentException("Parameter 'processorCount' must be larger than 0");
        } else if (this.processorCount != processorCount) {
            int oldProcessorCount = this.processorCount;
            log.debug("Updating processor count from {} to {}", oldProcessorCount, processorCount);
            synchronized (this) {
                this.processorCount = processorCount;
                if (processorCount > oldProcessorCount) {
                    this.checkWaitingRequests();
                }
            }
            this.getJobSchedulerListeners().forEach(l -> l.onProcessorCountUpdated(processorCount));
        }
    }

}
