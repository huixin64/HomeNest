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

public class BookingAdapter extends RecyclerView.Adapter<BookingAdapter.BookingViewHolder> {

    private List<Booking> bookings;
    private OnBookingClickListener listener;
    private SimpleDateFormat dateFormat;

    public interface OnBookingClickListener {
        void onBookingClick(Booking booking);
    }

    public BookingAdapter(List<Booking> bookings, OnBookingClickListener listener) {
        this.bookings = bookings;
        this.listener = listener;
        this.dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
    }

    @NonNull
    @Override
    public BookingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_booking, parent, false);
        return new BookingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookingViewHolder holder, int position) {
        Booking booking = bookings.get(position);
        holder.bind(booking);
    }

    @Override
    public int getItemCount() {
        return bookings.size();
    }

    class BookingViewHolder extends RecyclerView.ViewHolder {
        private TextView textViewGuestName;
        private TextView textViewDates;
        private TextView textViewStatus;
        private TextView textViewTotalAmount;

        BookingViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewGuestName = itemView.findViewById(R.id.textViewGuestName);
            textViewDates = itemView.findViewById(R.id.textViewDates);
            textViewStatus = itemView.findViewById(R.id.textViewStatus);
            textViewTotalAmount = itemView.findViewById(R.id.textViewTotalAmount);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    listener.onBookingClick(bookings.get(position));
                }
            });
        }

        void bind(Booking booking) {
            textViewGuestName.setText(booking.getGuestName());
            
            String dates = String.format(Locale.getDefault(), "Check-in: %s - Check-out: %s",
                    dateFormat.format(booking.getCheckInDate()),
                    dateFormat.format(booking.getCheckOutDate()));
            textViewDates.setText(dates);
            
            textViewStatus.setText(booking.getStatus());
            textViewTotalAmount.setText(String.format(Locale.getDefault(), "$%.2f", booking.getTotalAmount()));
        }
    }
} 