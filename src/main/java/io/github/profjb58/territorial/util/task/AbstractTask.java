package io.github.profjb58.territorial.util.task;

import net.minecraft.util.Identifier;

public abstract class AbstractTask implements TaskInfo {

    protected final Identifier taskId;
    protected final Runnable taskRunnable, cancelRunnable;

    public AbstractTask(Identifier taskId, Runnable taskRunnable, Runnable cancelRunnable) {
        this.taskId = taskId;
        this.taskRunnable = taskRunnable;
        this.cancelRunnable = cancelRunnable;
    }

    public abstract boolean cancel(boolean failHard);
}
