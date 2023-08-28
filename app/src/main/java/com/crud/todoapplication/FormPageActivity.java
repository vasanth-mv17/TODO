package com.crud.todoapplication;

import android.annotation.SuppressLint;
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

    @SuppressLint("MissingInflatedId")
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_page);

        user = new User();
        backMenuButton = findViewById(R.id.backMenuButton);
        profileIcon = findViewById(R.id.profileIcon);
        user_name = findViewById(R.id.editTextText);
        user_title = findViewById(R.id.editTextText2);
        final String existingName = getIntent().getStringExtra("Name");
        final String existingTitle = getIntent().getStringExtra("Title");
        final String[] nameWords = existingName.split(" ");
        final StringBuilder profileText = new StringBuilder();

        for (final String word : nameWords) {

            if (! word.isEmpty()) {
                profileText.append(Character.toUpperCase(word.charAt(0)));
            }
        }
        user_name.setText(existingName);
        user_title.setText(existingTitle);
        profileIcon.setText(profileText);
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
                final String name = user_name.getText().toString();
                final String title = user_title.getText().toString();
                final Intent resultantIntent = new Intent();
                final String[] words = name.split(" ");
                final StringBuilder profile = new StringBuilder();

                user.setName(name);
                user.setTitle(title);

                for (final String word : words) {

                    if (! word.isEmpty()) {
                        profile.append(Character.toUpperCase(word.charAt(0)));
                    }
                }

                resultantIntent.putExtra("User Name", user.getName());
                resultantIntent.putExtra("User Title", user.getTitle());

                if (!TextUtils.isEmpty(name)) {
                    profileIcon.setText(profile);
                }
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
