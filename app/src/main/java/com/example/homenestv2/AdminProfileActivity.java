package com.example.homenestv2;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_profile);

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        
        // Get admin email from intent
        adminEmail = getIntent().getStringExtra("admin_email");

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
    }

    private void setupClickListeners() {
        btnEditProfile.setOnClickListener(v -> {
            Intent intent = new Intent(this, EditProfileActivity.class);
            intent.putExtra("admin_email", adminEmail);
            startActivity(intent);
        });

        btnChangePassword.setOnClickListener(v -> {
            Intent intent = new Intent(this, ChangePasswordActivity.class);
            startActivity(intent);
        });

        btnNotifications.setOnClickListener(v -> {
            Intent intent = new Intent(this, NotificationsActivity.class);
            startActivity(intent);
        });

        btnHelp.setOnClickListener(v -> {
            Intent intent = new Intent(this, HelpSupportActivity.class);
            startActivity(intent);
        });

        btnLogout.setOnClickListener(v -> showLogoutConfirmation());
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
        loadAdminData(); // Refresh data when returning from other activities
    }
} 