package com.kingominho.monchridiario.ui.home;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.kingominho.monchridiario.adapters.TaskAdapter;
import com.kingominho.monchridiario.models.DailyEntry;
import com.kingominho.monchridiario.adapters.DailyEntryAdapter;
import com.kingominho.monchridiario.R;
import com.kingominho.monchridiario.models.Task;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;

    private final String TAG = "HomeFragment: ";

    private final int ITEM_TASK = 1;
    private final int ITEM_DAILY_ENTRY = 2;

    private TextView textViewShowingWhat;
    private TextView textViewWelcome;
    private RecyclerView recyclerView;
    private TextView emptyTextView;
    private ProgressBar progressBar;

    private Button taskButton, dailyEntryButton;
    private FloatingActionButton fab;


    private LinearSnapHelper linearSnapHelper;
    private LinearLayoutManager taskLinearLayoutManager, deLinearLayoutManager;


    public HomeFragment() {
        //required
    }

    public static HomeFragment newInstance() {
        HomeFragment fragment = new HomeFragment();
        return fragment;
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        homeViewModel.getDailyEntryAdapter().startListening();
        homeViewModel.getTaskAdapter().startListening();
        textViewWelcome = root.findViewById(R.id.welcome_text_view);
        recyclerView = root.findViewById(R.id.recycler_view_tasks);
        recyclerView.setHasFixedSize(true);
        taskButton = root.findViewById(R.id.show_tasks_button);
        dailyEntryButton = root.findViewById(R.id.show_de_button);
        emptyTextView = root.findViewById(R.id.empty_text_view_task);
        progressBar = root.findViewById(R.id.progress_bar_task);
        textViewShowingWhat = root.findViewById(R.id.text_view_showing_what);
        fab = root.findViewById(R.id.fab_home);
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        homeViewModel.getDailyEntryAdapter().startListening();
        homeViewModel.getTaskAdapter().startListening();

        //for first load, show task is default.
        homeViewModel.getSelected().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                int value = integer;
                switch (value) {
                    case R.id.show_tasks_button: {
                        taskButton.setEnabled(false);
                        setRecycler(recyclerView, taskLinearLayoutManager);
                        break;
                    }
                    case R.id.show_de_button: {
                        dailyEntryButton.setEnabled(false);
                        setRecycler(recyclerView, deLinearLayoutManager);
                        break;
                    }
                }
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        recyclerView.setLayoutManager(null);
        recyclerView.setAdapter(null);
    }

    @Override
    public void onStop() {
        super.onStop();
        homeViewModel.getDailyEntryAdapter().stopListening();
        homeViewModel.getTaskAdapter().stopListening();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        taskLinearLayoutManager = null;
        deLinearLayoutManager = null;
        linearSnapHelper = null;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        String s = "Hello, " + FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        textViewWelcome.setText(s);

        taskLinearLayoutManager = new LinearLayoutManager(getActivity());
        deLinearLayoutManager = new LinearLayoutManager(getActivity(),
                LinearLayoutManager.HORIZONTAL, false);
        linearSnapHelper = new LinearSnapHelper() {
            @Override
            public int findTargetSnapPosition(RecyclerView.LayoutManager layoutManager, int velocityX, int velocityY) {
                View centerView = findSnapView(layoutManager);
                if (centerView == null)
                    return RecyclerView.NO_POSITION;

                int position = layoutManager.getPosition(centerView);
                int targetPosition = -1;
                if (layoutManager.canScrollHorizontally()) {
                    if (velocityX < 0) {
                        targetPosition = position - 1;
                    } else {
                        targetPosition = position + 1;
                    }
                }

                if (layoutManager.canScrollVertically()) {
                    if (velocityY < 0) {
                        targetPosition = position - 1;
                    } else {
                        targetPosition = position + 1;
                    }
                }

                final int firstItem = 0;
                final int lastItem = layoutManager.getItemCount() - 1;
                targetPosition = Math.min(lastItem, Math.max(targetPosition, firstItem));
                return targetPosition;
            }
        };

        homeViewModel.getTaskAdapter().setTaskInteractionListener(new TaskAdapter.OnTaskItemInteractionListener() {
            @Override
            public void onDeleteClick(DocumentSnapshot documentSnapshot, int position) {
                confirmDelete(documentSnapshot, position, ITEM_TASK);
            }

            @Override
            public void onCheckedChange(DocumentSnapshot documentSnapshot, int position, boolean isChecked) {
                Task task = documentSnapshot.toObject(Task.class);
                task.setFinished(isChecked);
                homeViewModel.getTaskManager().updateTask(documentSnapshot.getReference(), task);
            }

            @Override
            public void onDataChanged() {
                homeViewModel.setIsTaskListEmpty(homeViewModel.getTaskAdapter().getItemCount() == 0);
            }
        });

        homeViewModel.getDailyEntryAdapter().setOnClickListener(new DailyEntryAdapter.OnClickListener() {
            @Override
            public void OnClick(DocumentSnapshot documentSnapshot, int position) {
                viewEntry(documentSnapshot, position);
            }

            @Override
            public void onDataChanged() {
                progressBar.setVisibility(View.GONE);
                boolean bool = homeViewModel.getDailyEntryAdapter().getItemCount() == 0;
                if (homeViewModel.getSelected().getValue() == R.id.show_de_button) {
                    updateUI(bool);
                }
            }
        });

        homeViewModel.getDailyEntryAdapter().setOnContextMenuItemClickListener(new DailyEntryAdapter.OnContextMenuItemClickListener() {
            @Override
            public void onViewEntryClick(DocumentSnapshot documentSnapshot, int position) {
                viewEntry(documentSnapshot, position);
            }

            @Override
            public void onUpdateEntryClick(DocumentSnapshot documentSnapshot, int position) {
                DailyEntry dailyEntry = documentSnapshot.toObject(DailyEntry.class);
                dailyEntry.setDaily_entry_id(documentSnapshot.getId());
                Bundle bundle = new Bundle();
                bundle.putParcelable("DailyEntry", dailyEntry);
                bundle.putString("action", "update");
                Navigation.findNavController(getView()).navigate(R.id.addUpdateViewDailyEntry, bundle);
            }

            @Override
            public void onDeleteEntryClick(DocumentSnapshot documentSnapshot, int position) {
                //homeViewModel.getDailyEntryManager().deleteDailyEntry(documentSnapshot.getReference());
                confirmDelete(documentSnapshot, position, ITEM_DAILY_ENTRY);
            }
        });

        homeViewModel.getIsTaskListEmpty().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                progressBar.setVisibility(View.GONE);
                if (homeViewModel.getSelected().getValue() == R.id.show_tasks_button) {
                    updateUI(homeViewModel.getIsTaskListEmpty().getValue());
                }
            }
        });

        taskButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                setSelected(taskButton);
                updateUI(false);
                setRecycler(recyclerView, taskLinearLayoutManager);
            }
        });

        dailyEntryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                setSelected(dailyEntryButton);
                updateUI(false);
                setRecycler(recyclerView, deLinearLayoutManager);
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (homeViewModel.getSelected().getValue()) {
                    case R.id.show_tasks_button: {
                        Navigation.findNavController(v).navigate(R.id.addTask);
                        break;
                    }
                    case R.id.show_de_button: {
                        Bundle bundle = new Bundle();
                        bundle.putString("action", "add");
                        Navigation.findNavController(v).navigate(R.id.addUpdateViewDailyEntry, bundle);
                        break;
                    }
                }
            }
        });
    }

    private void updateUI(boolean bool) {
        progressBar.setVisibility(View.GONE);
        emptyTextView.setVisibility(bool ? View.VISIBLE : View.GONE);
        switch (homeViewModel.getSelected().getValue()) {
            case R.id.show_tasks_button: {
                textViewShowingWhat.setText("Tasks");
                emptyTextView.setText(getResources().getText(R.string.empty_remaining_tasks));
                break;
            }
            case R.id.show_de_button: {
                textViewShowingWhat.setText("Daily Entries");
                emptyTextView.setText(getResources().getText(R.string.empty_daily_entry));
            }

        }
    }

    private void setRecycler(RecyclerView recycler, LinearLayoutManager layoutManager) {
        switch (homeViewModel.getSelected().getValue()) {
            case R.id.show_tasks_button: {
                dailyEntryButton.setEnabled(true);
                linearSnapHelper.attachToRecyclerView(null);
                recycler.setAdapter(homeViewModel.getTaskAdapter());
                recycler.setLayoutManager(layoutManager);
                break;
            }
            case R.id.show_de_button: {
                taskButton.setEnabled(true);
                recycler.setAdapter(homeViewModel.getDailyEntryAdapter());
                recycler.setLayoutManager(layoutManager);
                linearSnapHelper.attachToRecyclerView(recycler);
                break;
            }
        }
        updateUI(recycler.getAdapter().getItemCount() == 0);
    }

    private void viewEntry(DocumentSnapshot documentSnapshot, int position) {
        Bundle bundle = new Bundle();
        bundle.putString("action", "view");
        bundle.putString("id", documentSnapshot.getId());
        bundle.putParcelable("DailyEntry", documentSnapshot.toObject(DailyEntry.class));
        Navigation.findNavController(getView()).navigate(R.id.addUpdateViewDailyEntry, bundle);
    }

    private void setSelected(Button b) {
        b.setSelected(true);
        b.setEnabled(false);
        homeViewModel.setSelected(b.getId());
    }

    private void confirmDelete(final DocumentSnapshot documentSnapshot, final int position, final int item) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle("Confirm action.");
        builder.setMessage("Are you sure you want to delete this ?" +
                "\nThis action cannot be undone.");

        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == DialogInterface.BUTTON_POSITIVE) {
                    switch (item) {
                        case ITEM_TASK: {
                            homeViewModel.getTaskManager().deleteTask(documentSnapshot.getReference());
                            break;
                        }
                        case ITEM_DAILY_ENTRY: {
                            homeViewModel.getDailyEntryManager().deleteDailyEntry(documentSnapshot.getReference());
                            break;
                        }
                    }
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