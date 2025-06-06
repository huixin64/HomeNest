package com.example.homenestv2;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class UserDashboardActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_dashboard);

        mAuth = FirebaseAuth.getInstance();

        bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            // Handle bottom navigation item clicks
            // Since fragments are deleted, we can add placeholder logic or remove navigation
            int itemId = item.getItemId();
            if (itemId == R.id.navigation_home) {
                // Placeholder for Home
                return true;
            } else if (itemId == R.id.navigation_search) {
                // Placeholder for Search
                return true;
            } else if (itemId == R.id.navigation_bookings) {
                // Placeholder for Bookings
                return true;
            } else if (itemId == R.id.navigation_profile) {
                // Placeholder for Profile or logout
                 mAuth.signOut();
                 Intent loginIntent = new Intent(UserDashboardActivity.this, LoginActivity.class);
                 loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                 startActivity(loginIntent);
                 finish();
                return true;
            }
            return false;
        });

        // Optionally show a default placeholder or message since fragments are removed
        // TextView placeholderText = findViewById(R.id.placeholderText);
        // placeholderText.setText("User dashboard - Fragments removed");
    }
} 