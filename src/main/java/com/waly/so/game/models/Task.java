package com.waly.so.game.models;

import com.waly.so.game.services.FleuryAlgorithm;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.concurrent.atomic.AtomicLong;

@Data
public class Task {

    private String taskId;
    private String username;
    private String sessionId;
    private Thread thread;
    private Boolean ready;
    private AtomicLong startTime = new AtomicLong(0);
    private AtomicLong endTime = new AtomicLong(0);
    private int diffTime;

    public Task(String taskId, String username, String sessionId, Thread thread) {
        this.taskId = taskId;
        this.sessionId = sessionId;
        this.thread = thread;
        this.username = username;
        ready = false;
    }

    public void startTask() throws IllegalThreadStateException {
        startTime.set(System.currentTimeMillis());
        thread.start();
       try {
           FleuryAlgorithm fleuryAlgorithm = FleuryAlgorithm.generateCircularEulerianGraph(1000);
           fleuryAlgorithm.executeFleury();
           endTask();
       } catch (Exception e) {
           e.printStackTrace();
       }
    }

    public void endTask() {
        endTime.set(System.currentTimeMillis());
        AtomicLong diff = new AtomicLong(endTime.get() - startTime.get());
        diffTime = diff.intValue();
        ready = true;
    }
}
