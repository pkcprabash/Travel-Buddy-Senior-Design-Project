package com.example.travelbuddy.Fragments;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.model.LatLng;
import com.example.travelbuddy.Adapters.PlacesAdapter;
import com.example.travelbuddy.Helper.DistanceCalculator;
import com.example.travelbuddy.Models.Place;
import com.example.travelbuddy.R;
import com.example.travelbuddy.WebsiteLoader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class NearbyHotelsFragment extends Fragment {
View rootView;
    LocationManager locationManager;
    LocationListener locationListener;
    LatLng currentLocation;
    String[] locationPermissions = new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
    LinearLayout layoutProgress,layoutGPS;
    RecyclerView recyclerView;

    String placeType = "hotel";
    int radius = 50000;
    PlacesAdapter placesAdapter;
    List<Place> places;
    DistanceCalculator distanceCalculator = new DistanceCalculator();
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_nearby_hotels, container, false);
        distanceCalculator = new DistanceCalculator();
        initView();
        setRecyclerView();
        getUserLocation();

  return rootView;
   }
    private void setRecyclerView() {
        places = new ArrayList<>();
        placesAdapter = new PlacesAdapter(places, getContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(placesAdapter);

        placesAdapter.setOnItemClickListener(new PlacesAdapter.onItemClickListener() {

            @Override
            public void loadURL(int position) {
                Intent intent=new Intent(getActivity(), WebsiteLoader.class);
                intent.putExtra("placeID",places.get(position).getPlaceID());
                intent.putExtra("placeName",places.get(position).getPlaceName());
                startActivity(intent);
            }
        });
    }
    private void initView() {
        layoutProgress = rootView.findViewById(R.id.layoutProgress);
        layoutGPS = rootView.findViewById(R.id.layoutGPS);
        layoutGPS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSettingsAlert();
            }
        });
        recyclerView = rootView.findViewById(R.id.recyclerView);

    }

    private void getUserLocation() {

        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            checkLocationPermissions();
            return;
        }

        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            visibleLoading();
            locationListener=new LocationListener() {
                @Override
                public void onLocationChanged(@NonNull Location location) {

                    currentLocation=new LatLng(location.getLatitude(),location.getLongitude());
                    locationManager.removeUpdates(locationListener);
                    getNearByPlaces();
                }
                @Override
                public void onProviderDisabled(@NonNull String provider) {

                }

                @Override
                public void onProviderEnabled(@NonNull String provider) {

                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {

                }
            };
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0,locationListener);
        } else {
            visibleGPS();
            showSettingsAlert();
        }
    }
    private void getNearByPlaces() {
        visibleLoading();
        String url=getString(R.string.google_places_base_url)
                +"location="+currentLocation.latitude+","+currentLocation.longitude
                +"&radius="+radius+"&type="+placeType
                +"&key="+getString(R.string.google_map_api_key);
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url
                , response -> {
                    visibleRecyclerView();
                    parseJSON(response);

                }, error -> {
                    showToast("Error : " + error.getMessage());
                    visibleRecyclerView();
                });
        stringRequest.setShouldCache(false);
        requestQueue.add(stringRequest);
    }
    private void parseJSON(String response) {
        places.clear();
        try {
            JSONObject jsonObject = new JSONObject(response);
            if (jsonObject.getString("status").equals("OK")) {
                JSONArray placesArray = jsonObject.getJSONArray("results");
                for (int i = 0; i < placesArray.length(); i++) {
                    JSONObject singlePlace = placesArray.getJSONObject(i);

                    Place place = new Place();
                    if(singlePlace.has("name"))
                        place.setPlaceName(singlePlace.getString("name"));
                    else
                        place.setPlaceName("None");
                    if(singlePlace.has("place_id"))
                        place.setPlaceID(singlePlace.getString("place_id"));
                    else
                        place.setPlaceID("");
                    if(singlePlace.has("vicinity"))
                        place.setAddress(singlePlace.getString("vicinity"));
                    else place.setAddress("Address : None");
                    if(singlePlace.has("opening_hours")) {

                        if (singlePlace.getJSONObject("opening_hours").has("open_now"))
                            place.setOpen(singlePlace.getJSONObject("opening_hours").getBoolean("open_now"));
                        else
                            place.setOpen(false);
                    }else{
                        place.setOpen(false);
                    }
                    if(singlePlace.has("rating")){
                        place.setRating(singlePlace.getDouble("rating"));
                    }else {
                        place.setRating(0.0);
                    }
                    if(singlePlace.has("user_ratings_total")){
                        place.setTotalRating(singlePlace.getInt("user_ratings_total"));
                    }else {
                        place.setTotalRating(0);
                    }
                    if(singlePlace.has("photos")){
                        JSONArray photosArray=singlePlace.getJSONArray("photos");
                        if(photosArray.getJSONObject(0).has("photo_reference"))
                            place.setImageID(photosArray.getJSONObject(0).getString("photo_reference"));
                        else
                            place.setImageID("none");
                    }else {
                        place.setImageID("none");
                    }
                    place.setLat(singlePlace.getJSONObject("geometry").getJSONObject("location").getDouble("lat"));
                    place.setLng(singlePlace.getJSONObject("geometry").getJSONObject("location").getDouble("lng"));
                    LatLng locationLatLng = new LatLng(place.getLat(), place.getLng());
                    place.setDistance(distanceCalculator.distance(currentLocation, locationLatLng));
                    places.add(place);
                }
            } else
                showToast("Nothing Found");
            placesAdapter.notifyDataSetChanged();
        } catch (JSONException e) {
            e.printStackTrace();
            showToast("Error : " + e.getMessage());
            Log.e("error", e.getMessage());
        }
    }
    private boolean checkLocationPermissions() {
        int permissionResult;
        List<String> permissionsList = new ArrayList<>();
        for (String p : locationPermissions) {
            permissionResult = ContextCompat.checkSelfPermission(getContext(), p);
            if (permissionResult != PackageManager.PERMISSION_GRANTED) {
                permissionsList.add(p);
            }
        }
        if (!permissionsList.isEmpty()) { //this is okay yes
            ActivityCompat.requestPermissions(getActivity(), permissionsList.toArray(new String[permissionsList.size()]), 202);
            return false;
        }else {
            return true;
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 202) {
            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                showToast("Permission denied");
                getActivity().finish();
            }else {
                showToast("Permission granted successfully");
                getUserLocation();
            }
        }
    }
    public void showSettingsAlert(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());

        alertDialog.setTitle("GPS not enable");

        alertDialog.setMessage("GPS is disabled. Do you want to go to settings?");
        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(intent,2021);
            }
        });
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                visibleGPS();

            }
        });
        alertDialog.show();
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2021) {
            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                showToast("GPS Enable successfully");
                getUserLocation();
            } else {
                showToast("Please Enable Gps");
                visibleGPS();

            }
        }
    }
    private void visibleRecyclerView(){
        layoutGPS.setVisibility(View.GONE);
        layoutProgress.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
    }
    private void visibleLoading(){
        layoutGPS.setVisibility(View.GONE);
        layoutProgress.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
    }
    private void visibleGPS(){
        layoutGPS.setVisibility(View.VISIBLE);
        layoutProgress.setVisibility(View.GONE);
        recyclerView.setVisibility(View.GONE);
    }
    private void showToast(String message) {
        Toast.makeText(
                getContext(),
                message
                , Toast.LENGTH_LONG
        ).show();
    }

}