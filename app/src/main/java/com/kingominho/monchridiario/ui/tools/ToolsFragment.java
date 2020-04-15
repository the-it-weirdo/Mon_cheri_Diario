package com.kingominho.monchridiario.ui.tools;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.kingominho.monchridiario.manager.AccountManager;
import com.kingominho.monchridiario.activity.ChooseProfilePicture;
import com.kingominho.monchridiario.activity.LoginActivity;
import com.kingominho.monchridiario.R;
import com.kingominho.monchridiario.manager.InputValidationUtil;
import com.squareup.picasso.Picasso;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;

public class ToolsFragment extends Fragment {

    private final int PICK_IMAGE_REQUEST = 1;
    private final static String TAG = "ToolsFragment";

    private ToolsViewModel toolsViewModel;

    Button changeProfilePictureButton, changePasswordButton,
            manageCategoriesButton, deleteAccountButton;
    ProgressBar progressBar;

    AccountManager accountManager;

    DialogInterface.OnDismissListener dialogDismissListener;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        toolsViewModel =
                new ViewModelProvider(this).get(ToolsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_tools, container, false);

        /*final TextView textView = root.findViewById(R.id.text_tools);
        toolsViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });*/
        accountManager = AccountManager.getInstance();
        return root;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ImageView profilePictureImageView = view.findViewById(R.id.profile_image_view);
        Picasso.with(getContext())
                .load(toolsViewModel.getmProfilePicUrl())
                .placeholder(view.getResources().getDrawable(R.drawable.ic_person_black_24dp))
                .transform(new CropCircleTransformation())
                .into(profilePictureImageView);

        progressBar = view.findViewById(R.id.progress_bar);

        TextView displayNameTextView = view.findViewById(R.id.display_name_text_view);
        displayNameTextView.setText(toolsViewModel.getmDisplayName());

        TextView emailTextView = view.findViewById(R.id.email_text_view);
        emailTextView.setText(toolsViewModel.getmEmail());

        changeProfilePictureButton = view.findViewById(R.id.change_profile_picture_button);
        changeProfilePictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeProfilePictureButtonClicked();
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

        accountManager.setAccountManagerTaskListener(new AccountManager.AccountManagerTaskListener() {
            @Override
            public void OnFailure(Exception e) {
                progressBar.setVisibility(View.GONE);
                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    updateUI("Invalid password", true);
                } else if (e instanceof FirebaseAuthInvalidUserException) {
                    String errorCode =
                            ((FirebaseAuthInvalidUserException) e).getErrorCode();
                    Log.d(TAG, "onFailure: " + errorCode, e);
                    updateUI("Error code: " + errorCode + " " + e.getLocalizedMessage(), true);

                } else {
                    updateUI(e.getLocalizedMessage(), true);
                }
            }

            @Override
            public void OnSuccess(String message) {

            }

            @Override
            public void OnComplete(Task task) {

            }

            @Override
            public void OnTaskNotSuccessful(Task task, int task_id) {
                progressBar.setVisibility(View.GONE);
                updateUI(task.getException().getLocalizedMessage(), true);
            }

            @Override
            public void OnTaskSuccessful(Task task, int task_id) {
                switch (task_id) {
                    case AccountManager.TASK_ID_REAUTHENTICATE_USER: {
                        accountManager.deleteAccount(FirebaseAuth.getInstance().getCurrentUser());
                        break;
                    }
                    case AccountManager.TASK_ID_DELETE_ACCOUNT: {
                        Log.d(TAG, "deleteAccount OnTaskSuccessful: Account Deleted.");
                        Toast.makeText(getContext(), "User Account and data deleted.", Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                        Intent intent = new Intent(getContext(), LoginActivity.class);
                        startActivity(intent);
                        getActivity().finish();
                        break;
                    }
                    default:
                        break;
                }

            }
        });

        dialogDismissListener = new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                onDialogDismissed();
            }
        };
    }

    private void changeProfilePictureButtonClicked() {
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

    private void updateUI(boolean isEnabled) {
        manageCategoriesButton.setEnabled(isEnabled);
        changeProfilePictureButton.setEnabled(isEnabled);
        changePasswordButton.setEnabled(isEnabled);
        deleteAccountButton.setEnabled(isEnabled);
    }

    private void updateUI(String message, boolean isEnabled) {
        updateUI(isEnabled);
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void deleteAccountButtonClicked() {
        //updateUI(false);
        showReEnterPasswordDialog();
    }

    private void showReEnterPasswordDialog() {
        final Dialog dialog = new Dialog(getActivity());
        dialog.setOnDismissListener(dialogDismissListener);
        dialog.setContentView(R.layout.enter_password_dialog);
        dialog.setTitle("Are you sure? Enter your password to continue..");

        final EditText editText = dialog.findViewById(R.id.edit_text);
        editText.setHint("Enter your password");
        Button okayButton = dialog.findViewById(R.id.positive_button);
        okayButton.setText("Confirm");
        Button dismissButton = dialog.findViewById(R.id.dismiss_dialog_button);

        okayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String s = editText.getText().toString();
                InputValidationUtil util = new InputValidationUtil();
                int resultCode = util.validatePassword(s);
                switch (resultCode) {
                    case InputValidationUtil.ERROR_CODE_EMPTY_STRING: {
                        editText.setError("Cannot be empty!");
                        editText.requestFocus();
                        break;
                    }
                    case InputValidationUtil.ERROR_CODE_REGEX_NO_MATCH: {
                        editText.setError("Invalid password.");
                        editText.requestFocus();
                        break;
                    }
                    case InputValidationUtil.SUCCESS_CODE_REGEX_MATCH: {
                        dialog.dismiss();
                        progressBar.setVisibility(View.VISIBLE);
                        updateUI(false);
                        accountManager.reAuthenticateUser(FirebaseAuth.getInstance().getCurrentUser().getEmail(),
                                s);
                        break;
                    }
                    default:
                        dialog.dismiss();
                }
            }
        });

        dismissButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                //updateUI(true);
            }
        });

        dialog.show();
    }

    private void onDialogDismissed() {
        updateUI(true);
    }
}