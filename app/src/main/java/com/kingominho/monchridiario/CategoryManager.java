package com.kingominho.monchridiario;

import android.util.Log;

import androidx.annotation.NonNull;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class CategoryManager {
    private final static String TAG = "CategoryManager: ";

    private final static String CATEGORY_COLLECTION_PATH = "categories";

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference categoryCollectionRef = db.collection(CATEGORY_COLLECTION_PATH);

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

    public FirestoreRecyclerOptions<Category> getAllCategoriesOptions() {
        Query query = categoryCollectionRef.whereEqualTo(Category.KEY_USER_ID,
                FirebaseAuth.getInstance().getCurrentUser().getUid())
                .orderBy(Category.KEY_CATEGORY_NAME);

        /* query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                Log.d(TAG, "onSuccess: Query Returned: " + queryDocumentSnapshots.size() + " records.");
            }
        }); */


        FirestoreRecyclerOptions<Category> options = new FirestoreRecyclerOptions.Builder<Category>()
                .setQuery(query, Category.class)
                .build();
        return options;
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
