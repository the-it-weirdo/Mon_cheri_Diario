package com.kingominho.monchridiario;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class AccountManager {

    private final static String TAG = "AccountManager: ";

    private static AccountManager mInstance;

    AccountManagerTaskListener accountManagerTaskListener;

    static final String PROFILE_PHOTO_UPLOAD_PATH = "profilePictures";

    private FirebaseStorage mStorage;
    private DatabaseReference mDatabaseReference;


    public static AccountManager getInstance() {
        if (mInstance == null) {
            mInstance = new AccountManager();
        }
        return mInstance;
    }

    public void setProfilePicture(Uri photoUri) {
        final FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
        mStorage = FirebaseStorage.getInstance();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference(AccountManager.PROFILE_PHOTO_UPLOAD_PATH);
        final StorageReference fileReference = mStorage.getReference(AccountManager.PROFILE_PHOTO_UPLOAD_PATH)
                .child(mUser.getUid());

        fileReference.putFile(photoUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Log.d(TAG, "onSuccess: Photo uploaded to storage.");
                        fileReference.getDownloadUrl()
                                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        Log.d(TAG, "onSuccess: Download URL obtained.");
                                        ProfilePicture upload = new ProfilePicture(mUser.getUid(),
                                                uri.toString());
                                        mDatabaseReference.child(mUser.getUid()).setValue(upload);

                                        UserProfileChangeRequest userProfileChangeRequest = new UserProfileChangeRequest.Builder()
                                                .setPhotoUri(uri).build();
                                        mUser.updateProfile(userProfileChangeRequest)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            accountManagerTaskListener.OnSuccessfulProfilePictureSet();
                                                        } else {
                                                            accountManagerTaskListener.OnTaskNotSuccessful(task);
                                                        }
                                                    }
                                                });
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        accountManagerTaskListener.OnFailure(e);
                                    }
                                });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        accountManagerTaskListener.OnFailure(e);
                    }
                });
    }

    public void deleteProfilePicture(String downloadURL) {
        mStorage = FirebaseStorage.getInstance();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference(AccountManager.PROFILE_PHOTO_UPLOAD_PATH);
        mStorage.getReferenceFromUrl(downloadURL)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "onSuccess: File Deleted from Storage.");
                        mDatabaseReference.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).removeValue()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        accountManagerTaskListener.OnSuccess("Profile Picture Deleted.");
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        accountManagerTaskListener.OnFailure(e);
                                    }
                                });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        accountManagerTaskListener.OnFailure(e);
                    }
                });
    }

    public void updateProfilePicture(String oldURL, Uri newUri) {
        deleteProfilePicture(oldURL);
        setProfilePicture(newUri);
    }


    public void setAccountManagerTaskListener(AccountManagerTaskListener accountManagerTaskListener) {
        this.accountManagerTaskListener = accountManagerTaskListener;
    }

    public interface AccountManagerTaskListener {
        void OnFailure(Exception e);

        void OnSuccess(String message);

        void OnComplete(Task task);

        void OnTaskNotSuccessful(Task task);

        void OnSuccessfulProfilePictureSet();
    }
}

