package com.kingominho.monchridiario;

import android.content.Context;
import android.net.Uri;
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
import com.google.firebase.auth.EmailAuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;


public class ChangePassword extends Fragment {
    private static final String TAG = "ChangePassword";

    EditText editTextCurrentPassword;
    EditText editTextNewPassword;
    EditText editTextconfirmPassword;
    Button buttonChangePassword;
    ProgressBar progressBar;

    @Nullable
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_change_password, container, false);
        editTextCurrentPassword = root.findViewById(R.id.edit_text_password);
        editTextNewPassword = root.findViewById(R.id.edit_text_new_password);
        editTextconfirmPassword = root.findViewById(R.id.edit_text_confirm_new_password);
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
                buttonChangePassword.setEnabled(false);
                String currentPassword = editTextCurrentPassword.getText().toString();
                final String newPassword = editTextNewPassword.getText().toString();
                String confirmNewPassword = editTextconfirmPassword.getText().toString();
                //TODO: validate inputs
                if (validateInputs()) {
                    changePassword(currentPassword, newPassword);
                } else {
                    progressBar.setVisibility(View.GONE);
                    buttonChangePassword.setEnabled(true);
                }
            }
        });
    }

    private boolean validateInputs() {
        return true;
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
                                                Toast.makeText(getContext(), "Password changed successfully!.",
                                                        Toast.LENGTH_SHORT).show();
                                                progressBar.setVisibility(View.GONE);
                                                buttonChangePassword.setEnabled(true);
                                                Navigation.findNavController(getView()).navigateUp();
                                            } else {
                                                Toast.makeText(getContext(), "Something went wrong.",
                                                        Toast.LENGTH_SHORT).show();
                                                progressBar.setVisibility(View.GONE);
                                                buttonChangePassword.setEnabled(true);
                                            }
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(getContext(), e.getLocalizedMessage(),
                                                    Toast.LENGTH_SHORT).show();
                                            progressBar.setVisibility(View.GONE);
                                            buttonChangePassword.setEnabled(true);
                                        }
                                    });
                        } else {
                            progressBar.setVisibility(View.GONE);
                            buttonChangePassword.setEnabled(true);
                            Toast.makeText(getContext(), "Something went wrong.", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        if (e instanceof FirebaseAuthInvalidCredentialsException) {
                            //notifyUser("Invalid password");
                            progressBar.setVisibility(View.GONE);
                            editTextCurrentPassword.setError("Invalid Password!!");
                            editTextCurrentPassword.requestFocus();
                        } else if (e instanceof FirebaseAuthInvalidUserException) {
                            String errorCode =
                                    ((FirebaseAuthInvalidUserException) e).getErrorCode();
                            Log.d(TAG, "onFailure: " + errorCode, e);
                            progressBar.setVisibility(View.GONE);
                            buttonChangePassword.setEnabled(true);

                        } else {
                            Toast.makeText(getContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                            buttonChangePassword.setEnabled(true);
                        }
                    }
                });
    }
}
