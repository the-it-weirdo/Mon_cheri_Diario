package com.kingominho.monchridiario;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;

public class ViewCompletedTasksFragment extends Fragment {
    private final String TAG = "ViewCompletedTasks: ";

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private FloatingActionButton fabAddTask;
    private TextView emptyTextView;

    private ViewTasksViewModel viewTasksViewModel;
    private String category_id;

    public ViewCompletedTasksFragment() {
        // Required constructor
        setHasOptionsMenu(true);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        setHasOptionsMenu(true);
        category_id = getArguments().getString("category_id");
        Log.d(TAG, "onCreateView: Category Id: " + category_id);
        viewTasksViewModel = ViewModelProviders.of(this).get(ViewTasksViewModel.class);
        viewTasksViewModel.setTaskAdapter(category_id, true);
        viewTasksViewModel.getTaskAdapter().startListening();

        //taskManager = TaskManager.getInstance();
        //taskAdapter = new TaskAdapter(taskManager.getAllTasksOptions(category_id, true));
        //taskAdapter.startListening();
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
                /*String categoryName = getArguments().getString(Category.KEY_CATEGORY_NAME);
                Bundle bundle = new Bundle();
                bundle.putString("category_id", categoryId);
                bundle.putString(Category.KEY_CATEGORY_NAME, categoryName);*/
                Navigation.findNavController(v).navigate(R.id.addTask);
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
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(taskAdapter);

        taskAdapter.setTaskInteractionListener(new TaskAdapter.OnTaskItemInteractionListener() {
            @Override
            public void onDeleteClick(DocumentSnapshot documentSnapshot, int position) {
                viewTasksViewModel.getTaskManager().deleteTask(documentSnapshot.getReference());
            }

            @Override
            public void onCheckedChange(DocumentSnapshot documentSnapshot, int position, boolean isChecked) {
                Task task = documentSnapshot.toObject(Task.class);
                task.setFinished(isChecked);
                viewTasksViewModel.getTaskManager().updateTask(documentSnapshot.getReference(), task);
            }

            @Override
            public void onDataChanged() {
                progressBar.setVisibility(View.GONE);
                boolean bool = viewTasksViewModel.getTaskAdapter().getItemCount() == 0;
                emptyTextView.setText(getResources().getText(R.string.empty_completed_task));
                emptyTextView.setVisibility(bool ? View.VISIBLE : View.GONE);
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_show_remaining, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_show_remaining:
                Bundle bundle = new Bundle();
                bundle.putString("category_id", category_id);
                Navigation.findNavController(getView()).navigateUp();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
