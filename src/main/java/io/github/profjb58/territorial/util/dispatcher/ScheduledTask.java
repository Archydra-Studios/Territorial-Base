package io.github.profjb58.territorial.util.dispatcher;

import javax.annotation.Nullable;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class ScheduledTask {

    final TaskType taskType;
    final long duration;
    final TimeUnit timeUnit;
    final Runnable success, fail;

    @Nullable
    ScheduledFuture<?> future = null;

    public ScheduledTask(TaskType taskType, long duration, TimeUnit timeUnit, Runnable success, Runnable fail) {
        this.taskType = taskType;
        this.duration = duration;
        this.timeUnit = timeUnit;
        this.success = success;
        this.fail = fail;
    }

    void setFuture(ScheduledFuture<?> future) {
        this.future = future;
    }

    boolean isDone() {
        return this.future != null && future.isDone();
    }
}
