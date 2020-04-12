package com.kingominho.monchridiario.manager;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.kingominho.monchridiario.models.ProfilePicture;

public class AccountManager {

    private final static String TAG = "AccountManager: ";

    public static final int TASK_ID_CHANGE_PROFILE_PICTURE = 1;
    public static final int TASK_ID_REAUTHENTICATE_USER = 2;
    public static final int TASK_ID_DELETE_ACCOUNT = 3;
    public static final int TASK_ID_CHANGE_PASSWORD = 4;

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
                                                            accountManagerTaskListener.OnTaskSuccessful(task,
                                                                    TASK_ID_CHANGE_PROFILE_PICTURE);
                                                        } else {
                                                            accountManagerTaskListener.OnTaskNotSuccessful(task,
                                                                    TASK_ID_CHANGE_PROFILE_PICTURE);
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

    public void changePassword(String currentPassword, final String newPassword) {
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), currentPassword);

        user.reauthenticate(credential)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            user.updatePassword(newPassword)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                accountManagerTaskListener.OnTaskSuccessful(task,
                                                        TASK_ID_CHANGE_PASSWORD);
                                            } else {
                                                accountManagerTaskListener.OnTaskNotSuccessful(task,
                                                        TASK_ID_CHANGE_PASSWORD);
                                            }
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            accountManagerTaskListener.OnFailure(e);
                                        }
                                    });
                        } else {
                            accountManagerTaskListener.OnTaskNotSuccessful(task, TASK_ID_REAUTHENTICATE_USER);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        accountManagerTaskListener.OnFailure(e);
                    }
                });
    }


    public void setAccountManagerTaskListener(AccountManagerTaskListener accountManagerTaskListener) {
        this.accountManagerTaskListener = accountManagerTaskListener;
    }

    //TODO: Make this a transaction
    public void deleteAccount(FirebaseUser user) {
        final String uid = user.getUid();
        final String url = user.getPhotoUrl().toString();

        CategoryManager.getInstance().deleteAllCategories(uid);
        DailyEntryManager.getInstance().deleteAllDailyEntry(uid);
        deleteProfilePicture(url);

        FirebaseAuth.getInstance().getCurrentUser().delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()) {
                    Log.d(TAG, "onComplete: Task successful. Account deleted.");
                }
                else {
                    Log.e(TAG, "onComplete: Task failed.", task.getException());
                    if(FirebaseAuth.getInstance().getCurrentUser()!=null) {
                        FirebaseAuth.getInstance().signOut();
                    }
                }
            }
        });
    }

    public void reAuthenticateUser(String email, String password) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        AuthCredential credential = EmailAuthProvider.getCredential(email, password);

        user.reauthenticate(credential)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            accountManagerTaskListener.OnTaskSuccessful(task, TASK_ID_REAUTHENTICATE_USER);
                        } else {
                            accountManagerTaskListener.OnTaskNotSuccessful(task, TASK_ID_REAUTHENTICATE_USER);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        accountManagerTaskListener.OnFailure(e);
                    }
                });
    }

    public interface AccountManagerTaskListener {
        void OnFailure(Exception e);

        void OnSuccess(String message);

        void OnComplete(Task task);

        void OnTaskNotSuccessful(Task task, int task_id);

        void OnTaskSuccessful(Task task, int task_id);
    }
}

