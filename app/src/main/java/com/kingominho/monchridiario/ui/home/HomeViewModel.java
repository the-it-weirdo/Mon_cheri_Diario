package com.kingominho.monchridiario.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.kingominho.monchridiario.DailyEntry;
import com.kingominho.monchridiario.DailyEntryAdapter;
import com.kingominho.monchridiario.DailyEntryManager;
import com.kingominho.monchridiario.TaskAdapter;
import com.kingominho.monchridiario.TaskManager;

public class HomeViewModel extends ViewModel {

    private MutableLiveData<String> mText;


    private DailyEntryManager dailyEntryManager;
    private DailyEntryAdapter dailyEntryAdapter;

    private TaskManager taskManager;
    private TaskAdapter taskAdapter;

    public HomeViewModel() {
        //    mText = new MutableLiveData<>();
        //  mText.setValue("This is home fragment");

        dailyEntryManager = DailyEntryManager.getInstance();
        dailyEntryAdapter = new DailyEntryAdapter(dailyEntryManager.getAllDailyEntriesOptions(
                FirebaseAuth.getInstance().getCurrentUser().getUid()));
        taskManager = TaskManager.getInstance();
        taskAdapter = new TaskAdapter(taskManager.getAllTaskOfUserOptions(
                FirebaseAuth.getInstance().getCurrentUser().getUid(), false));
    }

    public LiveData<String> getText() {
        return mText;
    }

    public DailyEntryManager getDailyEntryManager() {
        return dailyEntryManager;
    }

    public void setDailyEntryManager(DailyEntryManager dailyEntryManager) {
        this.dailyEntryManager = dailyEntryManager;
    }

    public DailyEntryAdapter getDailyEntryAdapter() {
        return dailyEntryAdapter;
    }

    public void setDailyEntryAdapter(DailyEntryAdapter dailyEntryAdapter) {
        this.dailyEntryAdapter = dailyEntryAdapter;
    }

    public TaskManager getTaskManager() {
        return taskManager;
    }

    public void setTaskManager(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    public TaskAdapter getTaskAdapter() {
        return taskAdapter;
    }

    public void setTaskAdapter(TaskAdapter taskAdapter) {
        this.taskAdapter = taskAdapter;
    }
}