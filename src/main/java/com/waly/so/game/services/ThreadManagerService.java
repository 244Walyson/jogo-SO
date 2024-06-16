package com.waly.so.game.services;

import com.waly.so.game.models.Task;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ThreadManagerService {

    private List<Task> taskList;
    private int taskIdCounter;

    public ThreadManagerService() {
        this.taskList = new ArrayList<>();
        this.taskIdCounter = 1;
    }

    public List<Task> getTaskList() {
        return taskList;
    }

    public void addTask() {

    }

    public void clearTasks() {
        taskList.clear();
        taskIdCounter = 1;
    }
}
