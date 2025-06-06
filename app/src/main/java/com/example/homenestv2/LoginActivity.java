package com.example.homenestv2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.homenestv2.databinding.ActivityLoginBinding;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {
    private ActivityLoginBinding binding;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private boolean isAdminLogin = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        setupTabLayout();
        setupClickListeners();
    }

    private void setupTabLayout() {
        if (binding == null) return;
        
        // Set initial state
        isAdminLogin = binding.tabLayout.getSelectedTabPosition() == 1;

        binding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                isAdminLogin = tab.getPosition() == 1;
                // Update button text based on selected tab
                if (binding != null) {
                    binding.btnRegister.setText(isAdminLogin ? "Become a Host" : "Create User Account");
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void setupClickListeners() {
        if (binding == null) return;

        binding.btnLogin.setOnClickListener(v -> performLogin());
        binding.btnRegister.setOnClickListener(v -> {
            try {
                startRegistration();
            } catch (Exception e) {
                Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        });
        binding.tvForgotPassword.setOnClickListener(v -> handleForgotPassword());
    }

    private void performLogin() {
        if (binding == null) return;

        String email = binding.etEmail.getText().toString().trim();
        String password = binding.etPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        binding.btnLogin.setEnabled(false);
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (binding != null) {
                        binding.btnLogin.setEnabled(true);
                    }
                    if (task.isSuccessful()) {
                        verifyUserType(email);
                    } else {
                        Toast.makeText(LoginActivity.this, "Authentication failed: " + 
                                task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void verifyUserType(String email) {
        // First check if it's a host account
        db.collection("hosts")
                .document(email)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // If it's a host account and we're in user login tab, deny access
                        if (!isAdminLogin) {
                            mAuth.signOut();
                            Toast.makeText(LoginActivity.this, 
                                    "Please use the Admin Login tab to access your account", 
                                    Toast.LENGTH_LONG).show();
                            return;
                        }
                        
                        // Check if subscription is still valid
                        long subscriptionEndDate = documentSnapshot.getLong("subscriptionEndDate");
                        if (subscriptionEndDate > System.currentTimeMillis()) {
                            isAdminLogin = true;
                            navigateToDashboard();
                        } else {
                            mAuth.signOut();
                            Toast.makeText(LoginActivity.this, 
                                    "Your host subscription has expired. Please renew to continue.", 
                                    Toast.LENGTH_LONG).show();
                        }
                    } else {
                        // If not a host, check if it's a regular user
                        db.collection("users")
                                .document(email)
                                .get()
                                .addOnSuccessListener(userSnapshot -> {
                                    if (userSnapshot.exists()) {
                                        // If it's a regular user and we're in admin login tab, deny access
                                        if (isAdminLogin) {
                                            mAuth.signOut();
                                            Toast.makeText(LoginActivity.this, 
                                                    "Please use the User Login tab to access your account", 
                                                    Toast.LENGTH_LONG).show();
                                            return;
                                        }
                                        isAdminLogin = false;
                                        navigateToDashboard();
                                    } else {
                                        mAuth.signOut();
                                        Toast.makeText(LoginActivity.this, 
                                                "Account not found", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .addOnFailureListener(e -> {
                                    mAuth.signOut();
                                    Toast.makeText(LoginActivity.this, 
                                            "Error verifying account: " + e.getMessage(), 
                                            Toast.LENGTH_SHORT).show();
                                });
                    }
                })
                .addOnFailureListener(e -> {
                    mAuth.signOut();
                    Toast.makeText(LoginActivity.this, 
                            "Error verifying account: " + e.getMessage(), 
                            Toast.LENGTH_SHORT).show();
                });
    }

    private void navigateToDashboard() {
        Intent intent = new Intent(this, 
                isAdminLogin ? AdminDashboardActivity.class : UserDashboardActivity.class);
        startActivity(intent);
        finish();
    }

    private void startRegistration() {
        try {
            Intent intent = new Intent(this, RegistrationActivity.class);
            intent.putExtra("isAdmin", isAdminLogin);
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(this, "Error starting registration: " + e.getMessage(), 
                    Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private void handleForgotPassword() {
        if (binding == null) return;

        String email = binding.etEmail.getText().toString().trim();
        if (email.isEmpty()) {
            Toast.makeText(this, "Please enter your email", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(LoginActivity.this, 
                                "Password reset email sent", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(LoginActivity.this, 
                                "Failed to send reset email: " + task.getException().getMessage(), 
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
} 