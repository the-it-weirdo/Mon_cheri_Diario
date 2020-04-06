package com.kingominho.monchridiario.ui.home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.kingominho.monchridiario.DailyEntry;
import com.kingominho.monchridiario.DailyEntryAdapter;
import com.kingominho.monchridiario.R;
import com.kingominho.monchridiario.Task;
import com.kingominho.monchridiario.TaskAdapter;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;

    private final String TAG = "HomeFragment: ";

    private TextView textViewWelcome;
    private RecyclerView recyclerViewTasks;
    private RecyclerView recyclerViewDailyEntries;
    private TextView emptyTextViewDE;
    private  TextView emptyTextViewTask;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        homeViewModel.getDailyEntryAdapter().startListening();
        homeViewModel.getTaskAdapter().startListening();
        textViewWelcome = root.findViewById(R.id.welcome_text_view);
        recyclerViewTasks = root.findViewById(R.id.recycler_view_tasks);
        recyclerViewDailyEntries = root.findViewById(R.id.recycler_view_daily_entry);
        emptyTextViewDE = root.findViewById(R.id.empty_text_view_de);
        emptyTextViewTask = root.findViewById(R.id.empty_text_view_task);
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        homeViewModel.getDailyEntryAdapter().startListening();
        homeViewModel.getTaskAdapter().startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        homeViewModel.getDailyEntryAdapter().stopListening();
        homeViewModel.getTaskAdapter().stopListening();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        String s = "Hello, " + FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        textViewWelcome.setText(s);
        setDailyEntryRecycler();
        setTaskRecycler();
    }

    void setDailyEntryRecycler() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        recyclerViewDailyEntries.setLayoutManager(linearLayoutManager);
        LinearSnapHelper linearSnapHelper = new LinearSnapHelper() {
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
        linearSnapHelper.attachToRecyclerView(recyclerViewDailyEntries);
        recyclerViewDailyEntries.setAdapter(homeViewModel.getDailyEntryAdapter());
        Log.d(TAG, "setRecyclerView: dailyEntryAdapter Size: " + homeViewModel.getDailyEntryAdapter().getItemCount());

        homeViewModel.getDailyEntryAdapter().setOnClickListener(new DailyEntryAdapter.OnClickListener() {
            @Override
            public void OnClick(DocumentSnapshot documentSnapshot, int position) {
                viewEntry(documentSnapshot, position);
            }

            @Override
            public void onDataChanged() {
                boolean bool = homeViewModel.getDailyEntryAdapter().getItemCount() == 0;
                emptyTextViewDE.setVisibility(bool ? View.VISIBLE : View.GONE);
                emptyTextViewDE.setText(getResources().getText(R.string.empty_daily_entry));
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
                homeViewModel.getDailyEntryManager().deleteDailyEntry(documentSnapshot.getReference());
            }
        });
    }

    private void viewEntry(DocumentSnapshot documentSnapshot, int position) {
        Bundle bundle = new Bundle();
        bundle.putString("action", "view");
        bundle.putString("id", documentSnapshot.getId());
        bundle.putParcelable("DailyEntry", documentSnapshot.toObject(DailyEntry.class));
        Navigation.findNavController(getView()).navigate(R.id.addUpdateViewDailyEntry, bundle);
    }

    void setTaskRecycler() {
        recyclerViewTasks.setHasFixedSize(true);
        recyclerViewTasks.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerViewTasks.setAdapter(homeViewModel.getTaskAdapter());
        //progressBar.setVisibility(View.GONE);

        homeViewModel.getTaskAdapter().setTaskInteractionListener(new TaskAdapter.OnTaskItemInteractionListener() {
            @Override
            public void onDeleteClick(DocumentSnapshot documentSnapshot, int position) {
                homeViewModel.getTaskManager().deleteTask(documentSnapshot.getReference());
            }

            @Override
            public void onCheckedChange(DocumentSnapshot documentSnapshot, int position, boolean isChecked) {
                Task task = documentSnapshot.toObject(Task.class);
                task.setFinished(isChecked);
                homeViewModel.getTaskManager().updateTask(documentSnapshot.getReference(), task);
            }

            @Override
            public void onDataChanged() {
                boolean bool = homeViewModel.getTaskAdapter().getItemCount() == 0;
                emptyTextViewTask.setVisibility(bool? View.VISIBLE: View.GONE);
                emptyTextViewTask.setText(getResources().getText(R.string.empty_remaining_tasks));
            }
        });
    }
}