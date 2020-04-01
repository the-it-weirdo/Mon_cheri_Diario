package com.kingominho.monchridiario;


import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;


/**
 * A simple {@link Fragment} subclass.
 */
public class AddTask extends Fragment {

    private TextView textViewUserDisplayName;
    private EditText editTextDescription;
    private Spinner spinnerCategory;
    private Button buttonFinishByDate;
    private Button buttonFinishByTime;
    private NumberPicker numberPickerPriority;

    private String category_name;
    private String category_id;

    public AddTask() {
        // Required empty public constructor
        setHasOptionsMenu(true);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_add_task, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        textViewUserDisplayName = view.findViewById(R.id.text_view_display_name);
        editTextDescription = view.findViewById(R.id.edit_text_description);
        spinnerCategory = view.findViewById(R.id.spinner_category);
        buttonFinishByDate = view.findViewById(R.id.button_choose_date);
        buttonFinishByTime = view.findViewById(R.id.button_choose_time);
        numberPickerPriority = view.findViewById(R.id.number_picker);

        try {
            category_name = getArguments().getString(Category.KEY_CATEGORY_NAME);
            category_id = getArguments().getString("category_key");
        } catch (Exception e) {
            category_name = "";
        }

        String s = FirebaseAuth.getInstance().getCurrentUser().getDisplayName() + " " + category_name;
        textViewUserDisplayName.setText(s);
        numberPickerPriority.setMaxValue(10);
        numberPickerPriority.setMinValue(1);
        buttonFinishByDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickDate();
            }
        });
        buttonFinishByTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickTime();
            }
        });

    }

    private void pickDate() {
        final Calendar c = Calendar.getInstance();
        int cyear = c.get(Calendar.YEAR);
        int cmonth = c.get(Calendar.MONTH);
        int cday = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                c.set(Calendar.YEAR, year);
                c.set(Calendar.MONTH, month);
                c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MMM/yyyy", Locale.getDefault());
                buttonFinishByDate.setText(simpleDateFormat.format(c.getTime()));
            }
        }, cyear, cmonth, cday);
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        datePickerDialog.show();
    }

    private void pickTime() {
        final Calendar c = Calendar.getInstance();
        int cHourOfDay = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);
        TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                c.set(Calendar.HOUR_OF_DAY, hourOfDay);
                c.set(Calendar.MINUTE, minute);
                c.set(Calendar.SECOND, 0);
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());
                buttonFinishByTime.setText(simpleDateFormat.format(c.getTime()));
            }
        }, cHourOfDay, minute, false);
        timePickerDialog.show();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                //saveClicked()
                Toast.makeText(getContext(), "Save clicked.", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_save, menu);
    }

}
