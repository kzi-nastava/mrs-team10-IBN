package com.example.ubercorp.activities;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.ubercorp.R;
import com.example.ubercorp.api.ApiClient;
import com.example.ubercorp.api.AuthService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VerifyAccountActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_verify_account);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        TextView infoText = findViewById(R.id.info_text);
        Button redirectButton = findViewById(R.id.redirect_btn);

        AuthService authService = ApiClient.getInstance().createService(AuthService.class);
        Uri uri = getIntent().getData();
        String verifyID = uri.getLastPathSegment();
        Call<Void> call = authService.verifyAccount(verifyID);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if(response.isSuccessful()) infoText.setText("Account Successfully Verified!");
                else infoText.setText("Invalid Verification Link!");
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                infoText.setText("Check Your Internet Connection and Try Again!");
            }
        });

        redirectButton.setOnClickListener((v) -> {
            Intent intent = new Intent(VerifyAccountActivity.this, LoginActivity.class);
            startActivity(intent);
            VerifyAccountActivity.this.finish();
        });
    }
}