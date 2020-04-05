package com.kingominho.monchridiario.ui.tools;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.kingominho.monchridiario.AccountManager;
import com.kingominho.monchridiario.CategoryManager;
import com.kingominho.monchridiario.ChooseProfilePicture;
import com.kingominho.monchridiario.DailyEntry;
import com.kingominho.monchridiario.DailyEntryManager;
import com.kingominho.monchridiario.LoginActivity;
import com.kingominho.monchridiario.R;
import com.squareup.picasso.Picasso;

import static android.app.Activity.RESULT_OK;

public class ToolsFragment extends Fragment {

    private final int PICK_IMAGE_REQUEST = 1;
    private final static String TAG = "ToolsFragment";

    private ToolsViewModel toolsViewModel;

    Button changeProfilePictureButton, changePasswordButton,
            manageCategoriesButton, deleteAccountButton;


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

        changeProfilePictureButton = view.findViewById(R.id.change_profile_picture_button);
        changeProfilePictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeProfilePictureButtonClicked(v);
            }
        });

        changePasswordButton = view.findViewById(R.id.change_password_button);
        changePasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changePasswordButtonClicked(v);
            }
        });

        manageCategoriesButton = view.findViewById(R.id.manage_categories_button);
        manageCategoriesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                manageCategoriesButtonClicked(v);
            }
        });

        deleteAccountButton = view.findViewById(R.id.delete_account_button);
        deleteAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteAccountButtonClicked();
            }
        });
    }

    private void changeProfilePictureButtonClicked(View view) {
        changeProfilePictureButton.setEnabled(false);
        //Navigation.findNavController(view).navigate(R.id.chooseProfilePicture);
        Intent intent = new Intent(getActivity(), ChooseProfilePicture.class);
        startActivity(intent);
        getActivity().finish();
    }

    @Override
    public void onResume() {
        super.onResume();
        changeProfilePictureButton.setEnabled(true);
    }

    private void changePasswordButtonClicked(View view) {
        //Toast.makeText(getContext(), "Password changed!!", Toast.LENGTH_SHORT).show();
        Navigation.findNavController(view).navigate(R.id.changePassword);
    }


    private void manageCategoriesButtonClicked(View view) {
        //Toast.makeText(getContext(), "Categories Managed!!", Toast.LENGTH_SHORT).show();
        Navigation.findNavController(view).navigate(R.id.manageCategory);
    }


    private void deleteAccountButtonClicked() {
        manageCategoriesButton.setEnabled(false);
        changeProfilePictureButton.setEnabled(false);
        changePasswordButton.setEnabled(false);
        deleteAccountButton.setEnabled(false);

        AccountManager.getInstance().deleteAccount(FirebaseAuth.getInstance().getCurrentUser());
        Toast.makeText(getContext(), "User Account and data deleted.", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(getContext(), LoginActivity.class);
        startActivity(intent);
        getActivity().finish();
    }
}