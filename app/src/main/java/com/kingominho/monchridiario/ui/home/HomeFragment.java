package com.kingominho.monchridiario.ui.home;

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

import com.google.firebase.auth.FirebaseAuth;
import com.kingominho.monchridiario.R;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;

    private TextView textViewWelcome;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        /*homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        final TextView textView = root.findViewById(R.id.text_home);
        homeViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });*/
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        textViewWelcome = root.findViewById(R.id.welcome_text_view);
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        String s = "Hello, " + FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        textViewWelcome.setText(s);
    }
}