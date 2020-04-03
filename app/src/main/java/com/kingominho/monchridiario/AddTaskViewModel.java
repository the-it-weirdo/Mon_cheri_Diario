package com.kingominho.monchridiario;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;

public class AddTaskViewModel extends ViewModel {

    final String default_date_prompt = "Choose Date";
    final String default_time_prompt = "Choose Time";
    final String default_taskDescription = "";
    final int default_priority = 1;
    final Category defaultCategory = new Category("Choose category...", "");

    private MutableLiveData<String> taskDescription;
    private MutableLiveData<String> date;
    private MutableLiveData<String> time;
    private MutableLiveData<Integer> priority;
    private MutableLiveData<Category> category;
    private MutableLiveData<String> userName;

    public AddTaskViewModel () {
        taskDescription = new MutableLiveData<>();
        date = new MutableLiveData<>();
        time = new MutableLiveData<>();
        priority = new MutableLiveData<>();
        category = new MutableLiveData<>();
        userName = new MutableLiveData<>();
        userName.setValue(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());

        restoreDefaultValues();
    }

    public void restoreDefaultValues() {
        taskDescription.setValue(default_taskDescription);
        date.setValue(default_date_prompt);
        time.setValue(default_time_prompt);
        priority.setValue(default_priority);
        category.setValue(defaultCategory);
    }

    public MutableLiveData<String> getTaskDescription() {
        return taskDescription;
    }

    public MutableLiveData<String> getDate() {
        return date;
    }

    public MutableLiveData<String> getTime() {
        return time;
    }

    public MutableLiveData<Integer> getPriority() {
        return priority;
    }

    public MutableLiveData<Category> getCategory() {
        return category;
    }

    public MutableLiveData<String> getUserName() {
        return userName;
    }

    public void setCategory(Category category) {
        this.category.setValue(category);
    }
}
