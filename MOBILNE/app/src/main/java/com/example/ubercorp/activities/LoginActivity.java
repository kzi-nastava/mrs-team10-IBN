package com.example.ubercorp.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.ubercorp.R;
import com.example.ubercorp.api.ApiClient;
import com.example.ubercorp.api.AuthService;
import com.example.ubercorp.api.FcmService;
import com.example.ubercorp.dto.FcmTokenDTO;
import com.example.ubercorp.model.AuthToken;
import com.example.ubercorp.model.Credentials;
import com.google.firebase.messaging.FirebaseMessaging;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private EditText emailField;
    private EditText passwordField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        emailField = findViewById(R.id.emailField);
        passwordField = findViewById(R.id.passwordField);

        Button loginButton = findViewById(R.id.loginButton);
        loginButton.setOnClickListener((v) -> {
            String email = emailField.getText().toString();
            if (Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                login();
            } else {
                Toast toast = Toast.makeText(getApplicationContext(), "Improper E-mail Format!", Toast.LENGTH_SHORT);
                toast.show();
            }
        });

        Button registerButton = findViewById(R.id.registerButton);
        registerButton.setOnClickListener((v) -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

        Button forgotPasswordButton = findViewById(R.id.forgotPasswordButton);
        forgotPasswordButton.setOnClickListener((v) -> {
            Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
            startActivity(intent);
        });

        Button continueButton = findViewById(R.id.continueButton);
        continueButton.setOnClickListener((v) -> {
            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
            startActivity(intent);
            this.finish();
        });
    }

    public void login() {
        Credentials creds = new Credentials(emailField.getText().toString().trim(), passwordField.getText().toString().trim());
        AuthService authService = ApiClient.getInstance().createService(AuthService.class);
        Call<AuthToken> login = authService.login(creds);

        login.enqueue(new Callback<AuthToken>() {
            @Override
            public void onResponse(Call<AuthToken> call, Response<AuthToken> response){
                if (response.isSuccessful()){
                    Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                    SharedPreferences sharedPref = getSharedPreferences("uber_corp", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putString("auth_token", response.body().getAccessToken());
                    editor.putLong("expires_in", response.body().getExpiresIn());
                    boolean saved = editor.commit();

                    registerFcmToken();
                    startActivity(intent);
                } else {
                    Log.e("LOGIN", "Login failed - Response code: " + response.code());
                    Toast toast = Toast.makeText(getApplicationContext(), "Incorrect credentials!", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }

            @Override
            public void onFailure(Call<AuthToken> call, Throwable t) {
                Toast toast = Toast.makeText(getApplicationContext(), "Login failed! Check your Internet connection and try again", Toast.LENGTH_SHORT);
                toast.show();
            }
        });
    }

    private void registerFcmToken() {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {

                    if (!task.isSuccessful()) {
                        Log.e("FCM", "Fetching FCM token failed", task.getException());
                        return;
                    }

                    String fcmToken = task.getResult();

                    sendTokenToServer(fcmToken);
                });
    }

    private void sendTokenToServer(String fcmToken) {

        SharedPreferences sharedPref = getSharedPreferences("uber_corp", MODE_PRIVATE);
        String authToken = sharedPref.getString("auth_token", null);

        if (authToken == null) {
            Log.e("FCM", "Auth token is NULL!");
            return;
        }

        FcmService fcmService = ApiClient.getInstance().createService(FcmService.class);
        FcmTokenDTO dto = new FcmTokenDTO(fcmToken);

        Call<Void> call = fcmService.updateFcmToken("Bearer " + authToken, dto);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {

                if (response.isSuccessful()) {
                    Log.d("FCM", "Token sent to server successfully");
                } else {
                    Log.e("FCM", "Failed - Response code: " + response.code());
                    try {
                        String errorBody = response.errorBody() != null ?
                                response.errorBody().string() : "No error body";
                        Log.e("FCM", "Error body: " + errorBody);
                    } catch (Exception e) {
                        Log.e("FCM", "Could not read error body", e);
                    }
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("FCM", "API call failed", t);
                Log.e("FCM", "Error message: " + t.getMessage());
                Log.e("FCM", "Error class: " + t.getClass().getName());
            }
        });
    }

}