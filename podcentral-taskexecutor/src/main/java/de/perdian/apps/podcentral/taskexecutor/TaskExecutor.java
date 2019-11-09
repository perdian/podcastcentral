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

import java.time.Clock;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TaskExecutor {

    private static final Logger log = LoggerFactory.getLogger(TaskExecutor.class);

    private Clock clock = Clock.systemDefaultZone();
    private ExecutorService executorService = null;
    private Queue<Task> scheduledTasks = null;
    private List<Task> activeTasks = null;
    private List<TaskListener> taskListeners = null;
    private int processorCount = 1;

    public TaskExecutor(int processorCount) {
        this.setExecutorService(Executors.newCachedThreadPool());
        this.setScheduledTasks(new PriorityQueue<>(10, new Task.PriorityComparator()));
        this.setActiveTasks(new ArrayList<>());
        this.setTaskListeners(new CopyOnWriteArrayList<>());
        this.setProcessorCount(processorCount);
    }

    public Task submitTask(TaskRequest taskRequest) {
        if (taskRequest == null) {
            throw new NullPointerException("Parameter 'taskRequest' must not be null!");
        } else if (taskRequest.getTitle() == null) {
            throw new NullPointerException("Property 'title' of task request must not be null!");
        } else if (taskRequest.getRunnable() == null) {
            throw new NullPointerException("Property 'runnable' of task request must not be null!");
        } else {

            log.info("Accepting job: {}", taskRequest);
            Task task = new Task(this);
            task.setRequest(taskRequest);
            task.setSubmitTime(this.getClock().instant());

            synchronized (this) {
                if (!this.executeTask(task, false)) {
                    this.getScheduledTasks().add(task);
                    this.getTaskListeners().forEach(l -> l.onTaskScheduled(task));
                }
            }
            return task;

        }
    }

    synchronized boolean executeTask(Task task, boolean ignoreSlots) {
        if (ignoreSlots || this.getProcessorCount() > this.getActiveTasks().size()) {

            // Make sure we remove the operation from the waiting list, so no matter from where we
            // come we always leave the waiting queue in a consistent state
            this.getScheduledTasks().remove(task);

            if (!this.getActiveTasks().contains(task)) {
                task.setStartTime(this.getClock().instant());
                task.setStatus(TaskStatus.ACTIVE);
                this.getActiveTasks().add(task);
                this.getExecutorService().submit(() -> this.startTask(task));
            }
            return true;

        } else {
            return false;
        }
    }

    private void startTask(Task task) {
        try {

            log.debug("Starting task: {}", task);
            this.getTaskListeners().forEach(l -> l.onTaskStarted(task));

            TaskProgress taskProgress = new TaskProgress(task.getRequest().getProgressListeners(), task::getStatus);
            TaskRunnable taskRunnable = task.getRequest().getRunnable();
            taskRunnable.run(taskProgress);

            task.setEndTime(this.getClock().instant());
            log.info("Task completed: {} in {}", task, Duration.between(task.getStartTime(), task.getEndTime()));

        } catch (Exception e) {

            task.setEndTime(this.getClock().instant());
            task.setException(e);
            log.info("Exception occured during job execution: " + task, e);

        } finally {
            if (!TaskStatus.CANCELLED.equals(task.getStatus())) {
                task.setStatus(TaskStatus.COMPLETED);
            }
            try {
                synchronized (this) {

                    // Make sure the operation is removed from the list of currently active operations
                    this.getActiveTasks().remove(task);

                    // After the current processor is finished we want to make sure that the next
                    // item in the queue get's picked up
                    this.checkWaitingRequests();

                }
            } finally {
                this.getTaskListeners().forEach(l -> l.onTaskCompleted(task));
            }
        }

    }

    private synchronized void checkWaitingRequests() {
        Queue<Task> scheduledTasks = this.getScheduledTasks();
        int maxTasksToRemove = this.getProcessorCount() - this.getActiveTasks().size();
        if (maxTasksToRemove > 0) {
            List<Task> removedTasks = new ArrayList<>(maxTasksToRemove);
            for (int i = 0; i < maxTasksToRemove && !scheduledTasks.isEmpty(); i++) {
                removedTasks.add(scheduledTasks.remove());
            }
            for (Task task : removedTasks) {
                this.executeTask(task, false);
            }
        }
    }

    synchronized boolean cancelTask(Task task, String reason) {
        if (TaskStatus.CANCELLED.equals(task.getStatus())) {
            return true;
        } else if (this.getScheduledTasks().remove(task) || this.getActiveTasks().remove(task)) {
            log.debug("Cancelling task {} with reason: {}", task, reason);
            task.setStatus(TaskStatus.CANCELLED);
            task.setCancelTime(this.getClock().instant());
            task.setCancelReason(reason);
            this.getTaskListeners().forEach(l -> l.onTaskCancelled(task));
            this.checkWaitingRequests();
            return true;
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

    private Queue<Task> getScheduledTasks() {
        return this.scheduledTasks;
    }
    private void setScheduledTasks(Queue<Task> scheduledTasks) {
        this.scheduledTasks = scheduledTasks;
    }

    private List<Task> getActiveTasks() {
        return this.activeTasks;
    }
    private void setActiveTasks(List<Task> activeTasks) {
        this.activeTasks = activeTasks;
    }

    public boolean addTaskListener(TaskListener listener) {
        return this.getTaskListeners().add(listener);
    }
    public boolean removeTaskListener(TaskListener listener) {
        return this.getTaskListeners().remove(listener);
    }
    private List<TaskListener> getTaskListeners() {
        return this.taskListeners;
    }
    private void setTaskListeners(List<TaskListener> taskListeners) {
        this.taskListeners = taskListeners;
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
            this.getTaskListeners().forEach(l -> l.onProcessorCountUpdated(processorCount));
        }
    }

}
