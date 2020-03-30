package com.kingominho.monchridiario;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

public class TaskManager {

    private final static String TAG = "TaskManager: ";

    private final static String TASKS_COLLECTION_PATH = "tasks";

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference taskCollectionRef = db.collection(TASKS_COLLECTION_PATH);

    private static TaskManager taskManagerInstance;

    private TaskManager() {
        //private empty constructor
    }

    public static  TaskManager getInstance() {
        if (taskManagerInstance == null) {
            taskManagerInstance = new TaskManager();
        }
        return taskManagerInstance;
    }

    public FirestoreRecyclerOptions<Task> getAllTasksOptions(String categoryId, boolean isFinished) {
        Query query = taskCollectionRef.whereEqualTo(Task.KEY_CATEGORY_ID, categoryId)
                .whereEqualTo(Task.KEY_IS_FINISHED, isFinished)
                .orderBy(Task.KEY_FINISH_BY)
                .orderBy(Task.KEY_PRIORITY);
        /* query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                Log.d(TAG, "onSuccess: Query Returned: " + queryDocumentSnapshots.size() + " records.");
            }
        }); */
        FirestoreRecyclerOptions<Task> options = new FirestoreRecyclerOptions.Builder<Task>()
                .setQuery(query, Task.class)
                .build();
        return options;
    }

    public Query getTaskCountQuery(String categoryId, boolean isFinished) {
        Query query = taskCollectionRef.whereEqualTo(Task.KEY_CATEGORY_ID, categoryId)
                .whereEqualTo(Task.KEY_IS_FINISHED, isFinished);
        return query;
    }

}
