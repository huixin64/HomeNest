package com.example.homenestv2;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.homenestv2.models.Property;
import com.google.firebase.firestore.FirebaseFirestore;

public class EditPropertyActivity extends AppCompatActivity {
    private FirebaseFirestore db;
    private String propertyId;
    private String adminEmail;
    
    private EditText etName, etLocation, etDescription, etPrice, etBedrooms, etBathrooms;
    private Button btnSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_property);

        // Initialize Firebase
        db = FirebaseFirestore.getInstance();

        // Setup toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Edit Property");
        }

        // Initialize views
        initializeViews();

        // Get property ID and admin email from intent
        propertyId = getIntent().getStringExtra("propertyId");
        adminEmail = getIntent().getStringExtra("admin_email");

        if (propertyId != null && adminEmail != null) {
            loadProperty();
        } else {
            Toast.makeText(this, "Error: Required data not found", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void initializeViews() {
        etName = findViewById(R.id.etPropertyName);
        etLocation = findViewById(R.id.etLocation);
        etDescription = findViewById(R.id.etDescription);
        etPrice = findViewById(R.id.etPrice);
        etBedrooms = findViewById(R.id.etBedrooms);
        etBathrooms = findViewById(R.id.etBathrooms);
        btnSave = findViewById(R.id.btnSave);

        btnSave.setOnClickListener(v -> saveProperty());
    }

    private void loadProperty() {
        db.collection("properties")
                .document(propertyId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    Property property = documentSnapshot.toObject(Property.class);
                    if (property != null) {
                        populateUI(property);
                    } else {
                        Toast.makeText(this, "Error: Property not found", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error loading property: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    finish();
                });
    }

    private void populateUI(Property property) {
        etName.setText(property.getName());
        etLocation.setText(property.getLocation());
        etDescription.setText(property.getDescription());
        etPrice.setText(String.format("%.2f", property.getPrice()));
        etBedrooms.setText(String.valueOf(property.getBedrooms()));
        etBathrooms.setText(String.valueOf(property.getBathrooms()));
    }

    private void saveProperty() {
        // Get values from UI
        String name = etName.getText().toString().trim();
        String location = etLocation.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        String priceStr = etPrice.getText().toString().trim();
        String bedroomsStr = etBedrooms.getText().toString().trim();
        String bathroomsStr = etBathrooms.getText().toString().trim();

        // Validate inputs
        if (name.isEmpty() || location.isEmpty() || description.isEmpty() || 
            priceStr.isEmpty() || bedroomsStr.isEmpty() || bathroomsStr.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        double price;
        int bedrooms, bathrooms;
        try {
            price = Double.parseDouble(priceStr);
            // Round price to 2 decimal places
            price = Math.round(price * 100.0) / 100.0;
            bedrooms = Integer.parseInt(bedroomsStr);
            bathrooms = Integer.parseInt(bathroomsStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Please enter valid numbers for price, bedrooms, and bathrooms", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create property object with updated values
        Property updatedProperty = new Property();
        updatedProperty.setName(name);
        updatedProperty.setLocation(location);
        updatedProperty.setDescription(description);
        updatedProperty.setPrice(price);
        updatedProperty.setBedrooms(bedrooms);
        updatedProperty.setBathrooms(bathrooms);
        updatedProperty.setOwnerId(adminEmail);

        // Save to Firestore
        db.collection("properties")
                .document(propertyId)
                .set(updatedProperty)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Property updated successfully", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error updating property: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        finish();
    }
} 