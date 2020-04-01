package com.kingominho.monchridiario;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;


/**
 * A simple {@link Fragment} subclass.
 */
public class ViewCategory extends Fragment implements TaskAdapter.OnTaskItemInteractionListener {

    private final String TAG = "ViewCategory:";

    private RecyclerView recyclerViewCompleted, recyclerViewRemaining;
    private ProgressBar progressBarCompleted, progressBarRemaining;
    private FloatingActionButton fabAddTask;

    private TaskAdapter taskAdapterCompleted, taskAdapterRemaining;
    private TaskManager taskManager;

    private String categoryId;


    public ViewCategory() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_view_category, container, false);
        recyclerViewCompleted = root.findViewById(R.id.recycler_view_completed);
        recyclerViewRemaining = root.findViewById(R.id.recycler_view_remaining);
        progressBarCompleted = root.findViewById(R.id.progress_circle_completed);
        progressBarRemaining = root.findViewById(R.id.progress_circle_remaining);
        fabAddTask = root.findViewById(R.id.fab_add_task);
        taskManager = TaskManager.getInstance();

        categoryId = getArguments().getString("category_id");

        taskAdapterCompleted = new TaskAdapter(taskManager.getAllTasksOptions(categoryId, true));
        taskAdapterCompleted.startListening();
        taskAdapterRemaining = new TaskAdapter(taskManager.getAllTasksOptions(categoryId, false));
        taskAdapterRemaining.startListening();
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        taskAdapterCompleted.startListening();
        taskAdapterRemaining.startListening();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setUpRecyclerView(recyclerViewCompleted, progressBarCompleted, taskAdapterCompleted);
        setUpRecyclerView(recyclerViewRemaining, progressBarRemaining, taskAdapterRemaining);

        fabAddTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String categoryName = getArguments().getString(Category.KEY_CATEGORY_NAME);
                Bundle bundle = new Bundle();
                bundle.putString("category_id", categoryId);
                bundle.putString(Category.KEY_CATEGORY_NAME, categoryName);
                Navigation.findNavController(v).navigate(R.id.addTask, bundle);
            }
        });
    }

    void setUpRecyclerView(RecyclerView recyclerView, ProgressBar progressBar, TaskAdapter taskAdapter) {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(taskAdapter);
        progressBar.setVisibility(View.GONE);

        taskAdapter.setTaskInteractionListener(this);
    }

    @Override
    public void onDeleteClick(DocumentSnapshot documentSnapshot, int position) {
        //taskManager.deleteTask(documentSnapshot);
    }

    @Override
    public void onCheckedChange(DocumentSnapshot documentSnapshot, int position) {

    }
}
