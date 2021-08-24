package com.example.travelbuddy;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Locale;

public class ItinerarySearch extends AppCompatActivity {

    TextView titlepage, titleActivity, titleLocation, titleDate, itineraryDate;
    EditText itineraryActivity;
    Button btnCancel;
    Button btnSearch;
    PlacesClient placesClient;


    final Calendar myCalendar = Calendar.getInstance();

    private void updateLabel() {
        String myFormat = "YYYY/MM/dd";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        itineraryDate.setText(sdf.format(myCalendar.getTime()));
    };

    String placeGet = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_itinerary_search);

        titlepage = findViewById(R.id.titlepage);

        titleActivity = findViewById(R.id.titleActivity);
        titleLocation = findViewById(R.id.titleLocation);
        titleDate = findViewById(R.id.titleDate);

        itineraryActivity = findViewById(R.id.itinerary_activity);
        itineraryDate = findViewById(R.id.itinerary_date);

        btnCancel = findViewById(R.id.btnCancel);
        btnSearch = findViewById(R.id.btnSearch);

        final DatePickerDialog.OnDateSetListener date = (view, year, monthOfYear, dayOfMonth) -> {
            // TODO Auto-generated method stub
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateLabel();
        };

        itineraryDate.setOnClickListener(v -> {
            // TODO Auto-generated method stub

            new DatePickerDialog(ItinerarySearch.this, date, myCalendar
                    .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                    myCalendar.get(Calendar.DAY_OF_MONTH)).show();
        });


        final String apiKey = getString(R.string.google_map_api_key);

        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), apiKey);
        }

        //////////////// AUTOCOMPLETE /////////////////////
        // Create a new Places client instance.
        placesClient = Places.createClient(this);

        // Initialize the AutocompleteSupportFragment.
        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment) getSupportFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
        autocompleteFragment.setTypeFilter(TypeFilter.CITIES);
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.PHOTO_METADATAS, Place.Field.ADDRESS, Place.Field.ADDRESS_COMPONENTS, Place.Field.LAT_LNG, Place.Field.RATING
        ));
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                // TODO: Get info about the selected place.

                String city = place.getAddress();

                Toast.makeText(getApplicationContext(), city, Toast.LENGTH_SHORT).show();
                placeGet = city;
            }

            @Override
            public void onError(@NonNull Status status) {
                // TODO: Handle the error.
                Toast.makeText(getApplicationContext(), status.toString(), Toast.LENGTH_SHORT).show();
            }
        });

        btnSearch.setOnClickListener(v -> {
            Vibrator vibe = (Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE) ;
            vibe.vibrate(15);

            //Required fields
            if( itineraryActivity.getText().toString().length() == 0 )
                itineraryActivity.setError( "Activity is required!" );
            if( itineraryDate.getText().toString().length() == 0 )
                itineraryDate.setError( "Date is required!" );
                //Search activity in ItineraryResults
            else {
                Intent a = new Intent(ItinerarySearch.this, ItineraryResults.class);
                a.putExtra("city", placeGet);
                a.putExtra("activity", itineraryActivity.getText().toString());
                a.putExtra("date", itineraryDate.getText().toString());
                startActivity(a);
            }
        });

        btnCancel.setOnClickListener(v -> {
            Vibrator vibe = (Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE) ;
            vibe.vibrate(15);
            finish();
        });


    }

}