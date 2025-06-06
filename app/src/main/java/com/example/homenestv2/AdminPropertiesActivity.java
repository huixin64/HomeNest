package com.example.homenestv2;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import com.example.homenestv2.adapters.AdminPropertyAdapter;
import com.example.homenestv2.models.Property;

import java.util.ArrayList;
import java.util.List;

public class AdminPropertiesActivity extends AppCompatActivity implements AdminPropertyAdapter.OnPropertyActionListener {
    private static final String TAG = "AdminPropertiesActivity";
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private String adminEmail;
    private RecyclerView recyclerView;
    private AdminPropertyAdapter adapter;
    private List<Property> propertiesList;
    private FloatingActionButton fabAddProperty;
    private MaterialCardView emptyStateCard;
    private TextView tvEmptyStateTitle, tvEmptyStateMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_properties);

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        
        // Get admin data from intent
        adminEmail = getIntent().getStringExtra("admin_email");
        Log.d(TAG, "Admin email: " + adminEmail);

        setupToolbar();
        initializeViews();
        setupRecyclerView();
        setupAddPropertyButton();
        loadProperties();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Manage Properties");
        }
    }

    private void initializeViews() {
        recyclerView = findViewById(R.id.recyclerView);
        emptyStateCard = findViewById(R.id.emptyStateCard);
        tvEmptyStateTitle = findViewById(R.id.tvEmptyStateTitle);
        tvEmptyStateMessage = findViewById(R.id.tvEmptyStateMessage);
        fabAddProperty = findViewById(R.id.fabAddProperty);
    }

    private void setupRecyclerView() {
        propertiesList = new ArrayList<>();
        adapter = new AdminPropertyAdapter(this, propertiesList, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    private void setupAddPropertyButton() {
        fabAddProperty.setOnClickListener(v -> {
            Intent intent = new Intent(this, com.example.homenestv2.AddPropertyActivity.class);
            intent.putExtra("admin_email", adminEmail);
            startActivity(intent);
        });
    }

    private void loadProperties() {
        Log.d("nav_enter_loadProperties", "1 ");
        if (adminEmail != null) {
            Log.d(TAG, "Loading properties for admin: " + adminEmail);
            db.collection("properties")
                    .whereEqualTo("ownerId", adminEmail)
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        Log.d(TAG, "Firestore query successful. Number of documents: " + queryDocumentSnapshots.size());
                        propertiesList.clear();
                        Log.d(TAG, "propertiesList cleared. Size: " + propertiesList.size());

                        if (queryDocumentSnapshots.isEmpty()) {
                            Log.d(TAG, "No properties found for this admin.");
                        } else {
                            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                                Property property = document.toObject(Property.class);
                                property.setId(document.getId());
                                propertiesList.add(property);
                                Log.d(TAG, "Added property: " + property.getName() + " (ID: " + property.getId() + ")");
                            }
                            Log.d(TAG, "Total properties added: " + propertiesList.size());
                        }

                        // Create a new ArrayList to ensure we're passing a fresh copy
                        List<Property> newList = new ArrayList<>(propertiesList);
                        Log.d(TAG, "Updating adapter with " + newList.size() + " properties");
                        adapter.updateList(newList);
                        Log.d(TAG, "Adapter updateList called with " + newList.size() + " items");

                        updateEmptyState();
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Error loading properties from Firestore", e);
                        Toast.makeText(this, "Error loading properties: " + e.getMessage(), 
                                Toast.LENGTH_SHORT).show();
                    });
        } else
        {Log.e(TAG, "Admin email is null, cannot load properties");
            Toast.makeText(this, "Error: Admin email not found", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateEmptyState(){
        Log.d(TAG, "updateEmptyState: propertiesList size = " + propertiesList.size());
        if (propertiesList.isEmpty()) {
            Log.d(TAG, "updateEmptyState: propertiesList is empty. Showing empty state.");
            emptyStateCard.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            tvEmptyStateTitle.setText("No Properties Yet");
            tvEmptyStateMessage.setText("Start by adding your first property to begin hosting guests");
        } else {
            Log.d(TAG, "updateEmptyState: propertiesList is not empty. Showing RecyclerView.");
            Log.d("debug","enter_emptyStateCard");
            emptyStateCard.setVisibility(View.GONE);
            Log.d("debug","exit_emptyStateCard");
            Log.d("debug","enter_recyclerView");
            recyclerView.setVisibility(View.VISIBLE);
            Log.d("debug","exit_recyclerView");
        }
    }

    @Override
    public void onPropertyClick(Property property) {
        Intent intent = new Intent(this, PropertyDetailsActivity.class);
        intent.putExtra("property_id", property.getId());
        intent.putExtra("admin_email", adminEmail);
        startActivity(intent);
    }

    @Override
    public void onPropertyEdit(Property property) {
        Intent intent = new Intent(this, EditPropertyActivity.class);
        intent.putExtra("propertyId", property.getId());
        intent.putExtra("admin_email", adminEmail);
        startActivity(intent);
    }

    @Override
    public void onPropertyDelete(Property property) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Property")
                .setMessage("Are you sure you want to delete \"" + property.getName() + "\"? This action cannot be undone.")
                .setPositiveButton("Delete", (dialog, which) -> {
                    deleteProperty(property);
                })
                .setNegativeButton("Cancel", null)
                .setIcon(R.drawable.ic_warning)
                .show();
    }

    private void deleteProperty(Property property) {
        // Show loading
        fabAddProperty.setEnabled(false);
        
        db.collection("properties").document(property.getId())
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Property deleted successfully", Toast.LENGTH_SHORT).show();
                    loadProperties(); // Refresh the list
                    fabAddProperty.setEnabled(true);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error deleting property: " + e.getMessage(), 
                            Toast.LENGTH_SHORT).show();
                    fabAddProperty.setEnabled(true);
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadProperties(); // Refresh properties when returning from other activities
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            navigateBackToDashboard();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        navigateBackToDashboard();
    }

    private void navigateBackToDashboard() {
        Intent intent = new Intent(this, AdminDashboardActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }

    @Override
    public void onSelectionChanged(int selectedCount) {
        // Handle selection changes if needed
    }
}