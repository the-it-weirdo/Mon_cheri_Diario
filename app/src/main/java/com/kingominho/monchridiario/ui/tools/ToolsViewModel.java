package com.kingominho.monchridiario.ui.tools;

import android.net.Uri;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ToolsViewModel extends ViewModel {

    //private MutableLiveData<String> mText;

    private Uri mProfilePicUrl;
    private String mDisplayName;
    private String mEmail;


    public ToolsViewModel() {
        //mText = new MutableLiveData<>();
        //mText.setValue("This is tools fragment");

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        mProfilePicUrl = currentUser.getPhotoUrl();
        mDisplayName = currentUser.getDisplayName();
        mEmail = currentUser.getEmail();
    }

    /*public LiveData<String> getText() {
        return mText;
    }*/

    public Uri getmProfilePicUrl() {
        return mProfilePicUrl;
    }

    public String getmDisplayName() {
        return mDisplayName;
    }

    public String getmEmail() {
        return mEmail;
    }
}