package com.example.travelbuddy;

public class ItineraryTask {

    String itineraryActivity;
    String itineraryRating;
    String itineraryPrice;
    String itineraryKey;
    String itineraryDate;
    String itineraryPhotoRef;
    String itineraryCity;

    public ItineraryTask(){

    }

    public ItineraryTask(String itineraryActivity, String itineraryRating, String itineraryPrice, String itineraryKey, String itineraryDate, String itineraryPhotoRef) {
        this.itineraryActivity = itineraryActivity;
        this.itineraryRating = itineraryRating;
        this.itineraryPrice = itineraryPrice;
        this.itineraryKey = itineraryKey;
        this.itineraryDate = itineraryDate;
        this.itineraryPhotoRef = itineraryPhotoRef;
    }

    public String getItineraryKey() {
        return itineraryKey;
    }

    public String getItineraryActivity() {
        return itineraryActivity;
    }

    public String getItineraryRating() {
        return itineraryRating;
    }

    public String getItineraryPrice() { return itineraryPrice; }

    public String getItineraryDate() { return itineraryDate; }

    public String getItineraryPhotoRef() { return itineraryPhotoRef; }

    public String getItineraryCity() { return itineraryCity; }


}