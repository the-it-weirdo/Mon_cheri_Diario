package com.kingominho.monchridiario;


import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;


/**
 * A simple {@link Fragment} subclass.
 */
public class AddTask extends Fragment {
    private final String TAG = "AddTask: ";

    private CategoryManager categoryManager;

    private AddTaskViewModel addTaskViewModel;

    private TextView textViewUserDisplayName;
    private EditText editTextDescription;
    private Spinner spinnerCategory;
    private Button buttonFinishByDate;
    private Button buttonFinishByTime;
    private NumberPicker numberPickerPriority;
    private ProgressBar progressBar;

    public AddTask() {
        // Required empty public constructor
        setHasOptionsMenu(true);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        setHasOptionsMenu(true);
        addTaskViewModel = ViewModelProviders.of(this).get(AddTaskViewModel.class);
        categoryManager = CategoryManager.getInstance();
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
        progressBar = view.findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.GONE);


        addTaskViewModel.getUserName().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                textViewUserDisplayName.setText(s);
            }
        });

        addTaskViewModel.getDate().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                buttonFinishByDate.setText(s);
            }
        });

        addTaskViewModel.getTime().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                buttonFinishByTime.setText(s);
            }
        });

        addTaskViewModel.getPriority().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                numberPickerPriority.setValue(integer);
            }
        });
        numberPickerPriority.setMaxValue(10);
        numberPickerPriority.setMinValue(1);

        addTaskViewModel.getTaskDescription().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                editTextDescription.setText(s);
            }
        });

        populateSpinner();
        spinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Category category = (Category) parent.getSelectedItem();
                addTaskViewModel.getCategory().setValue(category);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

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

    @Override
    public void onPause() {
        super.onPause();
        addTaskViewModel.getTaskDescription().setValue(editTextDescription.getText().toString());
        addTaskViewModel.getDate().setValue(buttonFinishByDate.getText().toString());
        addTaskViewModel.getTime().setValue(buttonFinishByTime.getText().toString());
        addTaskViewModel.getPriority().setValue(numberPickerPriority.getValue());
        addTaskViewModel.getCategory().setValue((Category) spinnerCategory.getSelectedItem());
    }

    private void populateSpinner() {
        final List<Category> categories = new ArrayList<>();
        categories.add(addTaskViewModel.defaultCategory);
        final ArrayAdapter<Category> categoryArrayAdapter = new ArrayAdapter<Category>(getContext(),
                android.R.layout.simple_spinner_item, categories);
        categoryArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(categoryArrayAdapter);
        Query query = categoryManager.getAllCategories(FirebaseAuth.getInstance().getCurrentUser().getUid());
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull com.google.android.gms.tasks.Task<QuerySnapshot> task) {
                for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                    Category c = documentSnapshot.toObject(Category.class);
                    c.setCategoryKey(documentSnapshot.getId());
                    categories.add(c);
                }
                categoryArrayAdapter.notifyDataSetChanged();
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
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
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
                saveClicked();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_save, menu);
    }

    private void saveClicked() {
        updateUI(false);
        if (validateInput()) {
            String desc = editTextDescription.getText().toString();
            String date1 = buttonFinishByDate.getText().toString();
            String time = buttonFinishByTime.getText().toString();
            Category c = (Category) spinnerCategory.getSelectedItem();
            String catId = c.getCategoryKey();
            int priority = numberPickerPriority.getValue();

            String s = date1 + " " + time;
            Date date;
            try {
                date = new SimpleDateFormat("dd/MMM/yyyy hh:mm a").parse(s);
            } catch (ParseException e) {
                date = null;
                Log.d(TAG, "saveClicked: Invalid date format", e);
            }
            TaskManager.getInstance().createNewTask(desc, catId, date, priority);
            Toast.makeText(getContext(), "Task Saved.", Toast.LENGTH_SHORT).show();
            addTaskViewModel.restoreDefaultValues();
            Navigation.findNavController(getView()).navigateUp();
        } else {
            updateUI(true);
        }
    }

    private void updateUI(boolean isEnabled) {
        editTextDescription.setEnabled(isEnabled);
        buttonFinishByDate.setEnabled(isEnabled);
        buttonFinishByTime.setEnabled(isEnabled);
        spinnerCategory.setEnabled(isEnabled);
        numberPickerPriority.setEnabled(isEnabled);
        if (!isEnabled) {
            progressBar.setVisibility(View.VISIBLE);
        } else {
            progressBar.setVisibility(View.GONE);
        }
    }

    private boolean validateInput() {
        if (editTextDescription.getText().toString().trim().isEmpty()) {
            editTextDescription.setError("Cannot be empty.");
            editTextDescription.requestFocus();
            return false;
        } else if (buttonFinishByDate.getText().toString().compareTo(addTaskViewModel.default_date_prompt) == 0) {
            buttonFinishByDate.setError("Please select Date");
            buttonFinishByDate.requestFocus();
            return false;
        } else if (buttonFinishByTime.getText().toString().compareTo(addTaskViewModel.default_time_prompt) == 0) {
            buttonFinishByTime.setError("Please select Time");
            buttonFinishByTime.requestFocus();
            return false;
        } else {
            Log.d(TAG, "validateInput: inside else");
            Category c = (Category) spinnerCategory.getSelectedItem();
            if (c.equals(addTaskViewModel.defaultCategory)) {
                TextView errorText = (TextView)spinnerCategory.getSelectedView();
                errorText.setTextColor(Color.RED);//just to highlight that this is an error
                return false;
            }
        }
        return true;
    }

}
