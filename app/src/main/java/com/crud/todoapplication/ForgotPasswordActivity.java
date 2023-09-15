package com.crud.todoapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.crud.todoapplication.api.AuthenticationService;
import com.google.android.material.snackbar.Snackbar;

public class ForgotPasswordActivity extends AppCompatActivity {

    private Button resetPassButton;
    private Button cancel;
    private EditText userEmail;
    private EditText userResetPassword;
    private EditText userResetConfirmPassword;
    private EditText oldHint;
    private EditText newHint;
    private ImageView visiblePassword;
    private ImageView visibleConfirmPassword;
    private boolean isPasswordVisibility;
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
        visiblePassword = findViewById(R.id.visible_password1);
        visibleConfirmPassword = findViewById(R.id.visible_confirm_password);
        oldHint = findViewById(R.id.email_for_old_hint);
        newHint = findViewById(R.id.email_for_new_hint);

        resetPassButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String email = userEmail.getText().toString();
                final String pass = userResetPassword.getText().toString();
                final String rePass = userResetConfirmPassword.getText().toString();
                final String oldHintForPassword = oldHint.getText().toString();
                final String newHintForPassword = newHint.getText().toString();

                final String hashPassword = MD5Helper.md5(pass);
                if (TextUtils.isEmpty(email) || TextUtils.isEmpty(pass) || TextUtils.isEmpty(rePass) || TextUtils.isEmpty(oldHintForPassword) || TextUtils.isEmpty(newHintForPassword)) {
                    showSnackBar("All Fields are Required");
                } else {
                    if (!pass.equals(rePass)) {
                        showSnackBar("Password Mismatch");
                    } else {
                        final AuthenticationService authenticationService = new AuthenticationService("http://192.168.1.29:8080/");
                        authenticationService.resetPassword(email, hashPassword, oldHintForPassword, newHintForPassword, new AuthenticationService.ApiResponseCallBack() {
                            @Override
                            public void onSuccess(String response) {
                                showSnackBar("Updated Successful");
                                new Handler().postDelayed(() -> {
                                    final Intent intent = new Intent(ForgotPasswordActivity.this, LoginActivity.class);
                                    startActivity(intent);
                                    finish();
                                },1000);
                            }

                            @Override
                            public void onFailure(String response) {
                                showSnackBar("Update Failed");
                            }
                        });
                    }
                }
            }
        });

        visiblePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                togglePasswordActivity(userResetPassword, visiblePassword);
            }
        });
        visibleConfirmPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                togglePasswordActivity(userResetConfirmPassword, visibleConfirmPassword);
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

    private void togglePasswordActivity(final EditText password, final ImageView passwordVisibility) {
        if (isPasswordVisibility) {
            password.setInputType(InputType.TYPE_CLASS_TEXT |
                    InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            isPasswordVisibility = false;

            passwordVisibility.setImageResource(R.drawable.visibiltiy);
        } else {
            password.setInputType(InputType.TYPE_CLASS_TEXT |
                    InputType.TYPE_TEXT_VARIATION_PASSWORD);
            isPasswordVisibility = true;

            passwordVisibility.setImageResource(R.drawable.baseline_visibility_off_24);
        }
        password.setSelection(password.getText().length());
    }
}