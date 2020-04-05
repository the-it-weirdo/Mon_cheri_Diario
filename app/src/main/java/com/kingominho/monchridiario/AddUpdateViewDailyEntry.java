package com.kingominho.monchridiario;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;


/**
 * A simple {@link Fragment} subclass.
 */
public class AddUpdateViewDailyEntry extends Fragment {
    private final static String TAG = "AddDailyFragment: ";

    private TextClock textClockDate;
    private TextClock textClockTime;
    private TextView textViewDate;
    private TextView textViewTime;
    private EditText editTextEntry;
    private TextView textViewViewEntry;

    private String action;

    public AddUpdateViewDailyEntry() {
        setHasOptionsMenu(true);
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_add_daily_entry, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        textClockDate = view.findViewById(R.id.current_date);
        textClockTime = view.findViewById(R.id.current_time);
        textViewDate = view.findViewById(R.id.date);
        textViewTime = view.findViewById(R.id.time);
        editTextEntry = view.findViewById(R.id.edit_text_daily_entry);
        textViewViewEntry = view.findViewById(R.id.textview_view_entry);
        action = "";
        try {
            action = getArguments().getString("action");
        } catch (NullPointerException e) {
            action = "add";
        } catch (Exception e) {
            Log.e(TAG, "onViewCreated: Error", e);
        }

        if (action.compareTo("add") == 0) {
            addUI();
        } else if (action.compareTo("update") == 0) {
            DailyEntry dailyEntry = getArguments().getParcelable("DailyEntry");
            updateUI(dailyEntry);
        } else if (action.compareTo("view") == 0) {
            DailyEntry dailyEntry = getArguments().getParcelable("DailyEntry");
            viewUI(dailyEntry);
        }
    }

    private void addUI() {
        textViewDate.setVisibility(View.GONE);
        textViewTime.setVisibility(View.GONE);
        textViewViewEntry.setVisibility(View.GONE);
    }

    private void updateUI(DailyEntry entry) {
        textClockTime.setVisibility(View.GONE);
        textClockDate.setVisibility(View.GONE);
        textViewDate.setVisibility(View.VISIBLE);
        textViewTime.setVisibility(View.VISIBLE);
        textViewViewEntry.setVisibility(View.GONE);

        textViewDate.setText(entry.getDate());
        textViewTime.setText(entry.getTime());
        editTextEntry.setText(entry.getEntry());
    }

    private void viewUI(DailyEntry entry) {
        editTextEntry.setVisibility(View.GONE);
        textViewViewEntry.setVisibility(View.VISIBLE);
        textClockTime.setVisibility(View.GONE);
        textClockDate.setVisibility(View.GONE);
        textViewDate.setVisibility(View.VISIBLE);
        textViewTime.setVisibility(View.VISIBLE);

        textViewViewEntry.setMovementMethod(new ScrollingMovementMethod());
        textViewViewEntry.setText(entry.getEntry());
        textViewDate.setText(entry.getDate());
        textViewTime.setText(entry.getTime());
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                saveClicked();
                Toast.makeText(getContext(), "Save clicked.", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void saveClicked() {
        if (action.compareTo("add") == 0) {
            String currentDate = textClockDate.getText().toString();
            String currentTime = textClockTime.getText().toString();
            String entry = editTextEntry.getText().toString();
            DailyEntryManager.getInstance().addDailyEntry(currentDate, currentTime, entry);
            Navigation.findNavController(getView()).navigateUp();
        } else if (action.compareTo("update") == 0) {
            String entry = editTextEntry.getText().toString();

            DailyEntryManager dailyEntryManager = DailyEntryManager.getInstance();
            DailyEntry dailyEntry = getArguments().getParcelable("DailyEntry");
            dailyEntry.setEntry(entry);
            dailyEntryManager.updateDailyEntry(dailyEntry);
            Navigation.findNavController(getView()).navigateUp();
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_save, menu);
    }

    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        super.onPrepareOptionsMenu(menu);
        getActivity().invalidateOptionsMenu();
        if(action.compareTo("view")==0) {
            menu.findItem(R.id.action_save).setVisible(false);
        }
    }
}
