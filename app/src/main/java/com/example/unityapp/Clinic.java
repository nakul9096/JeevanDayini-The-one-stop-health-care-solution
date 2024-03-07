package com.example.unityapp;

// Clinic.java
public class Clinic {
    private String name;
    private String address;
    private String imageUrl; // For image loading, you can use a URL or resource ID
    private float rating;
    private String location;

    public Clinic(String name, String address, String imageUrl, float rating, String location) {
        this.name = name;
        this.address = address;
        this.imageUrl = imageUrl;
        this.rating = rating;
        this.location = location;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
