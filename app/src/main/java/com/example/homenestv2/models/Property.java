package com.example.homenestv2.models;

import com.google.firebase.Timestamp;

import java.util.ArrayList;
import java.util.List;

public class Property {
    private String id;
    private String name;
    private String description;
    private String location;
    private double price;
    private double rating;
    private int reviewCount;
    private int bedrooms;
    private int bathrooms;
    private int maxGuests;
    private List<String> imageUrls;
    private String ownerId;
    private boolean isAvailable;
    private List<String> amenities;
    private String propertyType;
    private String address;
    private String city;
    private String state;
    private String country;
    private String zipCode;
    private Timestamp createdDate;
    private Timestamp updatedDate;
    private double latitude;
    private double longitude;
    private boolean isSelected;
    private int totalBookings;

    // Default constructor for Firestore
    public Property() {
        this.imageUrls = new ArrayList<>();
        this.amenities = new ArrayList<>();
        this.isAvailable = true;
        this.rating = 0.0;
        this.reviewCount = 0;
        this.totalBookings = 0;
    }

    // Getters
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getLocation() {
        return location;
    }

    public double getPrice() {
        return price;
    }

    public double getRating() {
        return rating;
    }

    public int getReviewCount() {
        return reviewCount;
    }

    public int getBedrooms() {
        return bedrooms;
    }

    public int getBathrooms() {
        return bathrooms;
    }

    public int getMaxGuests() {
        return maxGuests;
    }

    public List<String> getImageUrls() {
        return imageUrls;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public List<String> getAmenities() {
        return amenities;
    }

    public String getPropertyType() {
        return propertyType;
    }

    public String getAddress() {
        return address;
    }

    public String getCity() {
        return city;
    }

    public String getState() {
        return state;
    }

    public String getCountry() {
        return country;
    }

    public String getZipCode() {
        return zipCode;
    }

    public Timestamp getCreatedDate() {
        return createdDate;
    }

    public Timestamp getUpdatedDate() {
        return updatedDate;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public int getTotalBookings() {
        return totalBookings;
    }

    // Setters
    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public void setReviewCount(int reviewCount) {
        this.reviewCount = reviewCount;
    }

    public void setBedrooms(int bedrooms) {
        this.bedrooms = bedrooms;
    }

    public void setBathrooms(int bathrooms) {
        this.bathrooms = bathrooms;
    }

    public void setMaxGuests(int maxGuests) {
        this.maxGuests = maxGuests;
    }

    public void setImageUrls(List<String> imageUrls) {
        this.imageUrls = imageUrls;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }

    public void setAmenities(List<String> amenities) {
        this.amenities = amenities;
    }

    public void setPropertyType(String propertyType) {
        this.propertyType = propertyType;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setState(String state) {
        this.state = state;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public void setCreatedDate(Timestamp createdDate) {
        this.createdDate = createdDate;
    }

    public void setUpdatedDate(Timestamp updatedDate) {
        this.updatedDate = updatedDate;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public void setTotalBookings(int totalBookings) {
        this.totalBookings = totalBookings;
    }

    // Helper methods
    public String getFullAddress() {
        StringBuilder fullAddress = new StringBuilder();
        if (address != null && !address.trim().isEmpty()) {
            fullAddress.append(address);
        }
        if (city != null && !city.trim().isEmpty()) {
            if (fullAddress.length() > 0) fullAddress.append(", ");
            fullAddress.append(city);
        }
        if (state != null && !state.trim().isEmpty()) {
            if (fullAddress.length() > 0) fullAddress.append(", ");
            fullAddress.append(state);
        }
        if (country != null && !country.trim().isEmpty()) {
            if (fullAddress.length() > 0) fullAddress.append(", ");
            fullAddress.append(country);
        }
        return fullAddress.toString();
    }

    public String getMainImageUrl() {
        return (imageUrls != null && !imageUrls.isEmpty()) ? imageUrls.get(0) : null;
    }

    public String getFormattedPrice() {
        return String.format("$%.2f", price);
    }

    public String getPropertyDetails() {
        return bedrooms + " bed • " + bathrooms + " bath • " + maxGuests + " guests";
    }
} 
 