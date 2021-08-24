package com.example.travelbuddy.Models;

public class Place {
    String placeName;
    String placeID;
    String address;
    String imageID;
    Double lng;
    Double lat;
    Double distance;
    int totalRating;
    Double rating;
    boolean isOpen;


    public Place() {
    }

    public Place(String placeName, String placeID, String address, String imageID, Double lng, Double lat, Double distance, int totalRating, Double rating, boolean isOpen) {
        this.placeName = placeName;
        this.placeID = placeID;
        this.address = address;
        this.imageID = imageID;
        this.lng = lng;
        this.lat = lat;
        this.distance = distance;
        this.totalRating = totalRating;
        this.rating = rating;
        this.isOpen = isOpen;
    }

    public Double getLng() {
        return lng;
    }

    public void setLng(Double lng) {
        this.lng = lng;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public String getImageID() {
        return imageID;
    }

    public void setImageID(String imageID) {
        this.imageID = imageID;
    }

    public String getPlaceName() {
        return placeName;
    }

    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }

    public String getPlaceID() {
        return placeID;
    }

    public void setPlaceID(String placeID) {
        this.placeID = placeID;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Double getDistance() {
        return distance;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }

    public int getTotalRating() {
        return totalRating;
    }

    public void setTotalRating(int totalRating) {
        this.totalRating = totalRating;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public boolean isOpen() {
        return isOpen;
    }

    public void setOpen(boolean open) {
        isOpen = open;
    }
}
