package com.kingominho.monchridiario.manager;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.kingominho.monchridiario.models.Category;
import com.kingominho.monchridiario.models.DailyEntry;
import com.kingominho.monchridiario.models.ProfilePicture;

import java.util.List;

public class AccountManager {

    private final static String TAG = "AccountManager: ";

    public static final int TASK_ID_CHANGE_PROFILE_PICTURE = 1;
    public static final int TASK_ID_REAUTHENTICATE_USER = 2;
    public static final int TASK_ID_DELETE_ACCOUNT = 3;
    public static final int TASK_ID_CHANGE_PASSWORD = 4;

    private static AccountManager mInstance;

    private AccountManagerTaskListener accountManagerTaskListener;

    public static final String PROFILE_PHOTO_UPLOAD_PATH = "profilePictures";

    private FirebaseStorage mStorage;
    private DatabaseReference mDatabaseReference;


    public static AccountManager getInstance() {
        if (mInstance == null) {
            mInstance = new AccountManager();
        }
        return mInstance;
    }

    public void setProfilePicture(byte[] imageData) {
        final FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
        mStorage = FirebaseStorage.getInstance();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference(AccountManager.PROFILE_PHOTO_UPLOAD_PATH);
        final StorageReference fileReference = mStorage.getReference(AccountManager.PROFILE_PHOTO_UPLOAD_PATH)
                .child(mUser.getUid());


        fileReference.putBytes(imageData)
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
        //setProfilePicture(newUri);
    }

    public void changePassword(String currentPassword, final String newPassword) {
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), currentPassword);

        user.reauthenticate(credential)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            accountManagerTaskListener.OnTaskSuccessful(task, TASK_ID_REAUTHENTICATE_USER);
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
                                            //accountManagerTaskListener.OnFailure(e);
                                            Log.e(TAG, "onFailure: ", e);
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

    private void deleteAccount() {
        FirebaseAuth.getInstance().getCurrentUser().delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "deleteAccount onComplete: Task successful. Account deleted.");
                    accountManagerTaskListener.OnTaskSuccessful(task, TASK_ID_DELETE_ACCOUNT);
                } else {
                    accountManagerTaskListener.OnTaskNotSuccessful(task, TASK_ID_DELETE_ACCOUNT);
                    Log.e(TAG, "deleteAccount onComplete: Task failed.", task.getException());
                    if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                        FirebaseAuth.getInstance().signOut();
                    }
                }
            }
        });
    }

    public void deleteAccount(final FirebaseUser user) {
        if (user == null) {
            Log.e(TAG, "deleteAccount: User is null.", new Exception("UserNullException"));
            return;
        }
        mStorage = FirebaseStorage.getInstance();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference(AccountManager.PROFILE_PHOTO_UPLOAD_PATH);
        final StorageReference profilePicStorageRef = mStorage.getReferenceFromUrl(user.getPhotoUrl().toString());
        final CollectionReference categoryCollectionRef = CategoryManager.getInstance().categoryCollectionRef;
        final CollectionReference taskCollectionReference = TaskManager.getInstance().taskCollectionRef;
        final CollectionReference dailyEntryCollectionReference = DailyEntryManager.getInstance().dailyEntryCollectionRef;

        final WriteBatch writeBatch = FirebaseFirestore.getInstance().batch();

        final Task<QuerySnapshot> deleteCategories = categoryCollectionRef.whereEqualTo(Category.KEY_USER_ID, user.getUid()).get();
        final Task<QuerySnapshot> deleteTasks = taskCollectionReference.whereEqualTo(com.kingominho.monchridiario.models.Task.KEY_USER_ID,
                user.getUid()).get();
        final Task<QuerySnapshot> deleteDailyEntries = dailyEntryCollectionReference.whereEqualTo(DailyEntry.USER_ID_KEY,
                user.getUid()).get();

        Tasks.whenAllComplete(deleteCategories, deleteTasks, deleteDailyEntries)
                .addOnSuccessListener(new OnSuccessListener<List<Task<?>>>() {
                    @Override
                    public void onSuccess(List<Task<?>> tasks) {
                        Log.d(TAG, "deleteAccount onSuccess: Categories, Tasks and Daily Entries collected.");
                        for (DocumentSnapshot documentSnapshot : deleteCategories.getResult().getDocuments()) {
                            writeBatch.delete(documentSnapshot.getReference());
                        }
                        for (DocumentSnapshot documentSnapshot : deleteTasks.getResult().getDocuments()) {
                            writeBatch.delete(documentSnapshot.getReference());
                        }
                        for (DocumentSnapshot documentSnapshot: deleteDailyEntries.getResult().getDocuments()) {
                            writeBatch.delete(documentSnapshot.getReference());
                        }
                        writeBatch.commit()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d(TAG, "deleteAccount onSuccess: Categories, Tasks and Daily Entries deleted.");
                                        Task deleteRealTimeDatabaseRef = mDatabaseReference.child(user.getUid()).removeValue();
                                        Task deleteImageFromStorage = profilePicStorageRef.delete();

                                        Tasks.whenAllComplete(deleteRealTimeDatabaseRef, deleteImageFromStorage)
                                                .addOnSuccessListener(new OnSuccessListener<List<Task<?>>>() {
                                                    @Override
                                                    public void onSuccess(List<Task<?>> tasks) {
                                                        mInstance.deleteAccount();
                                                        Log.d(TAG, "deleteAccount onSuccess: RealtimeDatabase and" +
                                                                "Storage Reference deleted.");
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Log.e(TAG, "deleteAccount onFailure: RealtimeDatabase and " +
                                                                "Storage reference not deleted.", e);
                                                    }
                                                });
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.e(TAG, "deleteAccount onFailure: Categories, Tasks and Daily Entries delete" +
                                                "failed.", e);
                                    }
                                });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "deleteAccount onFailure: Categories, Tasks and Daily Entries collection failed.", e);
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

