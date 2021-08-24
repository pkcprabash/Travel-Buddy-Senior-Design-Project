package com.example.travelbuddy;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Vibrator;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ItineraryAdapter extends RecyclerView.Adapter<ItineraryAdapter.MyViewHolder> {
    FirebaseAuth fAuth;
    FirebaseUser user;
    String userId;

    Context context;
    ArrayList<ItineraryTask> ItineraryTasks;


    public ItineraryAdapter (Context c, ArrayList<ItineraryTask> p){
        context = c;
        ItineraryTasks = p;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        fAuth = FirebaseAuth.getInstance();
        user = fAuth.getCurrentUser();
        userId = user.getUid();
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.itinerary_item,parent, false));

    }

    @SuppressLint({"SetTextI18n", "UseCompatLoadingForDrawables"})
    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder myViewHolder, int i) {
        myViewHolder.itineraryActivity.setText(ItineraryTasks.get(i).getItineraryActivity());
        myViewHolder.itineraryPrice.setText("Price: " + ItineraryTasks.get(i).getItineraryPrice());
        myViewHolder.itineraryRating.setText("Rating: " + ItineraryTasks.get(i).getItineraryRating());
        myViewHolder.itineraryCity.setText(ItineraryTasks.get(i).getItineraryCity());

        if(ItineraryTasks.get(i).getItineraryRating() != "n/a"){
            try {
                myViewHolder.ratingBar.setRating(Float.parseFloat(ItineraryTasks.get(i).getItineraryRating()));
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        else{
            myViewHolder.ratingBar.setRating(0);
        }

        myViewHolder.itineraryDate.setText(ItineraryTasks.get(i).getItineraryDate());

        myViewHolder.setIsRecyclable(false);

        if (ItineraryTasks.get(i).getItineraryPhotoRef() == null){
            myViewHolder.itineraryImage.setImageDrawable(context.getDrawable(R.drawable.ic_default_place));
        }
        else {
            String url=  context.getString(R.string.photo_url)
                    + "maxwidth=400" + "&photoreference=" + ItineraryTasks.get(i).getItineraryPhotoRef() +
                    "&key=" + context.getString(R.string.google_map_api_key);
            try {
                Picasso.get().load(url).into(myViewHolder.itineraryImage);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        final String getItineraryKey = ItineraryTasks.get(i).getItineraryKey();

        myViewHolder.setIsRecyclable(false);

        myViewHolder.itemView.setOnLongClickListener(v -> {
            Vibrator vibe = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE) ;
            vibe.vibrate(25);


            new AlertDialog.Builder(context)
                    .setTitle("Delete entry")
                    .setMessage("Are you sure you want to delete this entry?")

                    // Specifying a listener allows you to take an action before dismissing the dialog.
                    // The dialog is automatically dismissed when a dialog button is clicked.
                    .setPositiveButton("yes", (dialog, which) -> {
                        // Continue with delete operation
                        ItineraryTasks.remove(i);
                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child(userId);
                        ref.child(getItineraryKey).removeValue();
                        Intent intent = new Intent(context, Itinerary.class);
                        context.startActivity(intent);
                        ((Activity)context).finish();
                        ((Activity)context).overridePendingTransition (0, 0);

                    })

                    // A null listener allows the button to dismiss the dialog and take no further action.
                    .setNegativeButton("no", null)
                    .show();

            return false;
        });

    }


    @Override
    public int getItemCount() {
        return ItineraryTasks.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView itineraryActivity;
        TextView itineraryPrice;
        TextView itineraryRating;
        TextView itineraryDate;
        TextView itineraryCity;
        RatingBar ratingBar;
        ImageView itineraryImage;



        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            itineraryActivity = itemView.findViewById(R.id.itinerary_activity);
            itineraryPrice = itemView.findViewById(R.id.itinerary_price);
            itineraryRating = itemView.findViewById(R.id.itinerary_rating);
            ratingBar = itemView.findViewById(R.id.itinerary_ratingbar);
            itineraryDate = itemView.findViewById(R.id.itinerary_date);
            itineraryImage = itemView.findViewById(R.id.place_image_itinerary);
            itineraryCity = itemView.findViewById(R.id.itinerary_city);

        }
    }
}