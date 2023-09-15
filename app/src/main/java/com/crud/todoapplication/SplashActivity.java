package com.crud.todoapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

/**
 * <p>
 * Represents the Entry page of the application
 * </p>
 *
 * @author vasanth
 * @version 1.0
 */
public class SplashActivity extends AppCompatActivity {

    /**
     * <p>
     * Creation of the menu list activity
     * </p>
     *
     * @param savedInstanceState Refers the saved instance of the state
     */
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(() -> {
            final Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        },3000);
    }
}