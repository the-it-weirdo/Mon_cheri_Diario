package com.kingominho.monchridiario.ui.toDo;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.kingominho.monchridiario.Category;
import com.kingominho.monchridiario.CategoryCardAdapter;
import com.kingominho.monchridiario.CategoryManager;
import com.kingominho.monchridiario.R;

public class ToDoFragment extends Fragment {
    private final String TAG = "ToDoFragment: ";


    //private ToDoViewModel toDoViewModel;

    private CategoryCardAdapter categoryCardAdapter;
    private CategoryManager categoryManager;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private FloatingActionButton fabAddTask;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        categoryManager = CategoryManager.getInstance();
        View root = inflater.inflate(R.layout.fragment_to_do, container, false);
        recyclerView = root.findViewById(R.id.recycler_view);
        progressBar = root.findViewById(R.id.progress_circle);
        fabAddTask = root.findViewById(R.id.fab_add_task);

        categoryCardAdapter = new CategoryCardAdapter(categoryManager.getAllCategoriesOptions());
        categoryCardAdapter.startListening();
        Log.d(TAG, "onCreateView: categoryAdapter started listening.");
        //toDoViewModel = ViewModelProviders.of(this).get(ToDoViewModel.class);
        /*final TextView textView = root.findViewById(R.id.text_slideshow);
        toDoViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });*/
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        categoryCardAdapter.startListening();
        Log.d(TAG, "onResume: categoryAdapter started listening.");
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setUpRecyclerView();
        fabAddTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop: categoryAdapter to stop listening.");
        categoryCardAdapter.stopListening();
    }

    private void setUpRecyclerView() {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        recyclerView.setAdapter(categoryCardAdapter);
        progressBar.setVisibility(View.GONE);

        Log.d(TAG, "setUpRecyclerView: " + categoryCardAdapter.getItemCount());

        categoryCardAdapter.setOnItemClickListener(new CategoryCardAdapter.OnItemClickListener() {
            @Override
            public void OnItemClick(DocumentSnapshot documentSnapshot, int position) {
                Category category = documentSnapshot.toObject(Category.class);
                String id = documentSnapshot.getId();
                String path = documentSnapshot.getReference().getPath();
                documentSnapshot.getReference();
                Toast.makeText(getContext(), "Position: " + position + " ID: " + id + " clicked." +
                                " path= " + path, Toast.LENGTH_SHORT).show();
            }
        });
    }
}