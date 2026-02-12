package com.example.ubercorp.activities;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.ubercorp.R;
import com.example.ubercorp.api.ApiClient;
import com.example.ubercorp.api.AuthService;
import com.example.ubercorp.dto.SetPasswordDTO;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SetPasswordActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_set_password);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        EditText passwordField = findViewById(R.id.passwordField);
        EditText confirmField = findViewById(R.id.confirmField);
        Button button = findViewById(R.id.redirect_btn);
        TextView infoText = findViewById(R.id.info_text);
        TextView errorText = findViewById(R.id.invalid_text);

        Uri uri = getIntent().getData();
        String token = uri.getLastPathSegment();

        AuthService authService = ApiClient.getInstance().createService(AuthService.class);
        Call<Void> call = authService.checkPasswordToken(token);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if(!response.isSuccessful()){
                    passwordField.setVisibility(GONE);
                    confirmField.setVisibility(GONE);
                    button.setVisibility(GONE);
                    infoText.setVisibility(GONE);
                    errorText.setVisibility(VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                errorText.setText("Check Your Internet Connection and Try Again!");
                passwordField.setVisibility(GONE);
                confirmField.setVisibility(GONE);
                button.setVisibility(GONE);
                infoText.setVisibility(GONE);
                errorText.setVisibility(VISIBLE);
            }
        });

        button.setOnClickListener((v) -> {
            if(passwordField.getText().length() < 6) {
                Toast toast = Toast.makeText(getApplicationContext(), "Password must be at least 6 characters long!", Toast.LENGTH_SHORT);
                toast.show();
            }
            else if (!confirmField.getText().toString().equals(passwordField.getText().toString())) {
                Toast toast = Toast.makeText(getApplicationContext(), "Invalid Confirm Password Field!", Toast.LENGTH_SHORT);
                toast.show();
            }
            else{
                SetPasswordDTO password = new SetPasswordDTO(passwordField.getText().toString().trim());
                Call<Void> setPasswordCall = authService.setPassword(token, password);
                setPasswordCall.enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if(response.isSuccessful()){
                            Toast toast = Toast.makeText(getApplicationContext(), "Password Set Successfully!", Toast.LENGTH_SHORT);
                            toast.show();
                            Intent intent = new Intent(SetPasswordActivity.this, LoginActivity.class);
                            startActivity(intent);
                            SetPasswordActivity.this.finish();
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Toast toast = Toast.makeText(getApplicationContext(), "Check Your Internet Connection and Try Again!", Toast.LENGTH_SHORT);
                        toast.show();
                    }
                });
            }
        });

    }
}