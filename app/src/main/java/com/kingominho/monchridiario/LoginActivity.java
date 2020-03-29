package com.kingominho.monchridiario;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity:";

    private EditText editTextEmail;
    private EditText editTextPassword;
    private Button buttonLogin;
    private Button buttonRegister;
    private Button buttonResetPassword;
    private ProgressBar progressBar;

    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        editTextEmail = (EditText) findViewById(R.id.edit_text_email);
        editTextPassword = (EditText) findViewById(R.id.edit_text_password);
        buttonLogin = (Button) findViewById(R.id.button_login);
        buttonResetPassword = (Button) findViewById(R.id.button_reset_password);
        buttonRegister = (Button) findViewById(R.id.button_register);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);

        mAuth = FirebaseAuth.getInstance();

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                buttonLogin.setEnabled(false);
                buttonResetPassword.setEnabled(false);
                buttonRegister.setEnabled(false);
                editTextEmail.setEnabled(false);
                editTextPassword.setEnabled(false);
                if (validateInputs()) {
                    loginUser(editTextEmail.getText().toString(), editTextPassword.getText().toString());
                }
            }
        });

        buttonResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonLogin.setEnabled(false);
                buttonResetPassword.setEnabled(false);
                buttonRegister.setEnabled(false);
                editTextEmail.setEnabled(false);
                editTextPassword.setEnabled(false);
                showResetPasswordDialog();
            }
        });

        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegistrationActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }

    private boolean validateInputs() {
        InputValidationUtil inputValidationUtil = new InputValidationUtil();
        int emailResult = inputValidationUtil.validateEmail(editTextEmail.getText().toString());
        int passResult = inputValidationUtil.validatePassword(editTextPassword.getText().toString());
        if (emailResult == inputValidationUtil.ERROR_CODE_EMPTY_STRING) {
            notifyUser(editTextEmail, "Email cannot be empty!");
            //editTextEmail.setError("Email cannot be empty!");
            //editTextEmail.requestFocus();
            return false;
        } else if (emailResult == inputValidationUtil.ERROR_CODE_REGEX_NO_MATCH) {
            notifyUser(editTextEmail, "Invalid Email !");
            //editTextEmail.setError("Invalid email!!");
            //editTextEmail.requestFocus();
            return false;
        } else if (passResult == inputValidationUtil.ERROR_CODE_EMPTY_STRING) {
            notifyUser(editTextPassword, "Password cannot be empty!");
            //editTextPassword.setError("Password cannot be empty!!");
            //editTextPassword.requestFocus();
            return false;
        } else if (passResult == inputValidationUtil.ERROR_CODE_REGEX_NO_MATCH) {
            notifyUser(editTextPassword, "Invalid password.");
            //editTextPassword.setError(inputValidationUtil.ERROR_MESSAGE_INVALID_PASSWORD);
            //editTextPassword.requestFocus();
            return false;
        } else {
            return true;
        }
    }

    private void loginUser(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(LoginActivity.TAG, "User logged in.");
                            Intent intent = new Intent(LoginActivity.this, DashboardActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            //Toast.makeText(LoginActivity.this, "Login Failed!!", Toast.LENGTH_SHORT).show();
                            notifyUser("Login Failed !!");
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        if (e instanceof FirebaseAuthInvalidCredentialsException) {
                            notifyUser(editTextPassword, "Invalid email or password");
                            //editTextPassword.setError("Invalid Password!!");
                            //editTextPassword.requestFocus();
                        } else if (e instanceof FirebaseAuthInvalidUserException) {
                            String errorCode =
                                    ((FirebaseAuthInvalidUserException) e).getErrorCode();
                            if (errorCode.equals("ERROR_USER_NOT_FOUND")) {
                                notifyUser(editTextEmail, "No matching account found. Consider registering.");
                                //editTextEmail.setError("No matching account found. Consider registering.");
                                //editTextEmail.requestFocus();
                            } else if (errorCode.equals("ERROR_USER_DISABLED")) {
                                notifyUser(editTextEmail, "User account has been disabled. Contact Developer.");
                                //editTextEmail.setError("User account has been disabled. Contact Developer.");
                                //editTextEmail.requestFocus();
                            } else {
                                notifyUser(e.getLocalizedMessage());
                                //Toast.makeText(LoginActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            notifyUser(e.getLocalizedMessage());
                            //Toast.makeText(LoginActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void showResetPasswordDialog() {
        final Dialog dialog = new Dialog(LoginActivity.this);
        dialog.setContentView(R.layout.single_edit_text_dialog_layout);
        dialog.setTitle("Reset Password");

        final EditText editTextEmail = dialog.findViewById(R.id.edit_text);
        editTextEmail.setHint("Enter email");
        Button okayButton = dialog.findViewById(R.id.positive_button);
        Button dismissButton = dialog.findViewById(R.id.dismiss_dialog_button);

        okayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String s = editTextEmail.getText().toString();
                InputValidationUtil inputValidationUtil = new InputValidationUtil();
                int emailResult = inputValidationUtil.validateEmail(s);
                if (emailResult == inputValidationUtil.SUCCESS_CODE_REGEX_MATCH) {
                    dialog.dismiss();
                    progressBar.setVisibility(View.VISIBLE);
                    resetPassword(s);
                } else {
                    editTextEmail.setError("Invalid email.");
                    editTextEmail.requestFocus();
                }
            }
        });

        dismissButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                notifyUser("Cancelled.");
            }
        });

        dialog.show();
    }

    private void resetPassword(String email) {
        mAuth.sendPasswordResetEmail(email)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        notifyUser("Email sent.");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        if (e instanceof FirebaseAuthInvalidUserException) {
                            String errorCode =
                                    ((FirebaseAuthInvalidUserException) e).getErrorCode();
                            if (errorCode.equals("ERROR_USER_NOT_FOUND")) {
                                notifyUser("No matching account found. Consider registering.");
                            } else if (errorCode.equals("ERROR_USER_DISABLED")) {
                                notifyUser("User account has been disabled. Contact Developer.");
                            } else {
                                notifyUser(e.getLocalizedMessage());
                            }
                        }
                    }
                });
    }

    private void notifyUser(EditText editText, String message) {
        editText.setError(message);
        editText.requestFocus();
        progressBar.setVisibility(View.GONE);
        buttonLogin.setEnabled(true);
        buttonRegister.setEnabled(true);
        editTextEmail.setEnabled(true);
        editTextPassword.setEnabled(true);
        buttonResetPassword.setEnabled(true);
    }

    private void notifyUser(String message) {
        Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
        progressBar.setVisibility(View.GONE);
        buttonLogin.setEnabled(true);
        buttonRegister.setEnabled(true);
        editTextEmail.setEnabled(true);
        editTextPassword.setEnabled(true);
        buttonResetPassword.setEnabled(true);
    }
}
