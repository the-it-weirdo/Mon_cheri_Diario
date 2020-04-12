package com.kingominho.monchridiario.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.kingominho.monchridiario.adapters.DailyEntryAdapter;
import com.kingominho.monchridiario.adapters.TaskAdapter;
import com.kingominho.monchridiario.manager.DailyEntryManager;
import com.kingominho.monchridiario.manager.TaskManager;
import com.kingominho.monchridiario.models.Task;

public class HomeViewModel extends ViewModel {


    private DailyEntryManager dailyEntryManager;
    private DailyEntryAdapter dailyEntryAdapter;

    private TaskManager taskManager;
    private TaskAdapter taskAdapter;

    private MutableLiveData<Boolean> isTaskListEmpty;

    public HomeViewModel() {
        isTaskListEmpty = new MutableLiveData<>();
        isTaskListEmpty.setValue(true);
        dailyEntryManager = DailyEntryManager.getInstance();
        dailyEntryAdapter = new DailyEntryAdapter(dailyEntryManager.getAllDailyEntriesOptions(
                FirebaseAuth.getInstance().getCurrentUser().getUid()));
        taskManager = TaskManager.getInstance();
        taskAdapter = new TaskAdapter(taskManager.getAllTaskOfUserOptions(
                FirebaseAuth.getInstance().getCurrentUser().getUid(), false));

        taskAdapter.setTaskInteractionListener(new TaskAdapter.OnTaskItemInteractionListener() {
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

    public DailyEntryManager getDailyEntryManager() {
        return dailyEntryManager;
    }


    public DailyEntryAdapter getDailyEntryAdapter() {
        return dailyEntryAdapter;
    }

    public TaskManager getTaskManager() {
        return taskManager;
    }

    public TaskAdapter getTaskAdapter() {
        return taskAdapter;
    }

}