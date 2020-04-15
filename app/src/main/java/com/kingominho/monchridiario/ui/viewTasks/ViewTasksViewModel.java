package com.kingominho.monchridiario.ui.viewTasks;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.firestore.DocumentSnapshot;
import com.kingominho.monchridiario.adapters.TaskAdapter;
import com.kingominho.monchridiario.manager.TaskManager;
import com.kingominho.monchridiario.models.Task;

public class ViewTasksViewModel extends ViewModel {

    private TaskManager taskManager;
    private TaskAdapter taskAdapter;
    private MutableLiveData<Boolean> isTaskListEmpty;

    private MutableLiveData<Boolean> confirmDelete;

    public ViewTasksViewModel() {
        taskManager = TaskManager.getInstance();
        isTaskListEmpty = new MutableLiveData<>();
        isTaskListEmpty.setValue(true);
        confirmDelete = new MutableLiveData<>();
        confirmDelete.setValue(false);
    }

    public void setTaskAdapter(String categoryId, boolean isFinished) {
        this.taskAdapter = new TaskAdapter(taskManager.getAllTasksOptions(categoryId, isFinished));
    }

    public TaskManager getTaskManager() {
        return taskManager;
    }

    public void setIsTaskListEmpty(boolean value) {
        isTaskListEmpty.setValue(value);
    }

    public LiveData<Boolean> getIsTaskListEmpty() {
        return isTaskListEmpty;
    }

    public TaskAdapter getTaskAdapter() {
        if(taskAdapter == null) {
            throw new RuntimeException("Task Adapter is not initialised. Initialise Adapter by calling" +
                    "setTaskAdapter().");
        }
        return taskAdapter;
    }
}
