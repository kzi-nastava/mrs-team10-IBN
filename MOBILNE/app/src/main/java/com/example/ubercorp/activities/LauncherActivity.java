package com.example.ubercorp.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.auth0.android.jwt.JWT;
import com.example.ubercorp.R;

public class LauncherActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_launcher);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Intent home = new Intent(LauncherActivity.this, HomeActivity.class);
        Intent login = new Intent(LauncherActivity.this, LoginActivity.class);
        SharedPreferences sharedPref = getSharedPreferences("uber_corp", Context.MODE_PRIVATE);
        try {
            JWT token = new JWT(sharedPref.getString("auth_token", ""));
            if (token.isExpired(0)){
                startActivity(login);
            } else {
                startActivity(home);
            }
            this.finish();
        } catch (Exception ex) {
            startActivity(login);
            this.finish();
        }
    }
}