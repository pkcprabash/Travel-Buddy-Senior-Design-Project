package com.example.travelbuddy.Helper;

import com.google.android.gms.maps.model.LatLng;

public class DistanceCalculator {

    //referense https://stackoverflow.com/questions/14394366/find-distance-between-two-points-on-map-using-google-map-api-v2
    public double distance(LatLng start, LatLng end) {
        Double lat1=start.latitude;
        Double lon1=start.longitude;
        Double lat2=end.latitude;
        Double lon2=end.longitude;
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1))
                * Math.sin(deg2rad(lat2))
                + Math.cos(deg2rad(lat1))
                * Math.cos(deg2rad(lat2))
                * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        // dist= dist / 0.62137;// this line will convert Km to miles
        return (dist);
    }
    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }
    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }
}
