package com.kingominho.monchridiario;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ProgressBar;

import android.widget.Toast;


import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;

import com.google.firebase.firestore.DocumentSnapshot;

public class ManageCategoryFragment extends Fragment {
    private final static String TAG = "ManageCategoryFragment";


    private CategoryAdapter categoryAdapter;

    private CategoryManager categoryManager;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private FloatingActionButton fabAddCategory;

    //TODO: Recyclerview not loading. StringBuilder not sending messages.

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        categoryManager = CategoryManager.getInstance();

        View root = inflater.inflate(R.layout.fragment_manage_category, container, false);
        recyclerView = root.findViewById(R.id.recycler_view);
        progressBar = root.findViewById(R.id.progress_circle);
        fabAddCategory = root.findViewById(R.id.fab_add_category);
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setUpRecyclerView();
        fabAddCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: create new category
                String message = categoryManager.createNewCategory("New Category " +
                        FirebaseAuth.getInstance().getCurrentUser().getDisplayName() + " ");
                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        categoryAdapter.startListening();
        Log.d(TAG, "onActivityCreated: categoryAdapter started listening.");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop: categoryAdapter to stop listening.");
        categoryAdapter.stopListening();
    }

    private void setUpRecyclerView() {

        categoryAdapter = new CategoryAdapter(categoryManager.getAllCategoriesOptions());

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(categoryAdapter);
        progressBar.setVisibility(View.GONE);

        Log.d(TAG, "setUpRecyclerView: " + categoryAdapter.getItemCount());

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                categoryAdapter.deleteItem(viewHolder.getAdapterPosition());
            }
        }).attachToRecyclerView(recyclerView);

        categoryAdapter.setOnItemClickListener(new CategoryAdapter.OnItemClickListener() {
            @Override
            public void OnItemClick(DocumentSnapshot documentSnapshot, int position) {
                Category category = documentSnapshot.toObject(Category.class);
                String id = documentSnapshot.getId();
                String path = documentSnapshot.getReference().getPath();
                documentSnapshot.getReference();
                Toast.makeText(getContext(), "Position: " + position + " ID: " + id + " clicked.",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

}
