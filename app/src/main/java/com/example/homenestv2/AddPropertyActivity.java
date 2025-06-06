package com.example.homenestv2;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import com.example.homenestv2.databinding.ActivityAddPropertyBinding;
import com.example.homenestv2.models.Property;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.Timestamp;




public class AddPropertyActivity extends AppCompatActivity {

    private ActivityAddPropertyBinding binding;
    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private ProgressDialog progressDialog;
    private TextInputEditText etName, etDescription, etLocation, etPrice, etBedrooms, etBathrooms, etMaxGuests;
    private MaterialButton btnSave;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddPropertyBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        setupToolbar();
        initializeViews();
        setupSaveButton();

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Saving property...");
        progressDialog.setCancelable(false);
    }

    private void setupToolbar() {
        Toolbar toolbar = binding.toolbar;
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Add New Property");
        }
    }

    private void initializeViews() {
        etName = binding.etName;
        etDescription = binding.etDescription;
        etLocation = binding.etLocation;
        etPrice = binding.etPrice;
        etBedrooms = binding.etBedrooms;
        etBathrooms = binding.etBathrooms;
        etMaxGuests = binding.etMaxGuests;
        btnSave = binding.btnSave;

    }

    private void setupSaveButton() {
        btnSave.setOnClickListener(v -> saveProperty());
    }

    private void saveProperty() {
        String name = etName.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        String priceStr = etPrice.getText().toString().trim();
        String bedroomsStr = etBedrooms.getText().toString().trim();
        String bathroomsStr = etBathrooms.getText().toString().trim();
        String guestsStr = etMaxGuests.getText().toString().trim();
        String location = etLocation.getText().toString().trim();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(description) || TextUtils.isEmpty(priceStr)
                || TextUtils.isEmpty(bedroomsStr) || TextUtils.isEmpty(bathroomsStr) || TextUtils.isEmpty(guestsStr)
                || TextUtils.isEmpty(location)) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        double price = 0;
        int bedrooms = 0;
        int bathrooms = 0;
        int maxGuests = 0;

        try {
            price = Double.parseDouble(priceStr);
            bedrooms = Integer.parseInt(bedroomsStr);
            bathrooms = Integer.parseInt(bathroomsStr);
            maxGuests = Integer.parseInt(guestsStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Please enter valid numbers for price, bedrooms, bathrooms, and guests", Toast.LENGTH_SHORT).show();
            return;
        }


        progressDialog.show();

        String ownerId = auth.getCurrentUser() != null ? auth.getCurrentUser().getEmail() : null;
        if (ownerId == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
            return;
        }

        Property property = new Property();
        property.setId(db.collection("properties").document().getId()); // Generate a new ID
        property.setName(name);
        property.setDescription(description);
        property.setPrice(price);
        property.setBedrooms(bedrooms);
        property.setBathrooms(bathrooms);
        property.setMaxGuests(maxGuests);
        property.setLocation(location);
        property.setOwnerId(ownerId);
        property.setAvailable(true); // Assuming property is available when added
        property.setCreatedDate(Timestamp.now());
        property.setUpdatedDate(Timestamp.now());
        // Latitude and longitude can be added later if using a map picker
        property.setLatitude(0.0); // Placeholder
        property.setLongitude(0.0); // Placeholder

        Log.d("AddPropertyActivity", "Saving property with ownerId (email): " + ownerId + ", name: " + name + ", price: " + price);

        // Since the current layout doesn't have image selection or amenities,
        // we won't set imageUrls or amenities here.
        // If you add these to the layout, you'll need to update this logic.

        savePropertyToFirestore(property);

    }


    private void savePropertyToFirestore(Property property) {
        db.collection("properties").document(property.getId())
                .set(property)
                .addOnSuccessListener(aVoid -> {
                    progressDialog.dismiss();
                    Toast.makeText(this, "Property added successfully!", Toast.LENGTH_SHORT).show();
                    finish(); // Close activity after saving
                })
                .addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    Toast.makeText(this, "Error saving property: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
} 