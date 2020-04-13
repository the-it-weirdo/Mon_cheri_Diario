package com.kingominho.monchridiario.ui.viewTasks;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.kingominho.monchridiario.R;
import com.kingominho.monchridiario.adapters.TaskAdapter;


/**
 * A simple {@link Fragment} subclass.
 */
public class ViewRemainingTasksFragment extends Fragment {

    private static final String TAG = "ViewRemainingTasks: ";

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private FloatingActionButton fabAddTask;
    private TextView emptyTextView;

    private ViewTasksViewModel viewTasksViewModel;

    private String category_id;

    public ViewRemainingTasksFragment() {
        // Required constructor
        setHasOptionsMenu(true);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        setHasOptionsMenu(true);
        category_id = getArguments().getString("category_id");
        viewTasksViewModel = new ViewModelProvider(this).get(ViewTasksViewModel.class);
        viewTasksViewModel.setTaskAdapter(category_id,false);
        viewTasksViewModel.getTaskAdapter().startListening();

        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_view_task_by_category, container, false);
        recyclerView = root.findViewById(R.id.recycler_view);
        progressBar = root.findViewById(R.id.progress_bar);
        fabAddTask = root.findViewById(R.id.fab_add_task);
        emptyTextView = root.findViewById(R.id.empty_text_view);
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setUpRecyclerView(recyclerView, progressBar, viewTasksViewModel.getTaskAdapter());
        fabAddTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigate(R.id.addTask);
            }
        });

        viewTasksViewModel.getIsTaskListEmpty().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                progressBar.setVisibility(View.GONE);
                emptyTextView.setText(getResources().getText(R.string.empty_remaining_tasks));
                emptyTextView.setVisibility(viewTasksViewModel.getIsTaskListEmpty().getValue() ? View.VISIBLE : View.GONE);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        viewTasksViewModel.getTaskAdapter().startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        viewTasksViewModel.getTaskAdapter().stopListening();
    }

    private void setUpRecyclerView(RecyclerView recyclerView, final ProgressBar progressBar, TaskAdapter taskAdapter) {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(taskAdapter);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_show_completed, menu);
    }

    //TODO:Not working
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_show_completed:
                Bundle bundle = new Bundle();
                bundle.putString("category_id", category_id);
                Navigation.findNavController(getView()).navigate(R.id.viewCompletedTasksFragment, bundle);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
