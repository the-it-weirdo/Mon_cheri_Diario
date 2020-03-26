package com.kingominho.monchridiario;

import android.util.Log;

import androidx.annotation.NonNull;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class CategoryManager {

    private final static String CATEGORY_COLLECTION_PATH = "categories";

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference categoryCollectionRef = db.collection(CATEGORY_COLLECTION_PATH);

    private static CategoryManager mInstance;

    private FirebaseAuth mAuth;


    public static CategoryManager getInstance() {
        if (mInstance == null) {
            mInstance = new CategoryManager();
        }
        return mInstance;
    }

    public FirestoreRecyclerOptions<Category> getAllCategoriesOptions() {
        Query query = categoryCollectionRef.whereEqualTo(Category.KEY_USER_ID,
                FirebaseAuth.getInstance().getCurrentUser().getUid());


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

    public String updateCategoryName(String newName, String userId, String path) {

        final StringBuilder message = new StringBuilder();

        Category category = new Category(newName, userId);

        //TODO: fix method to get document reference from cellection reference
        DocumentReference documentReference = categoryCollectionRef.document();

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

    public String deleteCategory(String path) {
        final StringBuilder message = new StringBuilder();

        //TODO: fix method to get document reference from collection reference
        DocumentReference documentReference = categoryCollectionRef.document();

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
