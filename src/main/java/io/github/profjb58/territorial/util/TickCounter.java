package io.github.profjb58.territorial.util;

public class TickCounter {

    private final int TICK_THRESHOLD;
    private final int MAX_VALUE;

    private int counter = 0;
    private boolean enabled = true;

    public TickCounter(int tickThreshold) {
        this(tickThreshold, Integer.MAX_VALUE);
    }

    public TickCounter(int tickThreshold, int maxValue) {
        TICK_THRESHOLD = tickThreshold;
        MAX_VALUE = maxValue;
    }

    public boolean test() {
        if(counter >= TICK_THRESHOLD) {
            reset();
            return true;
        }
        return false;
    }

    public void increment() {
        if(enabled) {
            if(counter >= MAX_VALUE || counter < 0) reset();
            else counter++;
        }
    }

    public void reset() {
        counter = 0;
    }

    public boolean isEnabled() { return this.enabled; }

    public void disable() { this.enabled = false; }
    public void enable() { this.enabled = true; }
    public int value() { return this.counter; }
}
