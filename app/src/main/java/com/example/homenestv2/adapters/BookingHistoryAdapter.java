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

public class BookingHistoryAdapter extends RecyclerView.Adapter<BookingHistoryAdapter.ViewHolder> {

    private List<Booking> bookingList;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());

    public BookingHistoryAdapter(List<Booking> bookingList) {
        this.bookingList = bookingList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_booking_history, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Booking booking = bookingList.get(position);
        
        // Access property name through the nested Property object
        if (booking.getProperty() != null) {
            holder.propertyNameTextView.setText(booking.getProperty().getName());
        } else {
            holder.propertyNameTextView.setText("N/A"); // Or handle as appropriate
        }
        
        // Format booking date (using check-in date as booking date for now)
        Date bookingDate = booking.getCheckInDate();
        if (bookingDate != null) {
             holder.bookingDateTextView.setText(dateFormat.format(bookingDate));
        } else {
             holder.bookingDateTextView.setText("N/A");
        }
        
        holder.bookingStatusTextView.setText(booking.getStatus());
        
        // Use getTotalAmount() and format as currency
        holder.totalAmountTextView.setText(String.format(Locale.getDefault(), "$%.2f", booking.getTotalAmount()));
    }

    @Override
    public int getItemCount() {
        return bookingList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView propertyNameTextView;
        TextView bookingDateTextView;
        TextView bookingStatusTextView;
        TextView totalAmountTextView;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            propertyNameTextView = itemView.findViewById(R.id.propertyNameTextView);
            bookingDateTextView = itemView.findViewById(R.id.bookingDateTextView);
            bookingStatusTextView = itemView.findViewById(R.id.bookingStatusTextView);
            totalAmountTextView = itemView.findViewById(R.id.totalAmountTextView);
        }
    }
} 