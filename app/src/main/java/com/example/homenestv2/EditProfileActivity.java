package com.example.homenestv2;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

public class EditProfileActivity extends AppCompatActivity {
    private static final String TAG = "EditProfileActivity";
    private static final int PERMISSION_REQUEST_CODE = 1001;
    
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private String adminEmail;
    private Uri selectedImageUri;

    // UI Components
    private ShapeableImageView ivProfileImage;
    private TextInputLayout tilName;
    private TextInputEditText etName;
    private MaterialButton btnSave;
    private MaterialButton btnSelectImage;

    private final ActivityResultLauncher<Intent> imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    selectedImageUri = result.getData().getData();
                    if (selectedImageUri != null) {
                        try {
                            Glide.with(this)
                                    .load(selectedImageUri)
                                    .into(ivProfileImage);
                        } catch (Exception e) {
                            Log.e(TAG, "Error loading image: " + e.getMessage());
                            Toast.makeText(this, "Error loading image", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });

    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    openImagePicker();
                } else {
                    Toast.makeText(this, "Permission required to select image", Toast.LENGTH_SHORT).show();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        
        // Get admin email from intent
        adminEmail = getIntent().getStringExtra("admin_email");
        if (adminEmail == null) {
            Toast.makeText(this, "Error: Admin email not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

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
            getSupportActionBar().setTitle("Edit Profile");
        }
    }

    private void initializeViews() {
        ivProfileImage = findViewById(R.id.ivProfileImage);
        tilName = findViewById(R.id.tilName);
        etName = findViewById(R.id.etName);
        btnSave = findViewById(R.id.btnSave);
        btnSelectImage = findViewById(R.id.btnSelectImage);
    }

    private void setupClickListeners() {
        btnSelectImage.setOnClickListener(v -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                // Android 13 and above - use photo picker
                openImagePicker();
            } else {
                // Below Android 13 - check storage permission
                if (checkStoragePermission()) {
                    openImagePicker();
                } else {
                    requestStoragePermission();
                }
            }
        });

        btnSave.setOnClickListener(v -> {
            if (validateInputs()) {
                saveProfile();
            }
        });
    }

    private boolean checkStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return true; // No permission needed for photo picker
        }
        return ContextCompat.checkSelfPermission(this, 
                Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            openImagePicker();
        } else {
            requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
    }

    private void openImagePicker() {
        Intent intent;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Use the new photo picker API
            intent = new Intent(MediaStore.ACTION_PICK_IMAGES);
        } else {
            // Use the legacy image picker
            intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
        }
        imagePickerLauncher.launch(intent);
    }

    private void loadAdminData() {
        if (adminEmail != null) {
            db.collection("admins")
                    .document(adminEmail)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String name = documentSnapshot.getString("name");
                            String profileImageUrl = documentSnapshot.getString("profileImageUrl");

                            etName.setText(name);

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
        }
    }

    private boolean validateInputs() {
        boolean isValid = true;

        if (etName.getText().toString().trim().isEmpty()) {
            tilName.setError("Name is required");
            isValid = false;
        } else {
            tilName.setError(null);
        }

        return isValid;
    }

    private void saveProfile() {
        btnSave.setEnabled(false);
        btnSave.setText("Saving...");

        if (selectedImageUri != null) {
            try {
                // Create a unique filename using timestamp
                String timestamp = String.valueOf(System.currentTimeMillis());
                String sanitizedEmail = adminEmail.replaceAll("[^a-zA-Z0-9]", "_");
                String imageFileName = "profile_" + sanitizedEmail + "_" + timestamp + ".jpg";
                
                // Create storage reference with a simpler path
                StorageReference storageRef = storage.getReference();
                StorageReference profileImagesRef = storageRef.child("profile_images").child(imageFileName);

                Log.d(TAG, "Starting upload to path: " + profileImagesRef.getPath());
                Log.d(TAG, "Selected image URI: " + selectedImageUri.toString());

                // Get the file extension
                String mimeType = getContentResolver().getType(selectedImageUri);
                Log.d(TAG, "Image MIME type: " + mimeType);

                // Upload the image with metadata
                UploadTask uploadTask = profileImagesRef.putFile(selectedImageUri);
                
                uploadTask.addOnProgressListener(taskSnapshot -> {
                    double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                    Log.d(TAG, "Upload progress: " + progress + "%");
                })
                .addOnSuccessListener(taskSnapshot -> {
                    Log.d(TAG, "Upload successful, getting download URL");
                    profileImagesRef.getDownloadUrl()
                            .addOnSuccessListener(uri -> {
                                String downloadUrl = uri.toString();
                                Log.d(TAG, "Download URL obtained: " + downloadUrl);
                                updateProfileData(downloadUrl);
                            })
                            .addOnFailureListener(e -> {
                                Log.e(TAG, "Error getting download URL: " + e.getMessage());
                                handleUploadError("Error getting image URL: " + e.getMessage());
                            });
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error uploading image: " + e.getMessage());
                    Log.e(TAG, "Storage path: " + profileImagesRef.getPath());
                    Log.e(TAG, "Image URI: " + selectedImageUri.toString());
                    
                    // Try to get more information about the error
                    if (e.getMessage() != null) {
                        Log.e(TAG, "Error message: " + e.getMessage());
                    }
                    if (e.getCause() != null) {
                        Log.e(TAG, "Error cause: " + e.getCause().getMessage());
                    }
                    
                    handleUploadError("Error uploading image: " + e.getMessage());
                });
            } catch (Exception e) {
                Log.e(TAG, "Exception during upload setup: " + e.getMessage());
                if (e.getCause() != null) {
                    Log.e(TAG, "Exception cause: " + e.getCause().getMessage());
                }
                handleUploadError("Error preparing image upload: " + e.getMessage());
            }
        } else {
            // Update profile without changing image
            updateProfileData(null);
        }
    }

    private void handleUploadError(String errorMessage) {
        runOnUiThread(() -> {
            Toast.makeText(EditProfileActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
            btnSave.setEnabled(true);
            btnSave.setText("Save");
        });
    }

    private void updateProfileData(String profileImageUrl) {
        Map<String, Object> updates = new HashMap<>();
        updates.put("name", etName.getText().toString().trim());
        if (profileImageUrl != null) {
            updates.put("profileImageUrl", profileImageUrl);
        }

        db.collection("admins")
                .document(adminEmail)
                .update(updates)
                .addOnSuccessListener(aVoid -> {
                    runOnUiThread(() -> {
                        Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                        finish();
                    });
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error updating profile: " + e.getMessage());
                    runOnUiThread(() -> {
                        Toast.makeText(this, "Error updating profile: " + e.getMessage(), 
                                Toast.LENGTH_SHORT).show();
                        btnSave.setEnabled(true);
                        btnSave.setText("Save");
                    });
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