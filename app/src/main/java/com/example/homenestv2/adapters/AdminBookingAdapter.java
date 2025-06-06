package com.example.homenestv2.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.homenestv2.R;
import com.example.homenestv2.models.Booking;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.Date;

public class AdminBookingAdapter extends RecyclerView.Adapter<AdminBookingAdapter.BookingViewHolder> {

    private List<Booking> bookingList;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());

    public AdminBookingAdapter(List<Booking> bookingList) {
        this.bookingList = bookingList;
    }

    @NonNull
    @Override
    public BookingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_admin_booking, parent, false);
        return new BookingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookingViewHolder holder, int position) {
        Booking booking = bookingList.get(position);
        
        holder.guestName.setText(booking.getGuestName());
        
        // Access property name through the nested Property object
        if (booking.getProperty() != null) {
             holder.propertyName.setText(booking.getProperty().getName());
        } else {
             holder.propertyName.setText("N/A"); // Or handle as appropriate
        }

        // Format dates
        Date checkInDate = booking.getCheckInDate();
        if (checkInDate != null) {
            holder.checkInDate.setText(dateFormat.format(checkInDate));
        } else {
            holder.checkInDate.setText("N/A");
        }

        Date checkOutDate = booking.getCheckOutDate();
        if (checkOutDate != null) {
            holder.checkOutDate.setText(dateFormat.format(checkOutDate));
        } else {
            holder.checkOutDate.setText("N/A");
        }

        // Use getTotalAmount() and format as currency
        holder.totalAmount.setText(String.format(Locale.getDefault(), "$%.2f", booking.getTotalAmount()));
        
        holder.status.setText(booking.getStatus());

        // Handle button clicks if needed
        // holder.editButton.setOnClickListener(...);
        // holder.cancelButton.setOnClickListener(...);
    }

    @Override
    public int getItemCount() {
        return bookingList.size();
    }

    static class BookingViewHolder extends RecyclerView.ViewHolder {
        TextView guestName;
        TextView propertyName;
        TextView checkInDate;
        TextView checkOutDate;
        TextView totalAmount;
        TextView status;
        // Add buttons if you have them in the layout
        // Button editButton;
        // Button cancelButton;

        BookingViewHolder(@NonNull View itemView) {
            super(itemView);
            guestName = itemView.findViewById(R.id.guestName);
            propertyName = itemView.findViewById(R.id.propertyName);
            checkInDate = itemView.findViewById(R.id.checkInDate);
            checkOutDate = itemView.findViewById(R.id.checkOutDate);
            totalAmount = itemView.findViewById(R.id.totalAmount);
            status = itemView.findViewById(R.id.status);
            // Initialize buttons if they exist
            // editButton = itemView.findViewById(R.id.editButton);
            // cancelButton = itemView.findViewById(R.id.cancelButton);
        }
    }
} 