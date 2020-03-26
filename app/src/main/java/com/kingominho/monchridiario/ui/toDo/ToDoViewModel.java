package com.kingominho.monchridiario.ui.toDo;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ToDoViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public ToDoViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is toDo fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}