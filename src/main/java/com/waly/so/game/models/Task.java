package com.waly.so.game.models;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.concurrent.atomic.AtomicLong;

@Data
public class Task {

    private String taskId;
    private String username;
    private String sessionId;
    private Thread t1;
    private Boolean ready;
    private AtomicLong startTime = new AtomicLong(0);
    private AtomicLong endTime = new AtomicLong(0);
    private int diffTime;

    public Task(String taskId, String username, String sessionId, Thread t1) {
        this.taskId = taskId;
        this.sessionId = sessionId;
        this.t1 = t1;
        this.username = username;
        ready = false;
    }

    public void startTask() throws IllegalThreadStateException {
        startTime.set(System.currentTimeMillis());
        t1.start();
       try {
           for (int i = 0; i < 500; i++) {
               Thread.sleep(5);
           }
           endTask();
       } catch (InterruptedException e) {
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
