package io.github.profjb58.territorial.util.task;

import io.github.profjb58.territorial.exception.ScheduleException;
import io.github.profjb58.territorial.util.TickCounter;
import net.minecraft.util.Identifier;

import java.time.Instant;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.concurrent.TimeUnit;

public class TickerTask extends AbstractTask {

    private static final EnumMap<TestFreq, HashSet<TickerTask>> TASK_FREQ_MAPPINGS = new EnumMap<>(TestFreq.class);
    private static final int FAST_TICKER_TICKS = 100;
    private static final TickCounter FAST_TICKER = new TickCounter(FAST_TICKER_TICKS);

    private final long timeInterval;
    private final TimeUnit timeUnit;
    private final TestFreq testFreq;
    private final boolean repeating;

    private boolean isTickerActive;
    private Instant lastRun;

    public TickerTask(Identifier id, long timeInterval, TimeUnit timeUnit, TestFreq testFreq, Runnable taskRunnable, Runnable cancelRunnable, boolean repeating) {
        super(id, taskRunnable, cancelRunnable);
        this.timeInterval = timeInterval;
        this.timeUnit = timeUnit;
        this.repeating = repeating;
        this.testFreq = testFreq;
        this.isTickerActive = false;

        if(!TASK_FREQ_MAPPINGS.containsKey(testFreq))
            TASK_FREQ_MAPPINGS.put(testFreq, new HashSet<>());
        TASK_FREQ_MAPPINGS.get(testFreq).add(this);
    }

    public void start() {
        isTickerActive = true;
    }

    public static void onTick() {
        if (FAST_TICKER.test()) {
            for (var taskMap : TASK_FREQ_MAPPINGS.keySet()) {
                if (taskMap.tickCounter.test()) {
                    var tasksSet = TASK_FREQ_MAPPINGS.get(taskMap);
                    if (tasksSet != null)
                        for (var task : tasksSet) {
                            if (task.isTickerActive) {
                                long instantEpochMillis = Instant.EPOCH.toEpochMilli();
                                long timeIntervalMillis = TimeUnit.MILLISECONDS.convert(task.timeInterval, task.timeUnit);

                                if (instantEpochMillis - task.lastRun.toEpochMilli() > timeIntervalMillis) {
                                    task.lastRun = Instant.now();
                                    task.taskRunnable.run();
                                    if(!task.repeating) task.finish();
                                }
                            }
                        }
                }
                taskMap.tickCounter.increment();
            }
        }
        FAST_TICKER.increment();
    }

    private boolean finish() {
        isTickerActive = false;
        if(TASK_FREQ_MAPPINGS.containsKey(testFreq))
            return TASK_FREQ_MAPPINGS.get(testFreq).remove(this);
        return false;
    }

    @Override
    public boolean cancel(boolean failHard) {
        if(!failHard && cancelRunnable != null)
            cancelRunnable.run();
        return finish();
    }

    @Override
    public boolean isActive() {
        return isTickerActive;
    }

    @Override
    public boolean isAsync() {
        return false;
    }

    /**
     * How frequently the server/client should check if a task has been completed (20 tps)
     */
    public enum TestFreq {
        FREQUENT(new TickCounter(FAST_TICKER_TICKS)),
        REGULAR(new TickCounter(FAST_TICKER_TICKS * 24)),
        INFREQUENT(new TickCounter(FAST_TICKER_TICKS * 240));

        private final TickCounter tickCounter;

        TestFreq(TickCounter tickCounter) {
            this.tickCounter = tickCounter;
        }
    }
}
