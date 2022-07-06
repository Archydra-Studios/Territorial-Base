package io.github.profjb58.territorial.util.task;

import io.github.profjb58.territorial.exception.ScheduleException;
import net.minecraft.util.Identifier;

import javax.annotation.Nullable;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;

public class AsyncTask extends AbstractTask {

    @Nullable
    protected Future<?> future;

    public AsyncTask(Identifier taskId, Runnable taskRunnable, Runnable cancelRunnable) {
        super(taskId, taskRunnable, cancelRunnable);
    }

    public void schedule(@Nullable ScheduledExecutorService scheduler) {
        if(scheduler != null)
            future = scheduler.submit(taskRunnable);
    }

    @Override
    public boolean cancel(boolean failHard) {
        if(!failHard)
            cancelRunnable.run();
        if(future != null)
            return future.cancel(true);
        return false;
    }

    @Override
    public boolean isActive() throws ScheduleException {
        if(future != null)
            return !future.isDone();
        else
            throw new ScheduleException();
    }

    @Override
    public boolean isAsync() {
        return true;
    }
}
