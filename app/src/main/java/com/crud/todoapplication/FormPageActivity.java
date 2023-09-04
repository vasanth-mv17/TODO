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

import com.crud.todoapplication.model.User;

public class FormPageActivity extends AppCompatActivity {

    private ImageButton backMenuButton;
    private EditText user_name;
    private EditText user_title;
    private TextView profileIcon;
    private Button saveButton;
    private Button cancelButton;
    private User user;
    private static Long id = 0L;
    private DatabaseConnection databaseConnection;


    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_page);

        databaseConnection = new DatabaseConnection(this);
        backMenuButton = findViewById(R.id.backMenuButton);
        profileIcon = findViewById(R.id.profileIcon);
        user_name = findViewById(R.id.editTextText);
        user_title = findViewById(R.id.editTextText2);
        user = databaseConnection.getUserProfile();

        if (null != user) {
            user_name.setText(user.getName());
            user_title.setText(user.getTitle());
            profileIcon.setText(user.setProfileIcon());
        } else {
            user = new User();
            user.setName(getIntent().getStringExtra(String.valueOf(R.string.Name)));
            user.setTitle(getIntent().getStringExtra(String.valueOf(R.string.Title)));
        }
        setListeners();
    }

    private void setListeners() {
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

                user.setName(user_name.getText().toString());
                user.setTitle(user_title.getText().toString());
                profileIcon.setText(user.setProfileIcon());
                databaseConnection.insertUser(user);

                user.setId(++id);
                resultantIntent.putExtra(String.valueOf(R.string.Id), user.getId());
                resultantIntent.putExtra(String.valueOf(R.string.UserName), user.getName());
                resultantIntent.putExtra(String.valueOf(R.string.UserTitle), user.getTitle());
                setResult(RESULT_OK, resultantIntent);
                finish();
            }
        });

        cancelButton = findViewById(R.id.cancel_button);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }
}
