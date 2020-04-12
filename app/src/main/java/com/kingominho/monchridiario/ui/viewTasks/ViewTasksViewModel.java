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

    public ViewTasksViewModel() {
        taskManager = TaskManager.getInstance();
        isTaskListEmpty = new MutableLiveData<>();
        isTaskListEmpty.setValue(true);
    }

    public void setTaskAdapter(String categoryId, boolean isFinished) {
        this.taskAdapter = new TaskAdapter(taskManager.getAllTasksOptions(categoryId, isFinished));

        this.taskAdapter.setTaskInteractionListener(new TaskAdapter.OnTaskItemInteractionListener() {
            @Override
            public void onDeleteClick(DocumentSnapshot documentSnapshot, int position) {
                taskManager.deleteTask(documentSnapshot.getReference());
            }

            @Override
            public void onCheckedChange(DocumentSnapshot documentSnapshot, int position, boolean isChecked) {
                Task task = documentSnapshot.toObject(Task.class);
                task.setFinished(isChecked);
                taskManager.updateTask(documentSnapshot.getReference(), task);
            }

            @Override
            public void onDataChanged() {
                isTaskListEmpty.setValue(taskAdapter.getItemCount() == 0);
            }
        });
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
