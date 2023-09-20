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
import com.crud.todoapplication.model.Credentials;
import com.crud.todoapplication.model.SignUp;
import com.crud.todoapplication.model.User;
import com.google.android.material.snackbar.Snackbar;

public class SignUpActivity extends AppCompatActivity {

    private EditText userName;
    private EditText email;
    private EditText title;
    private EditText hint;
    private EditText password;
    private EditText rePassword;
    private ImageView passwordVisibility;
    private ImageView confirmPasswordVisibility;
    private boolean isPasswordVisible;
    private Credentials userCredentials;
    private User user;
    private DatabaseConnection databaseConnection;
    private static Long id = 0L;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        TextView alreadyAccount = findViewById(R.id.already_account);
        userName = findViewById(R.id.sign_up_name);
        email = findViewById(R.id.sign_up_email);
        title = findViewById(R.id.sign_up_tile);
        hint = findViewById(R.id.sign_up_hint);
        password = findViewById(R.id.sign_up_pass);
        passwordVisibility = findViewById(R.id.visible_icon);
        confirmPasswordVisibility = findViewById(R.id.visibility_confirm);
        rePassword = findViewById(R.id.sign_up_repass);
        Button signUp = findViewById(R.id.register_button);
        userCredentials = new Credentials();
        user = new User();
        databaseConnection = new DatabaseConnection(this);
        alreadyAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        passwordVisibility.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                togglePasswordActivity(password, passwordVisibility);
            }
        });
        confirmPasswordVisibility.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                togglePasswordActivity(password, confirmPasswordVisibility);
            }
        });
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userCredentials.setId(++id);
                user.setName(userName.getText().toString());
                userCredentials.setEmail(email.getText().toString());
                user.setTitle(title.getText().toString());
                userCredentials.setPassword(password.getText().toString());
                userCredentials.setHint(hint.getText().toString());
                userCredentials.setConformPassword(rePassword.getText().toString());

                String hashedPassword = MD5Helper.md5(userCredentials.getPassword());
                String hashedConfirmPassword = MD5Helper.md5(userCredentials.getConformPassword());

                userCredentials.setPassword(hashedPassword);
                userCredentials.setConformPassword(hashedConfirmPassword);

                if (TextUtils.isEmpty(user.getName()) || TextUtils.isEmpty(userCredentials.getEmail()) || TextUtils.isEmpty(userCredentials.getPassword()) || TextUtils.isEmpty(userCredentials.getHint()) || TextUtils.isEmpty(userCredentials.getConformPassword()) || TextUtils.isEmpty(user.getTitle())) {
                    showSnackBar(String.valueOf(R.string.all_fields_are_required));
                } else {
                    if (userCredentials.getPassword().equals(userCredentials.getConformPassword())) {
                        final Boolean checkUser = databaseConnection.checkUserName(user);
                        final Boolean checkEmail = databaseConnection.checkUserSignUpEmail(userCredentials);

                        if (!checkUser && !checkEmail) {
                            final AuthenticationService authenticationService = new AuthenticationService("http://192.168.1.109:8080/");
                            final SignUp signUpUser = new SignUp(user, userCredentials);
                            authenticationService.signUp(signUpUser, new AuthenticationService.ApiResponseCallBack() {
                                @Override
                                public void onSuccess(String response) {
                                    showSnackBar(getString(R.string.Signup_successful));
                                    new Handler().postDelayed(() -> {
                                        final Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                                        startActivity(intent);
                                        finish();
                                    },100);
                                }

                                @Override
                                public void onFailure(String response) {
                                    showSnackBar(getString(R.string.Signup_failed));
                                }
                            });
                        } else {
                            showSnackBar(getString(R.string.User_already_exists));
                        }
                    } else {
                        showSnackBar(getString(R.string.password_mismatch));
                    }
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