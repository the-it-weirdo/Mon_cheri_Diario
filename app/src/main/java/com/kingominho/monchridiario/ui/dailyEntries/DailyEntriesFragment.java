package com.kingominho.monchridiario.ui.dailyEntries;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.kingominho.monchridiario.DailyEntry;
import com.kingominho.monchridiario.DailyEntryAdapter;
import com.kingominho.monchridiario.DailyEntryManager;
import com.kingominho.monchridiario.R;

public class DailyEntriesFragment extends Fragment {

    private final String TAG = "DailyEntriesFragment: ";


    private DailyEntryAdapter dailyEntryAdapter;
    private DailyEntryManager dailyEntryManager;

    private FloatingActionButton fabAddDailyEntry;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_daily_entries, container, false);
        fabAddDailyEntry = root.findViewById(R.id.fab_add_daily_entry);
        recyclerView = root.findViewById(R.id.recycler_view);
        progressBar = root.findViewById(R.id.progress_circle);

        dailyEntryManager = DailyEntryManager.getInstance();
        dailyEntryAdapter = new DailyEntryAdapter(dailyEntryManager.getAllDailyEntriesOptions());
        dailyEntryAdapter.startListening();

        return root;
    }

    @Override
    public void onResume() {
        dailyEntryAdapter.startListening();
        Log.d(TAG, "onResume: dailyEntryAdapter to start listening.");
        super.onResume();
    }

    @Override
    public void onStop() {
        Log.d(TAG, "onStop: dailyEntryAdapter to stop listening.");
        dailyEntryAdapter.stopListening();
        super.onStop();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fabAddDailyEntry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("action", "add");
                Navigation.findNavController(v).navigate(R.id.addUpdateViewDailyEntry, bundle);
            }
        });
        Log.d(TAG, "onViewCreated: " + dailyEntryAdapter.getItemCount());
        setUpRecyclerView();
    }

    private void setUpRecyclerView() {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(),
                LinearLayoutManager.HORIZONTAL, false));

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
        linearSnapHelper.attachToRecyclerView(recyclerView);
        recyclerView.setAdapter(dailyEntryAdapter);
        progressBar.setVisibility(View.GONE);

        dailyEntryAdapter.setOnContextMenuItemClickListener(new DailyEntryAdapter.OnContextMenuItemClickListener() {
            @Override
            public void onViewEntryClick(DocumentSnapshot documentSnapshot, int position) {

            }

            @Override
            public void onUpdateEntryClick(DocumentSnapshot documentSnapshot, int position) {
                DailyEntry dailyEntry =documentSnapshot.toObject(DailyEntry.class);
                String entry = "Debaleen";
                dailyEntry.setEntry(entry);
                dailyEntryManager.updateDailyEntry(documentSnapshot.getReference(), dailyEntry);
            }

            @Override
            public void onDeleteEntryClick(DocumentSnapshot documentSnapshot, int position) {
                dailyEntryManager.deleteDailyEntry(documentSnapshot.getReference());
            }
        });
    }
}