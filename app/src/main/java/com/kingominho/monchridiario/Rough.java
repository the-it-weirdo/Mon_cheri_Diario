/*
package com.kingominho.monchridiario;

import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.StorageReference;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Rough {

    @Override
    public void onDeleteClick(int position) {
        Upload selectedItem = mUploads.get(position);
        final String selectedKey = selectedItem.getmKey();

        StorageReference imageRef = mStorage.getReferenceFromUrl(selectedItem.getmImageUrl());
        imageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                mDatabaseReference.child(selectedKey).removeValue();
                Toast.makeText(ImagesActivity.this, "Item Deleted", Toast.LENGTH_SHORT).show();
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ImagesActivity.this, "Failed !!", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void test()
    {
        CategoryManager categoryManager;

        categoryManager = CategoryManager.getInstance();

        int position = 2;

        LocalDateTime localDateTime = LocalDateTime.of()

    }
}
*/