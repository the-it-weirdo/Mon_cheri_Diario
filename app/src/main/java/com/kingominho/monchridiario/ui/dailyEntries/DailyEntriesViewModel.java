package com.kingominho.monchridiario.ui.dailyEntries;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.kingominho.monchridiario.adapters.DailyEntryAdapter;
import com.kingominho.monchridiario.manager.DailyEntryManager;

public class DailyEntriesViewModel extends ViewModel {
    private final String TAG = "DailyEntriesViewModel";

    private DailyEntryAdapter dailyEntryAdapter;
    private DailyEntryManager dailyEntryManager;

    private MutableLiveData<Integer> position;

    public DailyEntriesViewModel() {
        //mText = new MutableLiveData<>();
        //mText.setValue("This is dailyEntries fragment");
        Log.d(TAG, "DailyEntriesViewModel: constructor called.");
        position = new MutableLiveData<>();
        position.setValue(0);
        dailyEntryManager = DailyEntryManager.getInstance();
        dailyEntryAdapter = new DailyEntryAdapter(dailyEntryManager.getAllDailyEntriesOptions(
                FirebaseAuth.getInstance().getCurrentUser().getUid()));
    }

    public DailyEntryAdapter getDailyEntryAdapter() {
        return dailyEntryAdapter;
    }

    public DailyEntryManager getDailyEntryManager() {
        return dailyEntryManager;
    }

    public MutableLiveData<Integer> getPosition() {
        return position;
    }

    /*public LiveData<String> getText() {
        return mText;
    }*/


}