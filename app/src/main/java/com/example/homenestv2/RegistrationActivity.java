package com.example.homenestv2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.homenestv2.databinding.ActivityRegistrationBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Calendar;

public class RegistrationActivity extends AppCompatActivity {
    private ActivityRegistrationBinding binding;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private boolean isAdminRegistration;
    private static final double YEARLY_HOST_FEE = 299.99; // Yearly fee in MYR

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegistrationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        isAdminRegistration = getIntent().getBooleanExtra("isAdmin", false);

        setupToolbar();
        setupUI();
        setupClickListeners();
        setupBackPress();
    }

    private void setupToolbar() {
        try {
            Toolbar toolbar = binding.toolbar;
            setSupportActionBar(toolbar);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle(isAdminRegistration ? "Become a Host" : "User Registration");
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setDisplayShowHomeEnabled(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error setting up toolbar: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void setupUI() {
        if (binding == null) return;
        try {
            // Show admin-specific fields only for admin registration
            binding.tilStaffId.setVisibility(isAdminRegistration ? View.VISIBLE : View.GONE);
            binding.tvRegistrationFee.setVisibility(isAdminRegistration ? View.VISIBLE : View.GONE);
            binding.tvFeeDescription.setVisibility(isAdminRegistration ? View.VISIBLE : View.GONE);
            
            // Update button text and fee information
            if (isAdminRegistration) {
                binding.btnRegister.setText("Pay & Register as Host");
                binding.tvRegistrationFee.setText(String.format("Yearly Registration Fee: RM%.2f", YEARLY_HOST_FEE));
                binding.tvFeeDescription.setText("This fee is required annually to maintain your host status and list your property.");
            } else {
                binding.btnRegister.setText("Register as User");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error setting up UI: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void setupBackPress() {
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                finish();
            }
        });
    }

    private void setupClickListeners() {
        if (binding == null) return;
        try {
            binding.btnRegister.setOnClickListener(v -> {
                if (isAdminRegistration) {
                    showPaymentConfirmation();
                } else {
                    performRegistration();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error setting up click listeners: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void showPaymentConfirmation() {
        // Here you would typically show a payment dialog or navigate to a payment screen
        // For now, we'll just show a confirmation dialog
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Confirm Yearly Payment")
                .setMessage(String.format("Yearly registration fee: RM%.2f\n\n" +
                        "This fee is required to become a host and list your property.\n" +
                        "The fee is charged annually and will be automatically renewed.\n\n" +
                        "Benefits:\n" +
                        "• List unlimited properties\n" +
                        "• Manage bookings\n" +
                        "• Access to host dashboard\n" +
                        "• Priority customer support", YEARLY_HOST_FEE))
                .setPositiveButton("Proceed to Payment", (dialog, which) -> {
                    // Here you would typically integrate with a payment gateway
                    // For now, we'll just proceed with registration
                    performRegistration();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void performRegistration() {
        if (binding == null) return;

        try {
            String name = binding.etName.getText().toString().trim();
            String email = binding.etEmail.getText().toString().trim();
            String password = binding.etPassword.getText().toString().trim();
            String confirmPassword = binding.etConfirmPassword.getText().toString().trim();
            String staffId = isAdminRegistration ? binding.etStaffId.getText().toString().trim() : "";

            if (!validateInputs(name, email, password, confirmPassword, staffId)) {
                return;
            }

            binding.btnRegister.setEnabled(false);
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, task -> {
                        if (binding != null) {
                            binding.btnRegister.setEnabled(true);
                        }
                        if (task.isSuccessful()) {
                            saveUserData(name, email, staffId);
                        } else {
                            Toast.makeText(RegistrationActivity.this,
                                    "Registration failed: " + task.getException().getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error during registration: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            if (binding != null) {
                binding.btnRegister.setEnabled(true);
            }
        }
    }

    private boolean validateInputs(String name, String email, String password,
                                 String confirmPassword, String staffId) {
        if (binding == null) return false;

        try {
            if (TextUtils.isEmpty(name)) {
                binding.etName.setError("Name is required");
                return false;
            }

            if (TextUtils.isEmpty(email)) {
                binding.etEmail.setError("Email is required");
                return false;
            }

            if (TextUtils.isEmpty(password)) {
                binding.etPassword.setError("Password is required");
                return false;
            }

            if (password.length() < 6) {
                binding.etPassword.setError("Password must be at least 6 characters");
                return false;
            }

            if (!password.equals(confirmPassword)) {
                binding.etConfirmPassword.setError("Passwords do not match");
                return false;
            }

            if (isAdminRegistration && TextUtils.isEmpty(staffId)) {
                binding.etStaffId.setError("Business ID is required");
                return false;
            }

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error validating inputs: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    private void saveUserData(String name, String email, String staffId) {
        try {
            String collection = isAdminRegistration ? "hosts" : "users";
            Map<String, Object> userData = new HashMap<>();
            userData.put("name", name);
            userData.put("email", email);
            userData.put("registrationDate", System.currentTimeMillis());
            
            if (isAdminRegistration) {
                userData.put("businessId", staffId);
                userData.put("yearlyFee", YEARLY_HOST_FEE);
                userData.put("status", "active");
                
                // Set subscription end date (1 year from now)
                Calendar calendar = Calendar.getInstance();
                calendar.add(Calendar.YEAR, 1);
                userData.put("subscriptionEndDate", calendar.getTimeInMillis());
            }

            db.collection(collection).document(email)
                    .set(userData)
                    .addOnSuccessListener(aVoid -> {
                        String message = isAdminRegistration ? 
                            "Host registration successful! You can now list your property." :
                            "Registration successful! You can now book homestays.";
                        Toast.makeText(RegistrationActivity.this, message, Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        if (binding != null) {
                            binding.btnRegister.setEnabled(true);
                        }
                        Toast.makeText(RegistrationActivity.this,
                                "Error saving user data: " + e.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    });
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error saving user data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            if (binding != null) {
                binding.btnRegister.setEnabled(true);
            }
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        getOnBackPressedDispatcher().onBackPressed();
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
} 