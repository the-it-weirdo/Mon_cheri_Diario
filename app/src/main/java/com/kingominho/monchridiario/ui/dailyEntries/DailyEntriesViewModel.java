package com.kingominho.monchridiario.ui.dailyEntries;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class DailyEntriesViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public DailyEntriesViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is dailyEntries fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}