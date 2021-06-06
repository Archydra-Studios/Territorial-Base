package io.github.profjb58.territorial.util.debug;

import java.util.HashMap;

public class DebugTimer {
    private final HashMap<String, Long> timePoints = new HashMap<>();

    long startTime, prevTimePoint;
    int timePointCounter;
    private final int numTimePoints;

    public DebugTimer(int numTimePoints) {
        startTime = System.nanoTime();
        prevTimePoint = startTime;
        this.numTimePoints = numTimePoints;
        timePointCounter = 0;
    }

    public void addTimePoint(String name) {
        timePoints.put(name, System.nanoTime() - prevTimePoint);
        prevTimePoint = System.nanoTime();
        timePointCounter++;

        if(timePointCounter == numTimePoints) {
            print();
        }
    }

    private void print() {
        System.out.println("====DEBUG TIMER====");
        System.out.println("Start time: " + (startTime / 100000) + "ms");
        System.out.println("[name] : [time difference]");
        timePoints.forEach((name, timeDiff) -> {
            System.out.println(name + " : " + (timeDiff / 100000) + "ms");
        });
    }
}
