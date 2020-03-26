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

import com.kingominho.monchridiario.R;

public class DailyEntriesFragment extends Fragment {

    private DailyEntriesViewModel dailyEntriesViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        dailyEntriesViewModel =
                ViewModelProviders.of(this).get(DailyEntriesViewModel.class);
        View root = inflater.inflate(R.layout.fragment_daily_entries, container, false);
        final TextView textView = root.findViewById(R.id.text_gallery);
        dailyEntriesViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        return root;
    }
}