package com.example.ubercorp.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.ubercorp.R;
import com.example.ubercorp.api.ApiClient;
import com.example.ubercorp.api.AuthService;
import com.example.ubercorp.dto.GetProfileDTO;
import com.example.ubercorp.dto.RegisterDTO;
import com.example.ubercorp.enums.AccountType;
import com.example.ubercorp.utils.ImageHelper;

import java.net.URI;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {
    final int MEDIA_IMAGES_PERMISSION = 100;
    final int SELECT_IMAGE = 200;
    private String ImagePermission;
    private ImageView profilePicture;
    private Button selectImageButton;
    private Button registerButton;
    private EditText nameField;
    private EditText lastNameField;
    private EditText addressField;
    private EditText phoneField;
    private EditText emailField;
    private EditText passwordField;
    private EditText confirmField;

    private void selectImage(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Profile Picture"), SELECT_IMAGE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            ImagePermission = Manifest.permission.READ_MEDIA_IMAGES;
        else
            ImagePermission = Manifest.permission.READ_EXTERNAL_STORAGE;
        profilePicture = findViewById(R.id.profilePicture);
        selectImageButton = findViewById(R.id.selectImageButton);
        registerButton = findViewById(R.id.registerButton);
        nameField = findViewById(R.id.nameField);
        lastNameField = findViewById(R.id.lastNameField);
        addressField = findViewById(R.id.addressField);
        phoneField = findViewById(R.id.phoneField);
        emailField = findViewById(R.id.emailField);
        passwordField = findViewById(R.id.passwordField);
        confirmField = findViewById(R.id.confirmField);

        selectImageButton.setOnClickListener((v) -> {
            if (ContextCompat.checkSelfPermission(this, ImagePermission)
                    != PackageManager.PERMISSION_GRANTED){
                requestPermissions(new String[]{ImagePermission}, MEDIA_IMAGES_PERMISSION);
            } else {
                selectImage();
            }
        });
        registerButton.setOnClickListener((v) -> {
            String formError = getFormError();
            if(formError == null){
                register();
            } else {
                Toast toast = Toast.makeText(getApplicationContext(), formError, Toast.LENGTH_SHORT);
                toast.show();
            }
        });
    }

    private String getFormError() {
        if(nameField.getText().isEmpty()) return "Please fill out all fields!";
        else if(lastNameField.getText().isEmpty()) return "Please fill out all fields!";
        else if(addressField.getText().isEmpty()) return "Please fill out all fields!";
        else if(phoneField.getText().isEmpty()) return "Please fill out all fields!";
        else if(!Patterns.EMAIL_ADDRESS.matcher(emailField.getText()).matches()) return "Improper E-Mail format!";
        else if(passwordField.getText().length() < 6) return "Password must be at least 6 characters long!";
        else if(!confirmField.getText().toString().equals(passwordField.getText().toString())) return "Incorrect Confirm Password Field!";
        else return null;
    }

    private void register(){
        BitmapDrawable drawable = (BitmapDrawable) profilePicture.getDrawable();
        Bitmap bitmap = drawable.getBitmap();
        RegisterDTO registrationData = new RegisterDTO(
                emailField.getText().toString(),
                passwordField.getText().toString(),
                AccountType.PASSENGER,
                nameField.getText().toString(),
                lastNameField.getText().toString(),
                addressField.getText().toString(),
                phoneField.getText().toString(),
                ImageHelper.bitmapToBase64(bitmap)
        );

        AuthService authService = ApiClient.getInstance().createService(AuthService.class);
        Call<GetProfileDTO> register = authService.register(registrationData);

        register.enqueue(new Callback<GetProfileDTO>() {
            @Override
            public void onResponse(Call<GetProfileDTO> call, Response<GetProfileDTO> response) {
                if(response.isSuccessful()){
                    Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                    Toast toast = Toast.makeText(getApplicationContext(), "Successful registration! Verification sent to e-mail address!", Toast.LENGTH_SHORT);
                    toast.show();
                    startActivity(intent);
                    RegisterActivity.this.finish();
                } else {
                    Toast toast = Toast.makeText(getApplicationContext(), "Account with given e-mail already exists!", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }

            @Override
            public void onFailure(Call<GetProfileDTO> call, Throwable t) {
                Toast toast = Toast.makeText(getApplicationContext(), "Register failed! Check your Internet connection and try again", Toast.LENGTH_SHORT);
                toast.show();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK && requestCode == SELECT_IMAGE){
            Uri uri = data.getData();
            profilePicture.setImageURI(uri);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults, int deviceId) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults, deviceId);
        if (requestCode == MEDIA_IMAGES_PERMISSION && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            selectImage();
        }
    }
}