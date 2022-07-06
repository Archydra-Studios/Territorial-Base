package io.github.profjb58.territorial.util.task;

import io.github.profjb58.territorial.exception.ScheduleException;

public interface TaskInfo {

    boolean isActive() throws ScheduleException;

    boolean isAsync();
}
