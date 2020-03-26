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
        return true;
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
                            progressBar.setVisibility(View.GONE);

                            Toast.makeText(getApplicationContext(), "Registration failed!!", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
