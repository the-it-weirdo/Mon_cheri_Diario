package com.kingominho.monchridiario;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity:";

    private EditText editTextEmail;
    private EditText editTextPassword;
    private Button buttonLogin;
    private Button buttonRegister;

    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        editTextEmail = (EditText) findViewById(R.id.edit_text_email);
        editTextPassword = (EditText) findViewById(R.id.edit_text_password);
        buttonLogin = (Button) findViewById(R.id.button_login);
        buttonRegister = (Button) findViewById(R.id.button_register);

        mAuth = FirebaseAuth.getInstance();

        //TODO: add progress bar
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateInputs()) {
                    loginUser(editTextEmail.getText().toString(), editTextPassword.getText().toString());
                }
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
        return true;
    }

    private void loginUser(String email, String password)
    {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful())
                        {
                            Log.d(LoginActivity.TAG, "User logged in.");
                            Intent intent = new Intent(LoginActivity.this, DashboardActivity.class);
                            startActivity(intent);
                            finish();
                        }
                        else
                        {
                            Toast.makeText(LoginActivity.this, "Login Failed!!", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        if (e instanceof FirebaseAuthInvalidCredentialsException) {
                            //notifyUser("Invalid password");
                            editTextPassword.setError("Invalid Password!!");
                            editTextPassword.requestFocus();
                        } else if (e instanceof FirebaseAuthInvalidUserException) {
                            String errorCode =
                                    ((FirebaseAuthInvalidUserException) e).getErrorCode();

                            if (errorCode.equals("ERROR_USER_NOT_FOUND")) {
                                //notifyUser("No matching account found");
                                editTextEmail.setError("No matching account found. Consider registering.");
                                editTextEmail.requestFocus();
                            } else if (errorCode.equals("ERROR_USER_DISABLED")) {
                                //notifyUser("User account has been disabled");
                                editTextEmail.setError("User account has been disabled");
                                editTextEmail.requestFocus();
                            } else {
                                //notifyUser(e.getLocalizedMessage());
                                Toast.makeText(LoginActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(LoginActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

}
