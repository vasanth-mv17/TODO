package com.crud.todoapplication;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.crud.todoapplication.api.AuthenticationService;
import com.crud.todoapplication.model.Credentials;
import com.crud.todoapplication.model.User;
import com.google.android.material.snackbar.Snackbar;

public class FormPageActivity extends AppCompatActivity {

    private ImageButton backMenuButton;
    private EditText user_name;
    private EditText user_title;
    private TextView profileIcon;
    private ImageButton saveButton;
    private ImageButton cancelButton;
    private User user;
    private String token;
    private static Long id = 0L;
    private DatabaseConnection databaseConnection;
    private Credentials userCredentials;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int currentTheme = FontManager.getCurrentColour();
        setContentView(R.layout.activity_form_page);

        databaseConnection = new DatabaseConnection(this);
        backMenuButton = findViewById(R.id.backMenuButton);
        profileIcon = findViewById(R.id.profileIcon);
        user_name = findViewById(R.id.editTextText);
        user_title = findViewById(R.id.editTextText2);
        //user = databaseConnection.getUserProfile();
        token = getIntent().getStringExtra(getString(R.string.token));
        user = new User();
        userCredentials = new Credentials();

//        if (null != user) {
//            user_name.setText(user.getName());
//            user_title.setText(user.getTitle());
//            profileIcon.setText(user.setProfileIcon());
//        }

        backMenuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        saveButton = findViewById(R.id.save_button);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Intent resultantIntent = new Intent();

                if (null != user) {
                    final String name = null != user.getName() ? user.getName() : "";
                    final String title = null != user.getTitle() ? user.getTitle() : "";

                    if (!name.equals(user_name.getText().toString()) || !title.equals(user_title.getText().toString())) {
                        user.setName(user_name.getText().toString());
                        user.setTitle(user_title.getText().toString());
                        profileIcon.setText(user.setProfileIcon());

                        final AuthenticationService authenticationService = new AuthenticationService(getString(R.string.http_192_168_1_109_8080), token );

                        authenticationService.UpdateUserDetail(user, new AuthenticationService.ApiResponseCallBack() {
                            @Override
                            public void onSuccess(String response) {
                                showSnackBar(response);
                                resultantIntent.putExtra(String.valueOf(R.string.UserName), user.getName());
                                resultantIntent.putExtra(String.valueOf(R.string.UserTitle), user.getTitle());
                                setResult(RESULT_OK, resultantIntent);
                                finish();
                            }

                            @Override
                            public void onFailure(String response) {
                                showSnackBar(response);
                            }
                        });
                    }
                }
            }
        });

        cancelButton = findViewById(R.id.cancel_button);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        if (currentTheme == R.color.Primary) {
            saveButton.setBackgroundColor(getResources().getColor(R.color.Primary));
            cancelButton.setBackgroundColor(getResources().getColor(R.color.Primary));
        } else if (currentTheme == R.color.Secondary) {
            saveButton.setBackgroundColor(getResources().getColor(R.color.Secondary));
            cancelButton.setBackgroundColor(getResources().getColor(R.color.Secondary));
        }

        applyFontToAllLayout();
        applyTextSizeToTextViews();
    }

    public void applyFontToAllLayout() {
        FontManager.applyFontToView(this, getWindow().getDecorView().findViewById(android.R.id.content));
    }
    private void applyTextSizeToTextViews() {
        FontManager.applyTextSizeToView(findViewById(android.R.id.content).getRootView());
    }
    private void showSnackBar(final String message) {
        Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_SHORT).show();
    }
}
