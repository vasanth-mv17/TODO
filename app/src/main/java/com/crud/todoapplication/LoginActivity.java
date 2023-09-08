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

import com.google.android.material.snackbar.Snackbar;

public class LoginActivity extends AppCompatActivity {

    private EditText userEmail;
    private EditText userPassword;
    private DatabaseConnection databaseConnection;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        databaseConnection = new DatabaseConnection(this);
        final TextView signUp = findViewById(R.id.signUp);
        final TextView forgotPassword = findViewById(R.id.textView4);
        final Button signIn = findViewById(R.id.signIn);
        userEmail = findViewById(R.id.login_email);
        userPassword = findViewById(R.id.login_pass);
         signUp.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 final Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                 startActivity(intent);
             }
         });

         forgotPassword.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 final Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
                 startActivity(intent);
             }
         });
         signIn.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 final String email = userEmail.getText().toString();
                 final String pass = userPassword.getText().toString();

                 if (TextUtils.isEmpty(email) || TextUtils.isEmpty(pass)){
                     showSnackBar("All Fields are Required");
                 } else {
                     final Boolean checkUserPass = databaseConnection.checkUserNameAndPassword(email, pass);

                     if (checkUserPass) {
                         showSnackBar("Login Successfully");
                         final Intent intent = new Intent(LoginActivity.this, MenuActivity2.class);
                         startActivity(intent);
                     } else {
                         showSnackBar("Login Failed");
                     }
                 }
             }
         });
    }

    private void showSnackBar(final String message) {
        Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_SHORT).show();
    }
}