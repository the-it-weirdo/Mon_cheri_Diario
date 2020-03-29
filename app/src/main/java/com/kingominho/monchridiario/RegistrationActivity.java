package com.kingominho.monchridiario;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class RegistrationActivity extends AppCompatActivity {

    private static final String TAG = "RegistrationActivity:";

    private EditText editTextEmail;
    private EditText editTextName;
    private EditText editTextPassword;
    private EditText editTextConfirmPassword;
    private Button registerButton;
    private Button loginButton;
    private ProgressBar progressBar;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        editTextEmail = (EditText) findViewById(R.id.edit_text_email);
        editTextName = (EditText) findViewById(R.id.edit_text_name);
        editTextPassword = (EditText) findViewById(R.id.edit_text_password);
        editTextConfirmPassword = (EditText) findViewById(R.id.edit_text_confirm_password);
        registerButton = (Button) findViewById(R.id.register_button);
        loginButton = (Button) findViewById(R.id.login_button);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        mAuth = FirebaseAuth.getInstance();


        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editTextEmail.setEnabled(false);
                editTextPassword.setEnabled(false);
                editTextName.setEnabled(false);
                editTextConfirmPassword.setEnabled(false);
                registerButton.setEnabled(false);
                loginButton.setEnabled(false);
                progressBar.setVisibility(View.VISIBLE);
                if (validateInputs()) {
                    registerUser(editTextEmail.getText().toString(), editTextPassword.getText().toString()
                            , editTextName.getText().toString());
                }
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegistrationActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    boolean validateInputs() {
        InputValidationUtil inputValidationUtil = new InputValidationUtil();
        int nameResult = inputValidationUtil.validateName(editTextName.getText().toString());
        int emailResult = inputValidationUtil.validateEmail(editTextEmail.getText().toString());
        int passwordResult = inputValidationUtil.validatePassword(editTextPassword.getText().toString());
        int confirmPasswordResult = inputValidationUtil.confirmPassword(editTextPassword.getText().toString(),
                editTextConfirmPassword.getText().toString());
        if (nameResult == inputValidationUtil.ERROR_CODE_EMPTY_STRING) {
            notifyUser(editTextName, "Name cannot be empty.");
        } else if (nameResult == inputValidationUtil.ERROR_CODE_REGEX_NO_MATCH) {
            notifyUser(editTextName, "Name cannot contain symbols or numbers.");
        } else if (emailResult == inputValidationUtil.ERROR_CODE_EMPTY_STRING) {
            notifyUser(editTextEmail, "Email cannot be empty.");
        } else if (emailResult == inputValidationUtil.ERROR_CODE_REGEX_NO_MATCH) {
            notifyUser(editTextEmail, "Invalid email.");
        } else if (passwordResult == inputValidationUtil.ERROR_CODE_EMPTY_STRING) {
            notifyUser(editTextPassword, "Password cannot be empty.");
        } else if (passwordResult == inputValidationUtil.ERROR_CODE_REGEX_NO_MATCH) {
            notifyUser(editTextPassword, inputValidationUtil.ERROR_MESSAGE_INVALID_PASSWORD);
        } else if (confirmPasswordResult == inputValidationUtil.ERROR_CODE_EMPTY_STRING) {
            notifyUser(editTextConfirmPassword, "Field cannot be empty.");
        } else if (confirmPasswordResult != inputValidationUtil.SUCCESS_CODE_PASSWORDS_MATCH) {
            notifyUser(editTextConfirmPassword, "Passwords does not match.");
        } else {
            return true;
        }
        return false;
    }

    private void registerUser(final String email, final String password, final String displayName) {
        progressBar.setVisibility(View.VISIBLE);

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null) {
                                UserProfileChangeRequest userProfileChangeRequest = new UserProfileChangeRequest.Builder()
                                        .setDisplayName(displayName).build();
                                user.updateProfile(userProfileChangeRequest)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Log.d(RegistrationActivity.TAG, "Name Set Successfully");
                                                    progressBar.setVisibility(View.GONE);
                                                    Toast.makeText(getApplicationContext(), "Registration Successful.",
                                                            Toast.LENGTH_SHORT).show();
                                                } else {
                                                    Log.d(RegistrationActivity.TAG, "Name couldn't be set!!");
                                                }
                                                Intent intent = new Intent(RegistrationActivity.this,
                                                        ChooseProfilePicture.class);
                                                startActivity(intent);
                                                finish();
                                            }
                                        });
                            }
                        } else {
                            if(task.getException() instanceof FirebaseAuthUserCollisionException) {
                                notifyUser(editTextEmail, "Email already in use by another account. Login please.");
                            }
                            //progressBar.setVisibility(View.GONE);
                            //Toast.makeText(getApplicationContext(), "Registration failed!!", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //Toast.makeText(getApplicationContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        notifyUser(e.getLocalizedMessage());
                    }
                });
    }

    private void notifyUser(EditText editText, String message) {
        editText.setError(message);
        editText.requestFocus();
        editTextEmail.setEnabled(true);
        editTextPassword.setEnabled(true);
        editTextName.setEnabled(true);
        editTextConfirmPassword.setEnabled(true);
        registerButton.setEnabled(true);
        loginButton.setEnabled(true);
        progressBar.setVisibility(View.GONE);
    }

    private void notifyUser(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
        editTextEmail.setEnabled(true);
        editTextPassword.setEnabled(true);
        editTextName.setEnabled(true);
        editTextConfirmPassword.setEnabled(true);
        registerButton.setEnabled(true);
        loginButton.setEnabled(true);
        progressBar.setVisibility(View.GONE);
    }
}
