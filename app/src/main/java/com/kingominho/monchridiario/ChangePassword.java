package com.kingominho.monchridiario;

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


public class ChangePassword extends Fragment {
    private static final String TAG = "ChangePassword";

    EditText editTextCurrentPassword;
    EditText editTextNewPassword;
    EditText editTextConfirmPassword;
    Button buttonChangePassword;
    ProgressBar progressBar;

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
                    changePassword(currentPassword, newPassword);
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
        if (passwordResult == inputValidationUtil.ERROR_CODE_EMPTY_STRING) {
            notifyUser(editTextCurrentPassword, "Current password cannot be empty.");
        } else if (passwordResult == inputValidationUtil.ERROR_CODE_REGEX_NO_MATCH) {
            notifyUser(editTextCurrentPassword, "Invalid password.");
        } else if (newPasswordResult == inputValidationUtil.ERROR_CODE_EMPTY_STRING) {
            notifyUser(editTextNewPassword, "New Password cannot be empty.");
        } else if (newPasswordResult == inputValidationUtil.ERROR_CODE_REGEX_NO_MATCH) {
            notifyUser(editTextNewPassword, inputValidationUtil.ERROR_MESSAGE_INVALID_PASSWORD);
        } else if (confirmNewPassword == inputValidationUtil.ERROR_CODE_EMPTY_STRING) {
            notifyUser(editTextConfirmPassword, "Field cannot be empty.");
        } else if (confirmNewPassword != inputValidationUtil.SUCCESS_CODE_PASSWORDS_MATCH) {
            notifyUser(editTextConfirmPassword, "Passwords does not match.");
        } else {
            return true;
        }
        return false;
    }

    private void changePassword(String currentPassword, final String newPassword) {
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), currentPassword);

        user.reauthenticate(credential)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            user.updatePassword(newPassword)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                notifyUser("Password changed successfully.");
                                                //Toast.makeText(getContext(), "Password changed successfully!.", Toast.LENGTH_SHORT).show();
                                                //progressBar.setVisibility(View.GONE);
                                                //buttonChangePassword.setEnabled(true);
                                                Navigation.findNavController(getView()).navigateUp();
                                            } else {
                                                notifyUser("Something went wrong.");
                                            }
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            notifyUser(e.getLocalizedMessage());
                                            /*Toast.makeText(getContext(), e.getLocalizedMessage(),
                                                    Toast.LENGTH_SHORT).show();
                                            progressBar.setVisibility(View.GONE);
                                            buttonChangePassword.setEnabled(true);*/
                                        }
                                    });
                        } else {
                            //progressBar.setVisibility(View.GONE);
                            //buttonChangePassword.setEnabled(true);
                            //Toast.makeText(getContext(), "Something went wrong.", Toast.LENGTH_SHORT).show();
                            notifyUser("Something went wrong.");
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        if (e instanceof FirebaseAuthInvalidCredentialsException) {
                            notifyUser(editTextCurrentPassword, "Invalid password");
                            //progressBar.setVisibility(View.GONE);
                            //editTextCurrentPassword.setError("Invalid Password!!");
                            //editTextCurrentPassword.requestFocus();
                        } else if (e instanceof FirebaseAuthInvalidUserException) {
                            String errorCode =
                                    ((FirebaseAuthInvalidUserException) e).getErrorCode();
                            Log.d(TAG, "onFailure: " + errorCode, e);
                            notifyUser("Error code: " + errorCode + " " + e.getLocalizedMessage());
                            //progressBar.setVisibility(View.GONE);
                            //buttonChangePassword.setEnabled(true);

                        } else {
                            notifyUser(e.getLocalizedMessage());
                            //Toast.makeText(getContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                            //progressBar.setVisibility(View.GONE);
                            //buttonChangePassword.setEnabled(true);
                        }
                    }
                });
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
