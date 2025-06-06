package com.example.homenestv2;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.homenestv2.adapters.PropertyImageAdapter;
import com.example.homenestv2.databinding.ActivityPropertyDetailsBinding;
import com.example.homenestv2.models.Property;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Locale;

public class PropertyDetailsActivity extends AppCompatActivity implements PropertyImageAdapter.OnImageActionListener {

    private ActivityPropertyDetailsBinding binding;
    private FirebaseFirestore db;
    private String propertyId;

    private ImageView ivMainImage;
    private TextView tvPrice;
    private TextView tvLocation;
    private TextView tvRating;
    private TextView tvReviewCount;
    private TextView tvDescription;
    private TextView tvBedrooms;
    private TextView tvBathrooms;
    private TextView tvMaxGuests;
    private RecyclerView rvImages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPropertyDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db = FirebaseFirestore.getInstance();

        propertyId = getIntent().getStringExtra("propertyId");

        setupToolbar();
        initializeViews();

        if (propertyId != null) {
            loadPropertyDetails(propertyId);
        } else {
            Toast.makeText(this, "Property ID not found", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void setupToolbar() {
        Toolbar toolbar = binding.toolbar;
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void initializeViews() {
        ivMainImage = binding.ivMainImage;
        tvPrice = binding.tvPrice;
        tvLocation = binding.tvLocation;
        tvRating = binding.tvRating;
        tvReviewCount = binding.tvReviewCount;
        tvDescription = binding.tvDescription;
        tvBedrooms = binding.tvBedrooms;
        tvBathrooms = binding.tvBathrooms;
        tvMaxGuests = binding.tvMaxGuests;
        rvImages = binding.rvImages;

        rvImages.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
    }

    private void loadPropertyDetails(String propertyId) {
        db.collection("properties").document(propertyId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Property property = documentSnapshot.toObject(Property.class);
                        if (property != null) {
                            displayPropertyDetails(property);
                        } else {
                            Toast.makeText(this, "Failed to parse property data", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    } else {
                        Toast.makeText(this, "Property not found", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error loading property details: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    finish();
                });
    }

    private void displayPropertyDetails(Property property) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(property.getName());
        }
        tvPrice.setText(String.format(Locale.getDefault(), "$%.2f/night", property.getPrice()));
        tvLocation.setText(property.getLocation());
        tvRating.setText(String.format(Locale.getDefault(), "%.1f", property.getRating()));
        tvReviewCount.setText("(" + property.getReviewCount() + ")");
        tvDescription.setText(property.getDescription());
        tvBedrooms.setText(String.valueOf(property.getBedrooms()));
        tvBathrooms.setText(String.valueOf(property.getBathrooms()));
        tvMaxGuests.setText(String.valueOf(property.getMaxGuests()));

        // Display images
        if (property.getImageUrls() != null && !property.getImageUrls().isEmpty()) {
             PropertyImageAdapter imageAdapter = new PropertyImageAdapter(property.getImageUrls(), this);
             rvImages.setAdapter(imageAdapter);
             // Load the first image into the main ImageView as a fallback/preview
             Glide.with(this)
                .load(property.getImageUrls().get(0))
                .placeholder(R.drawable.placeholder_image)
                .error(R.drawable.error_image)
                .into(ivMainImage);
        } else {
            // Load a default image or hide the ImageView if no images are available
             Glide.with(this)
                .load(R.drawable.placeholder_image)
                .into(ivMainImage);
            rvImages.setVisibility(View.GONE);
        }
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

    @Override
    public void onImageClick(String imageUrl) {
        Toast.makeText(this, "Image clicked: " + imageUrl, Toast.LENGTH_SHORT).show();
    }
} 