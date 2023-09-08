package com.crud.todoapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.crud.todoapplication.model.Credentials;
import com.google.android.material.snackbar.Snackbar;

public class SignUpActivity extends AppCompatActivity {

    private Button signUp;
    private TextView alreadyAccount;
    private EditText userName;
    private EditText email;
    private EditText password;
    private EditText rePassword;
    private Credentials userCredentials;
    private DatabaseConnection databaseConnection;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        alreadyAccount = findViewById(R.id.already_account);
        userName = findViewById(R.id.sign_up_name);
        email = findViewById(R.id.sign_up_email);
        password = findViewById(R.id.sign_up_pass);
        rePassword = findViewById(R.id.sign_up_repass);
        signUp = findViewById(R.id.register_button);
        userCredentials = new Credentials();
        databaseConnection = new DatabaseConnection(this);
        alreadyAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userCredentials.setName(userName.getText().toString());
                userCredentials.setEmail(email.getText().toString());
                userCredentials.setPassword(password.getText().toString());
                userCredentials.setConfirmPassword(rePassword.getText().toString());

                if (TextUtils.isEmpty(userCredentials.getName()) || TextUtils.isEmpty(userCredentials.getEmail()) || TextUtils.isEmpty(userCredentials.getPassword()) || TextUtils.isEmpty(userCredentials.getConfirmPassword())) {
                    showSnackBar("All Fields are Required");
                } else {
                    if (userCredentials.getPassword().equals(userCredentials.getConfirmPassword())) {
                        final Boolean checkUser = databaseConnection.checkUserName(userCredentials);
                        final Boolean checkEmail = databaseConnection.checkUserSignUpEmail(userCredentials);

                        if (!checkUser && !checkEmail) {
                            final Boolean insert = databaseConnection.insertData(userCredentials);

                            if (insert) {
                                showSnackBar("Registered Successfully");
                                final Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                                startActivity(intent);
                            } else {
                                showSnackBar("Registration Failed");
                            }
                        } else {
                            showSnackBar("User already exists");
                        }
                    } else {
                        showSnackBar("Password mismatch");
                    }
                }
            }
        });
    }

    private void showSnackBar(final String message) {
        Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_SHORT).show();
    }
}