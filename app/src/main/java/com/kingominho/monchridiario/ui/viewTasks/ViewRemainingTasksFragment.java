package com.kingominho.monchridiario.ui.viewTasks;


import android.app.AlertDialog;
import android.content.DialogInterface;
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

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.kingominho.monchridiario.R;
import com.kingominho.monchridiario.adapters.TaskAdapter;
import com.kingominho.monchridiario.models.Task;


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

        viewTasksViewModel.getTaskAdapter().setTaskInteractionListener(new TaskAdapter.OnTaskItemInteractionListener() {
            @Override
            public void onDeleteClick(DocumentSnapshot documentSnapshot, int position) {
                Log.d(TAG, "onDeleteClick: Task Delete button clicked.");
                confirmDelete(documentSnapshot, position);
            }

            @Override
            public void onCheckedChange(DocumentSnapshot documentSnapshot, int position, boolean isChecked) {
                Task task = documentSnapshot.toObject(Task.class);
                task.setFinished(isChecked);
                viewTasksViewModel.getTaskManager().updateTask(documentSnapshot.getReference(), task);
            }

            @Override
            public void onDataChanged() {
                viewTasksViewModel.setIsTaskListEmpty(viewTasksViewModel.getTaskAdapter().getItemCount() == 0);
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

    private void confirmDelete(final DocumentSnapshot documentSnapshot, final int position) {
        Log.d(TAG, "confirmDelete: confirmDelete function called.");
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle("Confirm action.");
        builder.setMessage("Are you sure you want to delete this ?" +
                "\nThis action cannot be undone.");

        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == DialogInterface.BUTTON_POSITIVE) {
                    viewTasksViewModel.getTaskManager().deleteTask(documentSnapshot.getReference());
                }
                dialog.dismiss();
            }
        };

        builder.setPositiveButton("Yes", listener);
        builder.setNegativeButton("No", listener);

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}
