package com.example.homenestv2;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class HelpSupportActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private TextInputLayout tilSubject;
    private TextInputLayout tilMessage;
    private TextInputEditText etSubject;
    private TextInputEditText etMessage;
    private MaterialButton btnSubmit;
    private MaterialButton btnContactSupport;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_support);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        setupToolbar();
        initializeViews();
        setupClickListeners();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Help & Support");
        }
    }

    private void initializeViews() {
        tilSubject = findViewById(R.id.tilSubject);
        tilMessage = findViewById(R.id.tilMessage);
        etSubject = findViewById(R.id.etSubject);
        etMessage = findViewById(R.id.etMessage);
        btnSubmit = findViewById(R.id.btnSubmit);
        btnContactSupport = findViewById(R.id.btnContactSupport);
    }

    private void setupClickListeners() {
        btnSubmit.setOnClickListener(v -> {
            if (validateInputs()) {
                submitSupportRequest();
            }
        });

        btnContactSupport.setOnClickListener(v -> {
            String email = "support@homenest.com";
            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setData(Uri.parse("mailto:" + email));
            intent.putExtra(Intent.EXTRA_SUBJECT, "HomeNest Support Request");
            startActivity(intent);
        });
    }

    private boolean validateInputs() {
        boolean isValid = true;

        if (etSubject.getText().toString().trim().isEmpty()) {
            tilSubject.setError("Subject is required");
            isValid = false;
        } else {
            tilSubject.setError(null);
        }

        if (etMessage.getText().toString().trim().isEmpty()) {
            tilMessage.setError("Message is required");
            isValid = false;
        } else {
            tilMessage.setError(null);
        }

        return isValid;
    }

    private void submitSupportRequest() {
        String adminEmail = mAuth.getCurrentUser().getEmail();
        String subject = etSubject.getText().toString().trim();
        String message = etMessage.getText().toString().trim();

        Map<String, Object> supportRequest = new HashMap<>();
        supportRequest.put("adminEmail", adminEmail);
        supportRequest.put("subject", subject);
        supportRequest.put("message", message);
        supportRequest.put("timestamp", System.currentTimeMillis());
        supportRequest.put("status", "pending");

        db.collection("support_requests")
                .add(supportRequest)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "Support request submitted successfully", 
                            Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error submitting request: " + e.getMessage(), 
                            Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
} 