package com.crud.todoapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

public class ForgotPasswordActivity extends AppCompatActivity {

    private Button resetPassButton;
    private Button cancel;
    private EditText userEmail;
    private EditText userResetPassword;
    private EditText userResetConfirmPassword;
    private DatabaseConnection databaseConnection;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        databaseConnection = new DatabaseConnection(this);
        resetPassButton = findViewById(R.id.reset_password_button);
        cancel = findViewById(R.id.cancel);
        userEmail = findViewById(R.id.email_for_update);
        userResetPassword = findViewById(R.id.reset_password);
        userResetConfirmPassword = findViewById(R.id.reset_confirm_password);

        resetPassButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String email = userEmail.getText().toString();
                final String pass = userResetPassword.getText().toString();
                final String rePass = userResetConfirmPassword.getText().toString();

                if (TextUtils.isEmpty(email) || TextUtils.isEmpty(pass) || TextUtils.isEmpty(rePass)){
                    showSnackBar("All Fields are Required");
                } else {
                    final Boolean checkedEmail = databaseConnection.checkUserEmail(email);

                    if (checkedEmail) {
                        databaseConnection.updatePassword(email, pass);
                        showSnackBar("Password Updated Successfully");

                        final Intent intent = new Intent(ForgotPasswordActivity.this, LoginActivity.class);
                        startActivity(intent);
                    } else {
                        showSnackBar("Email does not exist");
                    }
                }
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private void showSnackBar(final String message) {
        Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_SHORT).show();
    }
}