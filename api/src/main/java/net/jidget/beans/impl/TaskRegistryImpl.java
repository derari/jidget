package net.jidget.beans.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;
import net.jidget.beans.TaskRegistry;

/**
 *
 * @author Arian Treffer
 */
public class TaskRegistryImpl implements TaskRegistry {
    
    private final Map<Runnable, Task> tasks = new HashMap<>();
    private final int defaultInterval;
    private ScheduledExecutorService scheduler;
    private boolean active = true;

    public TaskRegistryImpl(int defaultInterval) {
        this.defaultInterval = defaultInterval;
    }

    @Override
    public void addTask(Runnable r, int interval) {
        addTask(r, 0, interval);
    }

    @Override
    public synchronized void addTask(Runnable r, int delay, int interval) {
        if (!active) return;
        stopTask(r);
        Task t = new Task(r, delay, interval);
        tasks.put(r, t);
        if (scheduler != null) {
            scheduleTask(t);
        }
    }
    
    @Override
    public synchronized void stopTask(Runnable r) {
        Task t = tasks.remove(r);
        if (t != null) t.cancel();
    }
    
    public synchronized void setScheduler(ScheduledExecutorService scheduler) {
        assert this.scheduler == null : "Scheduler can be set only once";
        this.scheduler = scheduler;
        for (Task t: tasks.values()) {
            scheduleTask(t);
        }
    }
    
    public synchronized void shutDown() {
        active = false;
        for (Task t: tasks.values()) {
            t.cancel();
        }
        tasks.clear();
    }

    private void scheduleTask(Task t) {
        int i = t.interval > 0 ? t.interval : defaultInterval;
        t.scheduled = scheduler.scheduleAtFixedRate(t.r, t.delay, i, TimeUnit.MILLISECONDS);
    }
    
    private static class Task {
        public final Runnable r;
        public final int delay;
        public final int interval;
        public ScheduledFuture<?> scheduled;

        public Task(Runnable r, int delay, int interval) {
            this.r = r;
            this.delay = delay;
            this.interval = interval;
        }
        
        public void cancel() {
            if (scheduled != null) {
                scheduled.cancel(true);
            }
        }
        
    }
    
}
