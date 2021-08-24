package com.example.travelbuddy;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Vibrator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.squareup.picasso.Picasso;

public class ResultsAdapter extends RecyclerView.Adapter<ResultsAdapter.MyViewHolder> {
    FirebaseAuth fAuth;
    FirebaseUser user;
    String userId;

    Context context;
    ArrayList<ItineraryResult> ItineraryResults;

    public ResultsAdapter(Context c, ArrayList<ItineraryResult> p){
        context = c;
        ItineraryResults = p;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        fAuth = FirebaseAuth.getInstance();
        user = fAuth.getCurrentUser();
        userId = user.getUid();
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.result_item,parent, false));
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder myViewHolder, int i) {
        myViewHolder.resultsActivity.setText(ItineraryResults.get(i).getResultActivity());
        myViewHolder.resultsPrice.setText("Price: " + ItineraryResults.get(i).getResultPrice());
        myViewHolder.resultsRating.setText("Rating: " + ItineraryResults.get(i).getResultRating());
        myViewHolder.resultsCity.setText(ItineraryResults.get(i).getResultCity());
        try{
            myViewHolder.resultsRatingBar.setRating( Float.parseFloat(ItineraryResults.get(i).getResultRating()));
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        myViewHolder.setIsRecyclable(false);
//        myViewHolder.placeImage.setImageDrawable(context.getDrawable(R.drawable.simple_placeholder));

        if (ItineraryResults.get(i).getResultPhotoRef() == null){
            myViewHolder.placeImage.setImageDrawable(context.getDrawable(R.drawable.ic_default_place));
        }
        else {
            String url=  context.getString(R.string.photo_url)
                    +"maxwidth=400"+"&photoreference="+ ItineraryResults.get(i).getResultPhotoRef() +
                    "&key="+context.getString(R.string.google_map_api_key);
            try {
                Picasso.get().load(url).into(myViewHolder.placeImage);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }



    }

    @Override
    public int getItemCount() {
        return ItineraryResults.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView resultsActivity;
        TextView resultsPrice;
        TextView resultsRating;
        RatingBar resultsRatingBar;
        ImageView placeImage;
        TextView resultsCity;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            resultsActivity = itemView.findViewById(R.id.result_title);
            resultsPrice = itemView.findViewById(R.id.result_price);
            resultsRating = itemView.findViewById(R.id.result_rating);
            resultsRatingBar = itemView.findViewById(R.id.result_ratingbar);
            Button addButton = itemView.findViewById(R.id.add_button);
            resultsCity = itemView.findViewById(R.id.result_city);
            placeImage = itemView.findViewById(R.id.place_image);

            addButton.setOnClickListener(v -> {
                Vibrator vibe = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE) ;
                vibe.vibrate(15);

                int adapterPosition = getAdapterPosition();
                Toast.makeText(context.getApplicationContext(), "Added " + resultsActivity.getText() + " to itinerary.",  Toast.LENGTH_SHORT).show();
                ItineraryResults.get(adapterPosition);


                Log.i("Result Adapter::::", user.getUid());
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child(userId).push();
                Map<String, Object> map = new HashMap<>();
                map.put("itineraryKey", databaseReference.getKey());
                map.put("itineraryActivity", ItineraryResults.get(adapterPosition).getResultActivity());
                map.put("itineraryPrice", ItineraryResults.get(adapterPosition).getResultPrice());
                map.put("itineraryRating", ItineraryResults.get(adapterPosition).getResultRating());
                map.put("itineraryDate", ItineraryResults.get(adapterPosition).getResultDate());
                map.put("itineraryPhotoRef", ItineraryResults.get(adapterPosition).getResultPhotoRef());
                map.put("itineraryCity", ItineraryResults.get(adapterPosition).getResultCity());
                databaseReference.setValue(map);


                addButton.setEnabled(false);

            });
        }

    }

}