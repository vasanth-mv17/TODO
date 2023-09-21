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
import android.widget.TextView;

import com.crud.todoapplication.api.AuthenticationService;
import com.crud.todoapplication.model.User;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {

    private EditText userEmail;
    private EditText userPassword;
    private ImageView passwordVisibility;
    private boolean isPasswordVisible;
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
        passwordVisibility = findViewById(R.id.visible_password);
        final User user = databaseConnection.getUserProfile();
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });

        passwordVisibility.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                togglePasswordActivity(userPassword, passwordVisibility);
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

                if (TextUtils.isEmpty(email) || TextUtils.isEmpty(pass)) {
                    showSnackBar(String.valueOf(R.string.All_fields_are_required));
                } else {
                   // final String hashPassword = MD5Helper.md5(pass);
                    final AuthenticationService authenticationService = new AuthenticationService("http://192.168.1.109:8080/");
                    authenticationService.login(email, pass, new AuthenticationService.ApiResponseCallBack() {
                        @Override
                        public void onSuccess(String response) {
                            showSnackBar(String.valueOf(R.string.Login_success));

                            try {
                                final JSONObject jsonObject = new JSONObject(response);
                                final JSONObject data = jsonObject.getJSONObject(getString(R.string.data));
                                final String token = data.getString(getString(R.string.token));


                                new Handler().postDelayed(() -> {
                                    final Intent intent = new Intent(LoginActivity.this, MenuActivity2.class);
                                    intent.putExtra(getString(R.string.token), token);

                                    startActivity(intent);
                                    finish();
                                }, 100);
                            } catch (JSONException e) {
                                throw new RuntimeException(e);
                            }
                        }

                        @Override
                        public void onFailure(String response) {
                            showSnackBar(getString(R.string.login_failed));
                        }
                    });
                }
            }
        });
    }

    private void showSnackBar(final String message) {
        Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_SHORT).show();
    }

    private void togglePasswordActivity(final EditText password, final ImageView passwordVisibility) {
        if (isPasswordVisible) {
            password.setInputType(InputType.TYPE_CLASS_TEXT |
                    InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            isPasswordVisible = false;

            passwordVisibility.setImageResource(R.drawable.visibiltiy);
        } else {
            password.setInputType(InputType.TYPE_CLASS_TEXT |
                    InputType.TYPE_TEXT_VARIATION_PASSWORD);
            isPasswordVisible = true;

            passwordVisibility.setImageResource(R.drawable.baseline_visibility_off_24);
        }
        password.setSelection(password.getText().length());
    }
}