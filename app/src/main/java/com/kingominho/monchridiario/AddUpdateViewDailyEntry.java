package com.kingominho.monchridiario;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

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
        action = "";
        try {
            action = getArguments().getString("action");
        } catch (NullPointerException e) {
            action = "add";
        } catch (Exception e) {
            Log.e(TAG, "onViewCreated: Error", e);
        }

        Toast.makeText(getContext(), action, Toast.LENGTH_SHORT).show();

        if (action.compareTo("add") == 0) {
            addUI();
        } else if (action.compareTo("update") == 0) {
            updateUI(/*DailyEntry entry*/);
        } else if (action.compareTo("view") == 0) {
            viewUI(/*DailyEntry entry*/);
        }
    }

    private void addUI() {
        String currentDate = textClockDate.getText().toString();
        String currentTime = textClockTime.getText().toString();
        String entry = editTextEntry.getText().toString();
        //DailyEntryManager.addDailyEntry(entry, currentDate, currentTime);
    }

    private void updateUI(/*DailyEntry entry*/) {
        textClockTime.setVisibility(View.GONE);
        textClockDate.setVisibility(View.GONE);
        textViewDate.setVisibility(View.VISIBLE);
        textViewTime.setVisibility(View.VISIBLE);

        textViewDate.setText(/*entry.getDate()*/"");
        textViewTime.setText(/*entry.getTime()*/"");
        editTextEntry.setText(/*entry.getEntry()*/"");
    }

    private void viewUI(/*DailyEntry entry*/) {
        updateUI(/*DailyEntry entry*/);
        editTextEntry.setEnabled(false);
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

        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_save, menu);
    }

}
