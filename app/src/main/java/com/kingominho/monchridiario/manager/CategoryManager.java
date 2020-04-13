package com.kingominho.monchridiario.manager;

import android.util.Log;

import androidx.annotation.NonNull;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;
import com.kingominho.monchridiario.models.Category;

public class CategoryManager {
    private final static String TAG = "CategoryManager: ";

    private final static String CATEGORY_COLLECTION_PATH = "categories";

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    public CollectionReference categoryCollectionRef = db.collection(CATEGORY_COLLECTION_PATH);

    private static CategoryManager mInstance;

    private FirebaseAuth mAuth;

    private CategoryManager() {
        //empty private constructor
    }

    public static CategoryManager getInstance() {
        if (mInstance == null) {
            mInstance = new CategoryManager();
        }
        return mInstance;
    }

    public Query getAllCategories(String userId) {
        Query query = categoryCollectionRef.whereEqualTo(Category.KEY_USER_ID, userId)
                .orderBy(Category.KEY_CATEGORY_NAME);
        return query;
    }

    public FirestoreRecyclerOptions<Category> getAllCategoriesOptions() {
        Query query = categoryCollectionRef.whereEqualTo(Category.KEY_USER_ID,
                FirebaseAuth.getInstance().getCurrentUser().getUid())
                .orderBy(Category.KEY_CATEGORY_NAME);

        FirestoreRecyclerOptions<Category> options = new FirestoreRecyclerOptions.Builder<Category>()
                .setQuery(query, Category.class)
                .build();
        Log.d(TAG, "getAllCategoriesOptions: " + options.getSnapshots().size());
        return options;
    }

    public void deleteAllCategories(String userId) {
        Query query = categoryCollectionRef.whereEqualTo(Category.KEY_USER_ID, userId);
        final WriteBatch writeBatch = db.batch();
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()) {
                    for(DocumentSnapshot documentSnapshot: task.getResult().getDocuments())
                    {
                        TaskManager.getInstance().deleteAllTask(documentSnapshot.getId());
                        writeBatch.delete(documentSnapshot.getReference());
                    }
                    writeBatch.commit();
                }
            }
        });
    }


    public String createNewCategory(String categoryName) {

        final StringBuilder categoryId = new StringBuilder();

        mAuth = FirebaseAuth.getInstance();
        String user_id = mAuth.getCurrentUser().getUid();
        Category category = new Category(categoryName, user_id);

        //insert timestamp -> updates.put("timestamp", FieldValue.serverTimestamp());
        //TODO: 1. bug: duplicate entries possible
        categoryCollectionRef.add(category)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        categoryId.append("Success.");
                        Log.d(TAG, "onSuccess: Category created!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "onFailure: Failed to create category.", e);
                    }
                });
        return categoryId.toString();
    }

    public String updateCategoryName(DocumentReference documentReference, String newName) {

        final StringBuilder message = new StringBuilder();

        mAuth = FirebaseAuth.getInstance();
        String user_id = mAuth.getCurrentUser().getUid();
        Category category = new Category(newName, user_id);

        documentReference
                .update(category.toMap())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        message.append("Update successful.");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        message.append("Update failed!!");
                    }
                });


        return message.toString();
    }

    public String deleteCategory(DocumentReference documentReference) {
        final StringBuilder message = new StringBuilder();
        documentReference
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        message.append("Deleted successfully.");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        message.append("Delete failed!!");
                    }
                });
        return message.toString();
    }

}
