package com.example.homenestv2;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.homenestv2.adapters.BookingAdapter;
import com.example.homenestv2.models.Booking;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class AdminBookingsActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private RecyclerView recyclerViewBookings;
    private TabLayout tabLayout;
    private List<Booking> bookingsList;
    private BookingAdapter bookingAdapter;
    private String currentStatus = "all";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_bookings);

        // Initialize Firebase instances
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Set up toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Bookings");

        // Initialize views
        initializeViews();
        setupRecyclerView();
        setupTabLayout();
        loadBookings();
    }

    private void initializeViews() {
        recyclerViewBookings = findViewById(R.id.recyclerViewBookings);
        tabLayout = findViewById(R.id.tabLayout);
        bookingsList = new ArrayList<>();
    }

    private void setupRecyclerView() {
        bookingAdapter = new BookingAdapter(bookingsList, this::onBookingClick);
        recyclerViewBookings.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewBookings.setAdapter(bookingAdapter);
    }

    private void setupTabLayout() {
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0:
                        currentStatus = "all";
                        break;
                    case 1:
                        currentStatus = "pending";
                        break;
                    case 2:
                        currentStatus = "confirmed";
                        break;
                    case 3:
                        currentStatus = "completed";
                        break;
                }
                loadBookings();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void loadBookings() {
        db.collection("bookings")
            .whereEqualTo("status", currentStatus.equals("all") ? null : currentStatus)
            .get()
            .addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    bookingsList.clear();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Booking booking = document.toObject(Booking.class);
                        booking.setId(document.getId());
                        bookingsList.add(booking);
                    }
                    bookingAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(AdminBookingsActivity.this, 
                        "Error loading bookings", Toast.LENGTH_SHORT).show();
                }
            });
    }

    private void onBookingClick(Booking booking) {
        // TODO: Implement booking click handling
        Toast.makeText(this, "Booking clicked: " + booking.getId(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}