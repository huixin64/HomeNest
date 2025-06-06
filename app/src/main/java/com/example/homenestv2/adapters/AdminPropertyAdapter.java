package com.example.homenestv2.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ImageButton;
import android.widget.Button;
import android.widget.RatingBar;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.homenestv2.R;
import com.example.homenestv2.models.Property;
import com.google.android.material.card.MaterialCardView;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AdminPropertyAdapter extends RecyclerView.Adapter<AdminPropertyAdapter.PropertyViewHolder> {
    private Context context;
    private List<Property> propertyList;
    private OnPropertyActionListener listener;
    private boolean isSelectionMode = false;
    private NumberFormat currencyFormat;

    public interface OnPropertyActionListener {
        void onPropertyClick(Property property);
        void onPropertyEdit(Property property);
        void onPropertyDelete(Property property);
        void onSelectionChanged(int selectedCount);
    }

    public AdminPropertyAdapter(Context context, List<Property> propertyList, OnPropertyActionListener listener) {
        this.context = context;
        this.propertyList = propertyList;
        this.listener = listener;
        this.currencyFormat = NumberFormat.getCurrencyInstance(new Locale("en", "MY"));
    }

    @NonNull
    @Override
    public PropertyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_admin_property, parent, false);
        return new PropertyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PropertyViewHolder holder, int position) {
        Property property = propertyList.get(position);
        holder.bind(property);

        // Handle selection mode
        holder.selectCheckBox.setVisibility(isSelectionMode ? View.VISIBLE : View.GONE);
        holder.selectCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            property.setSelected(isChecked);
            if (listener != null) {
                int selectedCount = 0;
                for (Property p : propertyList) {
                    if (p.isSelected()) selectedCount++;
                }
                listener.onSelectionChanged(selectedCount);
            }
        });
    }

    @Override
    public int getItemCount() {
        return propertyList.size();
    }


    public void updateList(List<Property> newList) {
        if (newList != null) {
            this.propertyList = new ArrayList<>(newList);
            notifyDataSetChanged();
        }
    }

    public void setSelectionMode(boolean selectionMode) {
        this.isSelectionMode = selectionMode;
        notifyDataSetChanged();
    }

    class PropertyViewHolder extends RecyclerView.ViewHolder {
        ImageView propertyImageView;
        TextView propertyNameTextView;
        TextView propertyLocationTextView;
        TextView propertyPriceTextView;
        Button btnEdit, btnDelete, btnViewBookings;
        MaterialCardView propertyCardView;
        CheckBox selectCheckBox;
        RatingBar ratingBar;
        TextView ratingText;

        @SuppressLint("WrongViewCast")
        public PropertyViewHolder(@NonNull View itemView) {
            super(itemView);
            propertyImageView = itemView.findViewById(R.id.propertyImage);
            propertyNameTextView = itemView.findViewById(R.id.propertyName);
            propertyLocationTextView = itemView.findViewById(R.id.propertyLocation);
            propertyPriceTextView = itemView.findViewById(R.id.propertyPrice);
            btnEdit = itemView.findViewById(R.id.editButton);
            btnDelete = itemView.findViewById(R.id.deleteButton);
            btnViewBookings = itemView.findViewById(R.id.viewBookingsButton);
            propertyCardView = itemView.findViewById(R.id.propertyCardView);
            selectCheckBox = itemView.findViewById(R.id.selectCheckBox);
            ratingBar = itemView.findViewById(R.id.ratingBar);
            ratingText = itemView.findViewById(R.id.ratingText);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onPropertyClick(propertyList.get(position));
                }
            });

            btnEdit.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onPropertyEdit(propertyList.get(position));
                }
            });

            btnDelete.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onPropertyDelete(propertyList.get(position));
                }
            });
        }

        public void bind(Property property) {
            propertyNameTextView.setText(property.getName());
            propertyLocationTextView.setText(property.getLocation());
            propertyPriceTextView.setText("MYR " + currencyFormat.format(property.getPrice()));
            
            // Set rating
            float rating = (float) property.getRating();
            ratingBar.setRating(rating);
            ratingText.setText(String.format("(%.1f)", rating));

            if (property.getImageUrls() != null && !property.getImageUrls().isEmpty()) {
                Glide.with(context)
                        .load(property.getImageUrls().get(0))
                        .placeholder(R.drawable.placeholder_property)
                        .error(R.drawable.error_image)
                        .into(propertyImageView);
            } else {
                Glide.with(context)
                        .load(R.drawable.placeholder_property)
                        .into(propertyImageView);
            }
        }
    }
} 