package com.example.travelbuddy.Fragments;

import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
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


public class SearchFragment extends Fragment {
    View rootView;
    LatLng addressLocation;
    LinearLayout layoutProgress;
    RecyclerView recyclerView;
    String placeType = "hotel";
    int radius = 50000;
    PlacesAdapter placesAdapter;
    List<Place> places;
    EditText editTextSearch;
    ImageView imageViewSearch;
    TextView textViewOutPut;
    DistanceCalculator distanceCalculator;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_search, container, false);
  distanceCalculator=new DistanceCalculator();
        initView();
        setRecyclerView();
        return rootView;
    }
    private void setRecyclerView() {
        places=new ArrayList<>();
        placesAdapter= new PlacesAdapter(places, getContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(placesAdapter);
        placesAdapter.setOnItemClickListener(position -> {
            Intent intent=new Intent(getActivity(), WebsiteLoader.class);
            intent.putExtra("placeID",places.get(position).getPlaceID());
            intent.putExtra("placeName",places.get(position).getPlaceName());
            startActivity(intent);
        });
    }
    private void initView() {
        layoutProgress = rootView.findViewById(R.id.layoutProgress);
        recyclerView = rootView.findViewById(R.id.recyclerView);
        editTextSearch = rootView.findViewById(R.id.editTextSearch);
        imageViewSearch = rootView.findViewById(R.id.imageViewSearch);
        textViewOutPut = rootView.findViewById(R.id.textViewResult);
       imageViewSearch.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            performSearch();
        }
    });

        editTextSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    performSearch();
                    return true;
                }
                return false;
            }
        });
    }

    private void performSearch() {
        String inputQuery = editTextSearch.getText().toString().trim();
        if(inputQuery.equals("")){
            showToast("Please enter address");
        }else {
            textViewOutPut.setText("");
            getAddressDetails(inputQuery);
        }
    }

    private void getAddressDetails(String inputQuery) {
        visibleLoading();
       String apiBaseQuery=inputQuery.replace(" ","|");
        String url=getString(R.string.search_api_url)
        +"&input="+apiBaseQuery
        +"&key="+getString(R.string.google_map_api_key);
        Log.d("TAG", url);
        RequestQueue requestQueue2 = Volley.newRequestQueue(getContext());
       StringRequest stringRequest = new StringRequest(Request.Method.GET, url
                , response -> {
           Log.d("Response",response);
            try {
                JSONObject jsonObject = new JSONObject(response);
                if(jsonObject.getString("status").equals("OK")){
                   if(jsonObject.has("candidates")){
                       JSONArray jsonArray=jsonObject.getJSONArray("candidates");
                       JSONObject jsonObjectLocation=jsonArray.getJSONObject(0);
                       textViewOutPut.setText("Location : "+jsonObjectLocation.getString("name"));
                       double lat= jsonObjectLocation.getJSONObject("geometry").getJSONObject("location").getDouble("lat");
                       double lng= jsonObjectLocation.getJSONObject("geometry").getJSONObject("location").getDouble("lng");
                        addressLocation=new LatLng(lat,lng);
                        getNearByPlaces();
                   }else {
                       showToast("Invalid response");
                       visibleRecyclerView();
                   }
                }else {
                    showToast("Invalid Request");
                    visibleRecyclerView();
                }
            } catch (JSONException e) {
                showToast("Google places api parsing error");
                visibleRecyclerView();
                e.printStackTrace();
            }
        }, error -> {
            showToast("Error : " + error.getMessage());
           Log.d("Error ",error.getMessage());
            visibleRecyclerView();
        });
        stringRequest.setShouldCache(false);
        requestQueue2.add(stringRequest);
    }

    private void getNearByPlaces() {
        visibleLoading();
        String url=getString(R.string.google_places_base_url)
                +"location="+addressLocation.latitude+","+addressLocation.longitude
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
                    place.setDistance(distanceCalculator.distance(addressLocation, locationLatLng));
                    places.add(place);
                }
            } else
                showToast("Noting Found");
            if(places.size()>1){
                places.remove(0);

            }
            placesAdapter.notifyDataSetChanged();
        } catch (JSONException e) {
            e.printStackTrace();
            showToast("Error : " + e.getMessage());
            Log.e("error", e.getMessage());
        }
    }

    private void visibleRecyclerView(){
        layoutProgress.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
    }

    private void visibleLoading(){
        layoutProgress.setVisibility(View.VISIBLE);
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