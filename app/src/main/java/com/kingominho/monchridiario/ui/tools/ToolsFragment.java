package com.kingominho.monchridiario.ui.tools;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;

import com.google.firebase.auth.FirebaseAuth;
import com.kingominho.monchridiario.LoginActivity;
import com.kingominho.monchridiario.R;
import com.squareup.picasso.Picasso;

public class ToolsFragment extends Fragment {

    private ToolsViewModel toolsViewModel;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        toolsViewModel =
                ViewModelProviders.of(this).get(ToolsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_tools, container, false);

        /*final TextView textView = root.findViewById(R.id.text_tools);
        toolsViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });*/
        return root;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ImageView profilePictureImageView = view.findViewById(R.id.profile_image_view);
        Picasso.with(getContext())
                .load(toolsViewModel.getmProfilePicUrl())
                .placeholder(view.getResources().getDrawable(R.drawable.ic_person_black_24dp))
                .into(profilePictureImageView);

        TextView displayNameTextView = view.findViewById(R.id.display_name_text_view);
        displayNameTextView.setText(toolsViewModel.getmDisplayName());

        TextView emailTextView = view.findViewById(R.id.email_text_view);
        emailTextView.setText(toolsViewModel.getmEmail());

        Button changeProfilePictureButton = view.findViewById(R.id.change_profile_picture_button);
        changeProfilePictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeProfilePictureButtonClicked();
            }
        });

        Button changePasswordButton = view.findViewById(R.id.change_password_button);
        changePasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changePasswordButtonClicked();
            }
        });

        Button manageCategoriesButton = view.findViewById(R.id.manage_categories_button);
        manageCategoriesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                manageCategoriesButtonClicked(v);
            }
        });

        Button deleteAccountButton = view.findViewById(R.id.delete_account_button);
        deleteAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteAccountButtonClicked();
            }
        });
    }

    private void changeProfilePictureButtonClicked() {
        Toast.makeText(getContext(), "Profile Picture changed!", Toast.LENGTH_SHORT).show();
    }


    private void changePasswordButtonClicked() {
        Toast.makeText(getContext(), "Password changed!!", Toast.LENGTH_SHORT).show();
    }


    private void manageCategoriesButtonClicked(View view) {
        //Toast.makeText(getContext(), "Categories Managed!!", Toast.LENGTH_SHORT).show();
        Navigation.findNavController(view).navigate(R.id.manageCategory);
    }


    private void deleteAccountButtonClicked() {
        FirebaseAuth.getInstance().signOut();
        Toast.makeText(getContext(), "Account not deleted!!", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(getContext(), LoginActivity.class);
        startActivity(intent);
        getActivity().finish();
    }

}