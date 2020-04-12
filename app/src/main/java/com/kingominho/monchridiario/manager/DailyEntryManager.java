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
import com.kingominho.monchridiario.models.DailyEntry;

public class DailyEntryManager {

    private static String TAG = "DailyEntryManager:";

    private final String DAILY_ENTRY_COLLECTION_PATH = "daily_entry";

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference dailyEntryCollectionRef = db.collection(DAILY_ENTRY_COLLECTION_PATH);

    private static DailyEntryManager instance;

    private DailyEntryManager() {

    }

    public static DailyEntryManager getInstance() {
        if (instance == null) {
            instance = new DailyEntryManager();
        }
        return instance;
    }

    public FirestoreRecyclerOptions<DailyEntry> getAllDailyEntriesOptions(String user_id) {
        Query query = dailyEntryCollectionRef.whereEqualTo(DailyEntry.USER_ID_KEY, user_id)
                .orderBy(DailyEntry.TIME_STAMP_KEY, Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<DailyEntry> options = new FirestoreRecyclerOptions.Builder<DailyEntry>()
                .setQuery(query, DailyEntry.class)
                .build();
        return options;
    }

    public void addDailyEntry(String date, String time, String entry) {
        //String date, String time, String entry, String timeStamp, String user_id
        String timestamp = String.valueOf(System.currentTimeMillis());
        DailyEntry dailyEntry = new DailyEntry(date, time, entry, timestamp, FirebaseAuth.getInstance().getCurrentUser().getUid());

        dailyEntryCollectionRef.add(dailyEntry)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }

    private DailyEntry dailyEntry;

    public DailyEntry getDailyEntryById(String id) {
        dailyEntryCollectionRef.document(id).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        dailyEntry = documentSnapshot.toObject(DailyEntry.class);
                    }
                });

        Log.d(TAG, "getDailyEntryById: de" + dailyEntry.getEntry());
        return dailyEntry;
    }

    public CollectionReference getDailyEntryCollectionRef() {
        return dailyEntryCollectionRef;
    }

    private DocumentReference documentReference;

    public DocumentReference getReferenceById(String id) {
        dailyEntryCollectionRef.document(id).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        documentReference = documentSnapshot.getReference();
                    }
                });

        Log.d(TAG, "getReferenceById: " + documentReference.getId());

        return documentReference;
    }

    public void updateDailyEntry(DocumentReference documentReference, DailyEntry dailyEntry) {

        documentReference.update(dailyEntry.toMap())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }

    public void updateDailyEntry(DailyEntry dailyEntry) {
        dailyEntryCollectionRef.document(dailyEntry.getDaily_entry_id())
                .update(dailyEntry.toMap())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }

    public void deleteDailyEntry(DocumentReference documentReference) {
        documentReference.delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }

    public void deleteAllDailyEntry(String user_id) {
        Query query = dailyEntryCollectionRef.whereEqualTo(DailyEntry.USER_ID_KEY, user_id);
        final WriteBatch writeBatch = db.batch();
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()) {
                    for (DocumentSnapshot documentSnapshot : task.getResult().getDocuments()) {
                        writeBatch.delete(documentSnapshot.getReference());
                    }
                    writeBatch.commit();
                }
            }
        });
    }
}
