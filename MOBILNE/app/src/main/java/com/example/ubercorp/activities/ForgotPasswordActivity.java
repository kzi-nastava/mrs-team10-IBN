package com.example.ubercorp.activities;

import android.os.Bundle;
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
import com.example.ubercorp.dto.AccountDTO;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgotPasswordActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_forgot_password);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        EditText emailField = findViewById(R.id.emailField);
        Button sendEmailButton = findViewById(R.id.sendEmailButton);
        sendEmailButton.setOnClickListener((v) -> {
            String email = emailField.getText().toString();
            if (Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                AccountDTO account = new AccountDTO(email);
                AuthService authService = ApiClient.getInstance().createService(AuthService.class);
                Call<Void> forgotPassword = authService.forgotPassword(account);

                forgotPassword.enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if(response.isSuccessful()){
                            Toast toast = Toast.makeText(getApplicationContext(), "Password reset e-mail sent!", Toast.LENGTH_SHORT);
                            toast.show();
                            ForgotPasswordActivity.this.finish();
                        } else {
                            Toast toast = Toast.makeText(getApplicationContext(), "Account with given e-mail address doesn't exist!", Toast.LENGTH_SHORT);
                            toast.show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Toast toast = Toast.makeText(getApplicationContext(), "Check your Internet connection and try again", Toast.LENGTH_SHORT);
                        toast.show();
                    }
                });
                Toast toast = Toast.makeText(getApplicationContext(), "E-mail Sent!", Toast.LENGTH_SHORT);
                toast.show();
                finish();
            } else {
                Toast toast = Toast.makeText(getApplicationContext(), "Improper E-mail Format!", Toast.LENGTH_SHORT);
                toast.show();
            }
        });
    }
}