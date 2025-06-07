package com.example.homenestv2;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class AdminDashboardActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        mAuth = FirebaseAuth.getInstance();

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);

        // Set up navigation drawer toggle
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);

        // Set header text based on user email
        View headerView = navigationView.getHeaderView(0);
        TextView navHeaderTextView = headerView.findViewById(R.id.textViewAdminEmail);
        String adminEmail = mAuth.getCurrentUser() != null ? mAuth.getCurrentUser().getEmail() : "Admin";
        navHeaderTextView.setText(adminEmail);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        Intent intent = null;

        if (id == R.id.nav_dashboard) {
            // Already on dashboard, just close drawer
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        } else if (id == R.id.nav_properties) {
            intent = new Intent(AdminDashboardActivity.this, AdminPropertiesActivity.class);
            String adminEmail = mAuth.getCurrentUser() != null ? mAuth.getCurrentUser().getEmail() : null;
            if (adminEmail != null) {
                intent.putExtra("admin_email", adminEmail);
                startActivity(intent);
            } else {
                Toast.makeText(this, "Error: User not logged in", Toast.LENGTH_SHORT).show();
            }
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        } else if (id == R.id.nav_bookings) {
            intent = new Intent(this, AdminBookingsActivity.class);
        } else if (id == R.id.nav_profile) {
            intent = new Intent(this, AdminProfileActivity.class);
            String adminEmail = mAuth.getCurrentUser() != null ? mAuth.getCurrentUser().getEmail() : null;
            if (adminEmail != null) {
                intent.putExtra("admin_email", adminEmail);
            } else {
                Toast.makeText(this, "Error: User not logged in", Toast.LENGTH_SHORT).show();
                return true;
            }
        } else if (id == R.id.nav_settings) {
            intent = new Intent(this, AdminProfileActivity.class);
        } else if (id == R.id.nav_logout) {
            mAuth.signOut();
            Intent loginIntent = new Intent(this, LoginActivity.class);
            loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(loginIntent);
            finish();
            return true;
        }

        if (intent != null) {
            startActivity(intent);
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}