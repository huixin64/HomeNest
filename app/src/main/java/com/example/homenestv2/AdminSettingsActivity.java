package com.example.homenestv2;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.firebase.auth.FirebaseAuth;

public class AdminSettingsActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private SwitchMaterial switchNotifications;
    private SwitchMaterial switchEmailAlerts;
    private MaterialButton buttonChangePassword;
    private MaterialButton buttonUpdateEmail;
    private MaterialButton buttonPrivacyPolicy;
    private MaterialButton buttonTermsOfService;
    private TextView textViewVersion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_settings);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Set up toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Settings");

        // Initialize views
        initializeViews();
        setupClickListeners();
        loadSettings();
    }

    private void initializeViews() {
        switchNotifications = findViewById(R.id.switchNotifications);
        switchEmailAlerts = findViewById(R.id.switchEmailAlerts);
        buttonChangePassword = findViewById(R.id.buttonChangePassword);
        buttonUpdateEmail = findViewById(R.id.buttonUpdateEmail);
        buttonPrivacyPolicy = findViewById(R.id.buttonPrivacyPolicy);
        buttonTermsOfService = findViewById(R.id.buttonTermsOfService);
        textViewVersion = findViewById(R.id.textViewVersion);
    }

    private void setupClickListeners() {
        switchNotifications.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // TODO: Implement notification settings
            Toast.makeText(this, "Notifications " + (isChecked ? "enabled" : "disabled"), Toast.LENGTH_SHORT).show();
        });

        switchEmailAlerts.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // TODO: Implement email alerts settings
            Toast.makeText(this, "Email alerts " + (isChecked ? "enabled" : "disabled"), Toast.LENGTH_SHORT).show();
        });

        buttonChangePassword.setOnClickListener(v -> {
            // TODO: Implement change password functionality
            Toast.makeText(this, "Change password clicked", Toast.LENGTH_SHORT).show();
        });

        buttonUpdateEmail.setOnClickListener(v -> {
            // TODO: Implement update email functionality
            Toast.makeText(this, "Update email clicked", Toast.LENGTH_SHORT).show();
        });

        buttonPrivacyPolicy.setOnClickListener(v -> {
            // TODO: Implement privacy policy view
            Toast.makeText(this, "Privacy policy clicked", Toast.LENGTH_SHORT).show();
        });

        buttonTermsOfService.setOnClickListener(v -> {
            // TODO: Implement terms of service view
            Toast.makeText(this, "Terms of service clicked", Toast.LENGTH_SHORT).show();
        });
    }

    private void loadSettings() {
        // TODO: Load saved settings from SharedPreferences or Firebase
        switchNotifications.setChecked(true);
        switchEmailAlerts.setChecked(true);
        textViewVersion.setText("Version 1.0.0");
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
} 