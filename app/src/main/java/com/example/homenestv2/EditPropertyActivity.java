package com.example.homenestv2;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.homenestv2.models.Property;
import com.google.firebase.firestore.FirebaseFirestore;

public class EditPropertyActivity extends AppCompatActivity {
    private FirebaseFirestore db;
    private String propertyId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_property);

        // Initialize Firebase
        db = FirebaseFirestore.getInstance();

        // Setup toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Edit Property");

        // Get property ID from intent
        propertyId = getIntent().getStringExtra("propertyId");
        if (propertyId != null) {
            loadProperty();
        } else {
            Toast.makeText(this, "Error: Property ID not found", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void loadProperty() {
        db.collection("properties")
                .document(propertyId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    Property property = documentSnapshot.toObject(Property.class);
                    if (property != null) {
                        // TODO: Populate UI with property data
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error loading property", Toast.LENGTH_SHORT).show();
                    finish();
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