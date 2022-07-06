package io.github.profjb58.territorial.exception;

public class ScheduleException extends Exception {

    public ScheduleException() {
        super("Task has not been scheduled");
    }

    public ScheduleException(String errorMessage) {
        super(errorMessage);
    }
}
