package com.kingominho.monchridiario;

import androidx.lifecycle.ViewModel;

public class ViewTasksViewModel extends ViewModel {

    private TaskManager taskManager;
    private TaskAdapter taskAdapter;

    public ViewTasksViewModel() {
        taskManager = TaskManager.getInstance();
    }

    public void setTaskAdapter(String categoryId, boolean isFinished) {
        this.taskAdapter = new TaskAdapter(taskManager.getAllTasksOptions(categoryId, isFinished));
    }

    public TaskManager getTaskManager() {
        return taskManager;
    }

    public TaskAdapter getTaskAdapter() {
        if(taskAdapter == null) {
            throw new RuntimeException("Task Adapter is not initialised. Initialise Adapter by calling" +
                    "setTaskAdapter().");
        }
        return taskAdapter;
    }
}
