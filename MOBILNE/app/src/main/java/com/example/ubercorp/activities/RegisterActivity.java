package com.example.ubercorp.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.ubercorp.R;

import java.net.URI;

public class RegisterActivity extends AppCompatActivity {
    final int MEDIA_IMAGES_PERMISSION = 100;
    final int SELECT_IMAGE = 200;
    private String ImagePermission;
    ImageView profilePicture;

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
        Button selectImageButton = findViewById(R.id.selectImageButton);
        selectImageButton.setOnClickListener((v) -> {
            if (ContextCompat.checkSelfPermission(this, ImagePermission)
                    != PackageManager.PERMISSION_GRANTED){
                requestPermissions(new String[]{ImagePermission}, MEDIA_IMAGES_PERMISSION);
            } else {
                selectImage();
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