package com.kingominho.monchridiario.ui.changePassword;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.kingominho.monchridiario.R;
import com.kingominho.monchridiario.manager.AccountManager;
import com.kingominho.monchridiario.manager.InputValidationUtil;


public class ChangePassword extends Fragment {
    private static final String TAG = "ChangePassword";

    EditText editTextCurrentPassword;
    EditText editTextNewPassword;
    EditText editTextConfirmPassword;
    Button buttonChangePassword;
    ProgressBar progressBar;

    AccountManager accountManager;

    @Nullable
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_change_password, container, false);
        editTextCurrentPassword = root.findViewById(R.id.edit_text_password);
        editTextNewPassword = root.findViewById(R.id.edit_text_new_password);
        editTextConfirmPassword = root.findViewById(R.id.edit_text_confirm_new_password);
        buttonChangePassword = root.findViewById(R.id.button_change_password);
        progressBar = root.findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.GONE);
        accountManager = AccountManager.getInstance();
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        buttonChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                editTextCurrentPassword.setEnabled(false);
                editTextNewPassword.setEnabled(false);
                editTextConfirmPassword.setEnabled(false);
                buttonChangePassword.setEnabled(false);
                String currentPassword = editTextCurrentPassword.getText().toString();
                String newPassword = editTextNewPassword.getText().toString();
                if (validateInputs()) {
                    accountManager.changePassword(currentPassword, newPassword);
                }
            }
        });

        accountManager.setAccountManagerTaskListener(new AccountManager.AccountManagerTaskListener() {
            @Override
            public void OnFailure(Exception e) {
                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    notifyUser(editTextCurrentPassword, "Invalid password");
                } else if (e instanceof FirebaseAuthInvalidUserException) {
                    String errorCode =
                            ((FirebaseAuthInvalidUserException) e).getErrorCode();
                    Log.d(TAG, "onFailure: " + errorCode, e);
                    notifyUser("Error code: " + errorCode + " " + e.getLocalizedMessage());

                } else {
                    notifyUser(e.getLocalizedMessage());
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
                notifyUser("Something went wrong: " + task.getException().getLocalizedMessage());
            }

            @Override
            public void OnTaskSuccessful(Task task, int task_id) {
                switch (task_id) {
                    case AccountManager.TASK_ID_CHANGE_PASSWORD: {
                        notifyUser("Password changed successfully.");
                        Navigation.findNavController(getView()).navigateUp();
                        break;
                    }
                    case AccountManager.TASK_ID_REAUTHENTICATE_USER: {
                        Log.d(TAG, "OnTaskSuccessful: User Re-authenticated.");
                        break;
                    }
                }

            }
        });
    }

    private boolean validateInputs() {
        InputValidationUtil inputValidationUtil = new InputValidationUtil();
        int passwordResult = inputValidationUtil.validatePassword(editTextCurrentPassword.getText().toString());
        int newPasswordResult = inputValidationUtil.validatePassword(editTextNewPassword.getText().toString());
        int confirmNewPassword = inputValidationUtil.confirmPassword(editTextNewPassword.getText().toString(),
                editTextConfirmPassword.getText().toString());
        if (passwordResult == InputValidationUtil.ERROR_CODE_EMPTY_STRING) {
            notifyUser(editTextCurrentPassword, "Current password cannot be empty.");
        } else if (passwordResult == InputValidationUtil.ERROR_CODE_REGEX_NO_MATCH) {
            notifyUser(editTextCurrentPassword, "Invalid password.");
        } else if (newPasswordResult == InputValidationUtil.ERROR_CODE_EMPTY_STRING) {
            notifyUser(editTextNewPassword, "New Password cannot be empty.");
        } else if (newPasswordResult == InputValidationUtil.ERROR_CODE_REGEX_NO_MATCH) {
            notifyUser(editTextNewPassword, InputValidationUtil.ERROR_MESSAGE_INVALID_PASSWORD);
        } else if (confirmNewPassword == InputValidationUtil.ERROR_CODE_EMPTY_STRING) {
            notifyUser(editTextConfirmPassword, "Field cannot be empty.");
        } else if (confirmNewPassword != InputValidationUtil.SUCCESS_CODE_PASSWORDS_MATCH) {
            notifyUser(editTextConfirmPassword, "Passwords does not match.");
        } else {
            return true;
        }
        return false;
    }

    private void notifyUser(EditText editText, String message) {
        editText.setError(message);
        editText.requestFocus();
        progressBar.setVisibility(View.GONE);
        editTextCurrentPassword.setEnabled(true);
        editTextNewPassword.setEnabled(true);
        editTextConfirmPassword.setEnabled(true);
        buttonChangePassword.setEnabled(true);
    }

    private void notifyUser(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        progressBar.setVisibility(View.GONE);
        editTextCurrentPassword.setEnabled(true);
        editTextNewPassword.setEnabled(true);
        editTextConfirmPassword.setEnabled(true);
        buttonChangePassword.setEnabled(true);
    }
}
