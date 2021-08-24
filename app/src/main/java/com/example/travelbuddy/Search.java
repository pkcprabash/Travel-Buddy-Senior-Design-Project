package com.example.travelbuddy;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.Toast;

public class Search extends AppCompatActivity implements View.OnClickListener {
FrameLayout frameLayoutRestaurants,frameLayoutMoviesTheater,
        FrameLayoutKaraoke,frameLayoutMuseums,
        frameLayoutThemeParks,frameLayoutParks
        ,frameLayoutsGyms;

Spinner spinner;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        initUI();
    }

    private void initUI() {
        frameLayoutRestaurants = findViewById(R.id.frameLayoutRestaurants);
        frameLayoutRestaurants.setOnClickListener(this);
        frameLayoutMoviesTheater = findViewById(R.id.frameLayoutMoviesTheater);
        frameLayoutMoviesTheater.setOnClickListener(this);
        FrameLayoutKaraoke = findViewById(R.id.FrameLayoutKaraoke);
        FrameLayoutKaraoke.setOnClickListener(this);
        frameLayoutMuseums = findViewById(R.id.frameLayoutMuseums);
        frameLayoutMuseums.setOnClickListener(this);
        frameLayoutThemeParks = findViewById(R.id.frameLayoutThemeParks);
        frameLayoutThemeParks.setOnClickListener(this);
        frameLayoutParks = findViewById(R.id.frameLayoutParks);
        frameLayoutParks.setOnClickListener(this);
        frameLayoutsGyms = findViewById(R.id.frameLayoutsGyms);
        frameLayoutsGyms.setOnClickListener(this);


    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.frameLayoutRestaurants:
                LoadPlacesActivity(getString(R.string.restaurants),getString(R.string.restaurants_key));
                break;
            case R.id.frameLayoutMoviesTheater:
                LoadPlacesActivity(getString(R.string.movie_theater),getString(R.string.movie_theater_key));
                break;
            case R.id.FrameLayoutKaraoke:
                LoadPlacesActivity(getString(R.string.karaoke),getString(R.string.karaoke_key1));
                break;
            case R.id.frameLayoutMuseums:
                LoadPlacesActivity(getString(R.string.museum),getString(R.string.museum_key));
                break;
            case R.id.frameLayoutThemeParks:
                LoadPlacesActivity(getString(R.string.theme_parks),getString(R.string.theme_parks_key));
                break;
            case R.id.frameLayoutParks:
                LoadPlacesActivity(getString(R.string.parks),getString(R.string.parks_key));
                break;
            case R.id.frameLayoutsGyms:
                LoadPlacesActivity(getString(R.string.gyms),getString(R.string.gym_key));
                break;

        }

    }


    private void LoadPlacesActivity(String placesType,String placesKey){
      Intent intent = new Intent(Search.this, PlacesActivity.class);
             intent.putExtra("placesType",placesType);
             intent.putExtra("placesKey",placesKey);
             startActivity(intent);
  }
    private void showToast(String message){
        Toast.makeText(getApplicationContext(),
                message,Toast.LENGTH_LONG).show();
    }
}