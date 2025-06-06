package com.example.homenestv2.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.homenestv2.R;
import com.example.homenestv2.PropertyDetailsActivity;
import com.example.homenestv2.models.Property;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class PropertiesAdapter extends RecyclerView.Adapter<PropertiesAdapter.PropertyViewHolder> {
    private List<Property> properties;
    private Context context;
    private OnPropertyClickListener listener;
    private NumberFormat currencyFormat;

    public interface OnPropertyClickListener {
        void onPropertyClick(Property property);
        void onPropertyEdit(Property property);
        void onPropertyDelete(Property property);
    }

    public PropertiesAdapter(Context context, List<Property> properties, OnPropertyClickListener listener) {
        this.context = context;
        this.properties = properties;
        this.listener = listener;
        this.currencyFormat = NumberFormat.getCurrencyInstance(Locale.US);
    }

    @NonNull
    @Override
    public PropertyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_property, parent, false);
        return new PropertyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PropertyViewHolder holder, int position) {
        Property property = properties.get(position);
        
        // Set property details
        holder.tvPropertyName.setText(property.getName());
        holder.tvLocation.setText(property.getLocation());
        holder.tvPrice.setText(currencyFormat.format(property.getPrice()) + "/night");
        holder.tvRating.setText(String.format("%.1f", property.getRating()));
        holder.tvReviewCount.setText("(" + property.getReviewCount() + ")");
        
        // Load property image
        if (property.getImageUrls() != null && !property.getImageUrls().isEmpty()) {
            Glide.with(context)
                .load(property.getImageUrls().get(0))
                .placeholder(R.drawable.placeholder_property)
                .error(R.drawable.error_image)
                .into(holder.ivPropertyImage);
        } else {
            Glide.with(context)
                .load(R.drawable.placeholder_property)
                .into(holder.ivPropertyImage);
        }

        // Set click listeners
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onPropertyClick(property);
            }
        });

        holder.btnEdit.setOnClickListener(v -> {
            if (listener != null) {
                listener.onPropertyEdit(property);
            }
        });

        holder.btnDelete.setOnClickListener(v -> {
            if (listener != null) {
                listener.onPropertyDelete(property);
            }
        });
    }

    @Override
    public int getItemCount() {
        return properties != null ? properties.size() : 0;
    }

    public void updateProperties(List<Property> newProperties) {
        this.properties = newProperties;
        notifyDataSetChanged();
    }

    static class PropertyViewHolder extends RecyclerView.ViewHolder {
        ImageView ivPropertyImage;
        TextView tvPropertyName;
        TextView tvLocation;
        TextView tvPrice;
        TextView tvRating;
        TextView tvReviewCount;
        View btnEdit;
        View btnDelete;

        PropertyViewHolder(@NonNull View itemView) {
            super(itemView);
            ivPropertyImage = itemView.findViewById(R.id.ivPropertyImage);
            tvPropertyName = itemView.findViewById(R.id.tvPropertyName);
            tvLocation = itemView.findViewById(R.id.tvLocation);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvRating = itemView.findViewById(R.id.tvRating);
            tvReviewCount = itemView.findViewById(R.id.tvReviewCount);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
} 