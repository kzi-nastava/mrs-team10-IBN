package com.example.ubercorp.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.ubercorp.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

public class AccountFragment extends Fragment {

    private LinearLayout userInfoSection;
    private FloatingActionButton fabEditProfile;
    private FrameLayout profilePictureSection;
    private MaterialCardView editFormCard;
    private MaterialCardView menuCard;
    private MaterialButton btnCancel;
    private MaterialButton btnSaveChanges;
    private TextView tvUserName;
    private TextView tvUserEmail;
    private TextInputEditText etFirstName;
    private TextInputEditText etLastName;
    private TextInputEditText etEmail;

    public AccountFragment() {
        // Required empty public constructor
    }

    public static AccountFragment newInstance() {
        return new AccountFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_account, container, false);

        // Inicijalizuj views
        fabEditProfile = view.findViewById(R.id.fabEditProfile);
        profilePictureSection = view.findViewById(R.id.profilePictureSection);
        editFormCard = view.findViewById(R.id.editFormCard);
        menuCard = view.findViewById(R.id.menuCard);
        btnCancel = view.findViewById(R.id.btnCancel);
        btnSaveChanges = view.findViewById(R.id.btnSaveChanges);
        tvUserName = view.findViewById(R.id.tvUserName);
        tvUserEmail = view.findViewById(R.id.tvUserEmail);
        etFirstName = view.findViewById(R.id.etFirstName);
        etLastName = view.findViewById(R.id.etLastName);
        etEmail = view.findViewById(R.id.etEmail);
        userInfoSection = view.findViewById(R.id.userInfoSection);

        // Postavi listeners
        setupListeners();

        return view;
    }

    private void setupListeners() {
        // Klik na FAB - prikaži formu
        fabEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Slika ostaje vidljiva, sakrivamo samo ime i email
                userInfoSection.setVisibility(View.GONE);
                editFormCard.setVisibility(View.VISIBLE);
                menuCard.setVisibility(View.GONE);
            }
        });

        // Klik na Cancel - sakrij formu
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editFormCard.setVisibility(View.GONE);
                userInfoSection.setVisibility(View.VISIBLE);
                menuCard.setVisibility(View.VISIBLE);
            }
        });

        // Klik na Save - sačuvaj i sakrij formu
        btnSaveChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String firstName = etFirstName.getText().toString();
                String lastName = etLastName.getText().toString();
                String email = etEmail.getText().toString();

                // TODO: Sačuvaj podatke u bazu/API

                // Ažuriraj prikaz
                tvUserName.setText(firstName + " " + lastName);
                tvUserEmail.setText(email);

                // Sakrij formu
                editFormCard.setVisibility(View.GONE);
                userInfoSection.setVisibility(View.VISIBLE);
                menuCard.setVisibility(View.VISIBLE);
            }
        });

        // Opciono: Klik na sliku za promenu slike
        profilePictureSection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: Otvori galeriju ili kameru za izbor nove slike
            }
        });

    }
}