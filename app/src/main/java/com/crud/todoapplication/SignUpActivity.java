package com.crud.todoapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.crud.todoapplication.model.Credentials;
import com.crud.todoapplication.model.User;
import com.google.android.material.snackbar.Snackbar;

public class SignUpActivity extends AppCompatActivity {

    private Button signUp;
    private TextView alreadyAccount;
    private EditText userName;
    private EditText email;
    private EditText title;
    private EditText password;
    private EditText rePassword;
    private ImageView passwordVisibility;
    private ImageView confirmPasswordVisibility;
    private boolean isPasswordVisible;
    private Credentials userCredentials;
    private DatabaseConnection databaseConnection;
    private static Long id = 0L;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        alreadyAccount = findViewById(R.id.already_account);
        userName = findViewById(R.id.sign_up_name);
        email = findViewById(R.id.sign_up_email);
        title = findViewById(R.id.sign_up_tile);
        password = findViewById(R.id.sign_up_pass);
        passwordVisibility = findViewById(R.id.visible_icon);
        confirmPasswordVisibility = findViewById(R.id.visibility_confirm);
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
                userCredentials.setName(userName.getText().toString());
                userCredentials.setEmail(email.getText().toString());
                userCredentials.setTitle(title.getText().toString());
                userCredentials.setPassword(password.getText().toString());
                userCredentials.setConfirmPassword(rePassword.getText().toString());
                String hashedPassword = MD5Helper.md5(userCredentials.getPassword());
                String hashedConfirmPassword = MD5Helper.md5(userCredentials.getConfirmPassword());

                userCredentials.setPassword(hashedPassword);
                userCredentials.setConfirmPassword(hashedConfirmPassword);

                if (TextUtils.isEmpty(userCredentials.getName()) || TextUtils.isEmpty(userCredentials.getEmail()) || TextUtils.isEmpty(userCredentials.getPassword()) || TextUtils.isEmpty(userCredentials.getConfirmPassword()) || TextUtils.isEmpty(userCredentials.getTitle())) {
                    showSnackBar("All Fields are Required");
                } else {
                    if (userCredentials.getPassword().equals(userCredentials.getConfirmPassword())) {
                        final Boolean checkUser = databaseConnection.checkUserName(userCredentials);
                        final Boolean checkEmail = databaseConnection.checkUserSignUpEmail(userCredentials);

                        if (!checkUser && !checkEmail) {
                            final Boolean insert = databaseConnection.insertData(userCredentials);
                            final User user = new User();
                            user.setName(userName.getText().toString());
                            user.setEmail(email.getText().toString());
                            user.setTitle("");
                            databaseConnection.insertUser(user);

                            if (insert) {
                                showSnackBar("Registered Successfully");
                                final Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
//                                intent.putExtra("id", userCredentials.getId());
//                                intent.putExtra("userName", userCredentials.getName());
//                                intent.putExtra("userTitle", userCredentials.getTitle());
//                                intent.putExtra("email", userCredentials.getEmail());
                                startActivity(intent);
                                finish();
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