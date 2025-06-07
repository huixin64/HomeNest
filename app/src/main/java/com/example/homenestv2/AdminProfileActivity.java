package com.example.homenestv2;

import static androidx.constraintlayout.widget.ConstraintLayoutStates.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;

public class AdminProfileActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private String adminEmail;

    // UI Components
    private ShapeableImageView ivProfileImage;
    private TextView tvName, tvEmail;
    private TextView tvTotalProperties, tvTotalBookings, tvTotalRevenue;
    private MaterialButton btnEditProfile, btnChangePassword, btnNotifications, btnHelp, btnLogout;

    // Settings UI Components
    private SwitchMaterial switchNotifications;
    private SwitchMaterial switchEmailAlerts;
    private MaterialButton buttonUpdateEmail;
    private MaterialButton buttonPrivacyPolicy;
    private MaterialButton buttonTermsOfService;
    private TextView textViewVersion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_profile);

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        
        // Get admin email from intent
        adminEmail = getIntent().getStringExtra("admin_email");
        Log.d("AdminProfileActivity", "Admin email: " + adminEmail);

        setupToolbar();
        initializeViews();
        setupClickListeners();
        loadAdminData();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Profile");
        }
    }

    private void initializeViews() {
        ivProfileImage = findViewById(R.id.ivProfileImage);
        tvName = findViewById(R.id.tvName);
        tvEmail = findViewById(R.id.tvEmail);
        tvTotalProperties = findViewById(R.id.tvTotalProperties);
        tvTotalBookings = findViewById(R.id.tvTotalBookings);
        tvTotalRevenue = findViewById(R.id.tvTotalRevenue);
        
        btnEditProfile = findViewById(R.id.btnEditProfile);
        btnChangePassword = findViewById(R.id.btnChangePassword);
        btnNotifications = findViewById(R.id.btnNotifications);
        btnHelp = findViewById(R.id.btnHelp);
        btnLogout = findViewById(R.id.btnLogout);

        // Initialize Settings Views
        switchNotifications = findViewById(R.id.switchNotifications);
        switchEmailAlerts = findViewById(R.id.switchEmailAlerts);
        buttonUpdateEmail = findViewById(R.id.buttonUpdateEmail);
        buttonPrivacyPolicy = findViewById(R.id.buttonPrivacyPolicy);
        buttonTermsOfService = findViewById(R.id.buttonTermsOfService);
        textViewVersion = findViewById(R.id.textViewVersion);
    }

    private void setupClickListeners() {
        Log.d(TAG, "admin_email: " + adminEmail);
        btnEditProfile.setOnClickListener(v -> {
            if (adminEmail != null) {
                Intent intent = new Intent(this, EditProfileActivity.class);
                intent.putExtra("admin_email", adminEmail);
                Log.d("onclick_editP", "enter");
                startActivity(intent);
                Log.d("onclick_editP", "exit");
            } else {
                Toast.makeText(this, "Admin email is missing!", Toast.LENGTH_SHORT).show();
            }
        });

        btnChangePassword.setOnClickListener(v -> {
            Intent intent = new Intent(this, ChangePasswordActivity.class);
            startActivity(intent);
        });

        btnNotifications.setOnClickListener(v -> {
            Toast.makeText(this, "Notifications button clicked (Integrate logic or navigate)", Toast.LENGTH_SHORT).show();
        });

        btnHelp.setOnClickListener(v -> {
            Toast.makeText(this, "Help & Support button clicked (Integrate logic or navigate)", Toast.LENGTH_SHORT).show();
        });

        btnLogout.setOnClickListener(v -> showLogoutConfirmation());

        // Settings Click Listeners (Copied from AdminSettingsActivity)
        switchNotifications.setOnCheckedChangeListener((buttonView, isChecked) -> {
            Toast.makeText(this, "Notifications " + (isChecked ? "enabled" : "disabled") + " (Save setting)", Toast.LENGTH_SHORT).show();
        });

        switchEmailAlerts.setOnCheckedChangeListener((buttonView, isChecked) -> {
            Toast.makeText(this, "Email alerts " + (isChecked ? "enabled" : "disabled") + " (Save setting)", Toast.LENGTH_SHORT).show();
        });

        buttonUpdateEmail.setOnClickListener(v -> {
            Intent intent = new Intent(AdminProfileActivity.this, UpdateEmailActivity.class);
            intent.putExtra("admin_email", adminEmail);
            startActivity(intent);
        });

        buttonPrivacyPolicy.setOnClickListener(v -> {
            Intent intent = new Intent(AdminProfileActivity.this, WebViewActivity.class);
            intent.putExtra("title", "Privacy Policy");
            intent.putExtra("url", "https://www.example.com/privacy");
            startActivity(intent);
        });

        buttonTermsOfService.setOnClickListener(v -> {
            Intent intent = new Intent(AdminProfileActivity.this, WebViewActivity.class);
            intent.putExtra("title", "Terms of Service");
            intent.putExtra("url", "https://www.example.com/terms");
            startActivity(intent);
        });
    }

    private void loadAdminData() {
        if (adminEmail != null) {
            // Load admin profile data
            db.collection("admins")
                    .document(adminEmail)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String name = documentSnapshot.getString("name");
                            String email = documentSnapshot.getString("email");
                            String profileImageUrl = documentSnapshot.getString("profileImageUrl");

                            tvName.setText(name);
                            tvEmail.setText(email);

                            if (profileImageUrl != null && !profileImageUrl.isEmpty()) {
                                Glide.with(this)
                                    .load(profileImageUrl)
                                    .placeholder(R.drawable.placeholder_profile)
                                    .error(R.drawable.placeholder_profile)
                                    .into(ivProfileImage);
                            }
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Error loading profile: " + e.getMessage(), 
                                Toast.LENGTH_SHORT).show();
                    });

            // Load statistics
            loadStatistics();
        }
    }

    private void loadStatistics() {
        // Load total properties
        db.collection("properties")
                .whereEqualTo("ownerId", adminEmail)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    tvTotalProperties.setText(String.valueOf(queryDocumentSnapshots.size()));
                });

        // Load total bookings
        db.collection("bookings")
                .whereEqualTo("propertyOwnerId", adminEmail)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    tvTotalBookings.setText(String.valueOf(queryDocumentSnapshots.size()));
                });

        // Load total revenue
        db.collection("bookings")
                .whereEqualTo("propertyOwnerId", adminEmail)
                .whereEqualTo("status", "completed")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    double totalRevenue = 0;
                    for (DocumentSnapshot document : queryDocumentSnapshots) {
                        totalRevenue += document.getDouble("totalAmount");
                    }
                    tvTotalRevenue.setText(String.format("$%.2f", totalRevenue));
                });
    }

    private void loadSettings() {
        // TODO: Load saved settings from SharedPreferences or Firebase
        // Example: Load notification and email alert settings
        // boolean notificationsEnabled = getNotificationsSetting();
        // boolean emailAlertsEnabled = getEmailAlertsSetting();
        // switchNotifications.setChecked(notificationsEnabled);
        // switchEmailAlerts.setChecked(emailAlertsEnabled);

        // For now, setting default checked state and version text
        switchNotifications.setChecked(true);
        switchEmailAlerts.setChecked(true);
        textViewVersion.setText("Version 1.0.0");
    }

    private void showLogoutConfirmation() {
        new AlertDialog.Builder(this)
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Logout", (dialog, which) -> {
                    mAuth.signOut();
                    Intent intent = new Intent(this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadAdminData();
        loadSettings();
    }
} 