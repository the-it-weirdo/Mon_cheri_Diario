package com.kingominho.monchridiario;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.util.Date;
import java.util.logging.Handler;

public class TaskManager {

    private final static String TAG = "TaskManager: ";

    private final static String TASKS_COLLECTION_PATH = "tasks";

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference taskCollectionRef = db.collection(TASKS_COLLECTION_PATH);

    private static TaskManager taskManagerInstance;

    private TaskManager() {
        //private empty constructor
    }

    public static TaskManager getInstance() {
        if (taskManagerInstance == null) {
            taskManagerInstance = new TaskManager();
        }
        return taskManagerInstance;
    }

    public FirestoreRecyclerOptions<Task> getAllTasksOptions(String categoryId, boolean isFinished) {
        Query query = taskCollectionRef.whereEqualTo(Task.KEY_CATEGORY_ID, categoryId)
                .whereEqualTo(Task.KEY_FINISHED, isFinished)
                .orderBy(Task.KEY_FINISH_BY)
                .orderBy(Task.KEY_PRIORITY);
        query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                Log.d(TAG, "onSuccessTaskOptions: Query Returned: " + queryDocumentSnapshots.size() + " records.");
            }
        });
        FirestoreRecyclerOptions<Task> options = new FirestoreRecyclerOptions.Builder<Task>()
                .setQuery(query, Task.class)
                .build();
        Log.d(TAG, "getAllTasksOptions: " + options.getSnapshots().size());
        return options;
    }

    public Query getTaskCountQuery(String categoryId, boolean isFinished) {
        Query query = taskCollectionRef.whereEqualTo(Task.KEY_CATEGORY_ID, categoryId)
                .whereEqualTo(Task.KEY_FINISHED, isFinished);

        return query;
    }

    public void deleteAllTask(String categoryId) {
        Query query = taskCollectionRef.whereEqualTo(Task.KEY_CATEGORY_ID, categoryId);
        final WriteBatch writeBatch = db.batch();
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull com.google.android.gms.tasks.Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (DocumentSnapshot documentSnapshot : task.getResult().getDocuments()) {
                        writeBatch.delete(documentSnapshot.getReference());
                    }
                    writeBatch.commit();
                }
            }
        });
    }


    public void createNewTask(String description, String category_id, Date finish_by, int priority) {
        Task task = new Task(description, category_id, false, finish_by, priority);

        taskCollectionRef.add(task)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "onSuccess: Task created with id: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "onFailure: Task creation failed.", e);
                    }
                });
    }

    public void updateTask(DocumentReference documentReference, Task task) {

        documentReference.update(task.toMap())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "updateTask onSuccess: Task Updated.");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "updateTask onFailure: Task Update Failed", e);
                    }
                });
    }

    public void deleteTask(DocumentReference documentReference) {
        documentReference.delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "onSuccess: Task Deleted.");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "onFailure: Task delete failed.", e);
                    }
                });
    }
}