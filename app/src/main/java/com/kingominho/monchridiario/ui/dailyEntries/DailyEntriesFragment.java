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


    private DailyEntriesViewModel dailyEntriesViewModel;

    private FloatingActionButton fabAddDailyEntry;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;

    LinearLayoutManager layoutManager;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_daily_entries, container, false);
        dailyEntriesViewModel = ViewModelProviders.of(this).get(DailyEntriesViewModel.class);
        fabAddDailyEntry = root.findViewById(R.id.fab_add_daily_entry);
        recyclerView = root.findViewById(R.id.recycler_view);
        progressBar = root.findViewById(R.id.progress_circle);

        dailyEntriesViewModel.getDailyEntryAdapter().startListening();
        layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);

        return root;
    }

    @Override
    public void onResume() {
        Log.d(TAG, "onResume: position = " + dailyEntriesViewModel.getPosition().getValue());
        layoutManager.scrollToPosition(dailyEntriesViewModel.getPosition().getValue());
        dailyEntriesViewModel.getDailyEntryAdapter().startListening();
        Log.d(TAG, "onResume: dailyEntryAdapter to start listening.");
        super.onResume();
    }

    @Override
    public void onStop() {
        Log.d(TAG, "onStop: dailyEntryAdapter to stop listening.");
        dailyEntriesViewModel.getDailyEntryAdapter().stopListening();
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
        Log.d(TAG, "onViewCreated: " + dailyEntriesViewModel.getDailyEntryAdapter().getItemCount());
        setUpRecyclerView();
    }

    private void setUpRecyclerView() {
        Log.d(TAG, "setUpRecyclerView() called.");
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);

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
                dailyEntriesViewModel.getPosition().setValue(targetPosition);
                return targetPosition;
            }
        };
        linearSnapHelper.attachToRecyclerView(recyclerView);
        recyclerView.setAdapter(dailyEntriesViewModel.getDailyEntryAdapter());
        progressBar.setVisibility(View.GONE);

        dailyEntriesViewModel.getDailyEntryAdapter()
                .setOnContextMenuItemClickListener(new DailyEntryAdapter.OnContextMenuItemClickListener() {
                    @Override
                    public void onViewEntryClick(DocumentSnapshot documentSnapshot, int position) {
                        onViewEntryClick(documentSnapshot, position);
                    }

                    @Override
                    public void onUpdateEntryClick(DocumentSnapshot documentSnapshot, int position) {
                        dailyEntriesViewModel.getPosition().setValue(position);
                        DailyEntry dailyEntry = documentSnapshot.toObject(DailyEntry.class);
                        dailyEntry.setDaily_entry_id(documentSnapshot.getId());
                        Bundle bundle = new Bundle();
                        bundle.putParcelable("DailyEntry", dailyEntry);
                        bundle.putString("action", "update");
                        Navigation.findNavController(getView()).navigate(R.id.addUpdateViewDailyEntry, bundle);
                    }

                    @Override
                    public void onDeleteEntryClick(DocumentSnapshot documentSnapshot, int position) {
                        dailyEntriesViewModel.getPosition().setValue(position - 1);
                        dailyEntriesViewModel.getDailyEntryManager().deleteDailyEntry(documentSnapshot.getReference());
                    }
                });
        dailyEntriesViewModel.getDailyEntryAdapter()
                .setOnClickListener(new DailyEntryAdapter.OnClickListener() {
                    @Override
                    public void OnClick(DocumentSnapshot documentSnapshot, int position) {
                        onViewEntryClick(documentSnapshot, position);
                    }
                });
    }

    private void onViewEntryClick(DocumentSnapshot documentSnapshot, int position) {
        dailyEntriesViewModel.getPosition().setValue(position);
        Bundle bundle = new Bundle();
        bundle.putString("action", "view");
        bundle.putString("id", documentSnapshot.getId());
        bundle.putParcelable("DailyEntry", documentSnapshot.toObject(DailyEntry.class));
        Navigation.findNavController(getView()).navigate(R.id.addUpdateViewDailyEntry, bundle);
    }
}