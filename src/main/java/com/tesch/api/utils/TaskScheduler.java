package com.tesch.api.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class TaskScheduler {
    
    private ScheduledExecutorService scheduler;
    private List<ScheduledFuture<?>> scheduledTasks;

    public TaskScheduler() {
        this.scheduler = Executors.newScheduledThreadPool(1);
        this.scheduledTasks = new ArrayList<>();
    }

    public List<ScheduledFuture<?>> getScheduledTasks() {
        return this.scheduledTasks;
    }

    public ScheduledFuture<?> schedule(Runnable runnable, Integer time) {
        return this.scheduler.schedule(runnable, time, TimeUnit.SECONDS);
    }

    public void cancelTask(ScheduledFuture<?> task) {
        task.cancel(true);
    }

    public void cancelAll() {
        this.scheduledTasks.forEach(this::cancelTask);
    }
}
