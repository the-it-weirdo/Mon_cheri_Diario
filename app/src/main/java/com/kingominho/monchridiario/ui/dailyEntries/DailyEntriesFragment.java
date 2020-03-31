package com.kingominho.monchridiario.ui.dailyEntries;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.kingominho.monchridiario.R;

public class DailyEntriesFragment extends Fragment {

    private DailyEntriesViewModel dailyEntriesViewModel;

    private FloatingActionButton fabAddDailyEntry;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        dailyEntriesViewModel =
                ViewModelProviders.of(this).get(DailyEntriesViewModel.class);
        View root = inflater.inflate(R.layout.fragment_daily_entries, container, false);
        fabAddDailyEntry = root.findViewById(R.id.fab_add_daily_entry);
        //final TextView textView = root.findViewById(R.id.text_gallery);
        dailyEntriesViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
        //        textView.setText(s);
            }
        });
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fabAddDailyEntry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigate(R.id.addDailyEntry);
            }
        });
    }
}