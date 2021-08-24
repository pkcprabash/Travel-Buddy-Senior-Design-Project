package com.example.travelbuddy.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;


import com.example.travelbuddy.Models.Place;
import com.example.travelbuddy.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class PlacesAdapter extends RecyclerView.Adapter<PlacesAdapter.CustomViewHolder> {

    List<Place> places;
    Context context;
    private  onItemClickListener mListener;
      public  interface onItemClickListener{
          void  loadURL(int position);
        }

  public  void setOnItemClickListener(onItemClickListener listener){
          mListener = listener;
     }
   public static class  CustomViewHolder extends RecyclerView.ViewHolder{
        TextView placeName,kmAway,ratting,totalRatting,textViewOpenClose,textViewAddress;
        ImageView placeImage;
        ImageView[] star = new ImageView[5];


        public CustomViewHolder(View itemView, final onItemClickListener listener) {
            super(itemView);
            placeName = itemView.findViewById(R.id.textViewName);
            kmAway = itemView.findViewById(R.id.textViewDistance);
            ratting = itemView.findViewById(R.id.textViewRatting);
            textViewOpenClose = itemView.findViewById(R.id.textViewOpenClose);
            totalRatting = itemView.findViewById(R.id.textViewTotal);
            textViewAddress = itemView.findViewById(R.id.textViewAddress);
            placeImage = itemView.findViewById(R.id.imageViewDealerImage);
            star[0] = itemView.findViewById(R.id.imageViewStar1);
            star[1] = itemView.findViewById(R.id.imageViewStar2);
            star[2] = itemView.findViewById(R.id.imageViewStar3);
            star[3] = itemView.findViewById(R.id.imageViewStar4);
            star[4] = itemView.findViewById(R.id.imageViewStar5);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener!=null){
                        int position=getAdapterPosition();
                        if(position!= RecyclerView.NO_POSITION){
                            listener.loadURL(position);
                        }
                    }
                }
            });

        }
    }

    public PlacesAdapter(List<Place> places, Context context) {
        this.places = places;
        this.context = context;
    }

    @Override
    public int getItemViewType(int position) {

            return R.layout.recycler_view_item_layout;
    }

    @Override
    public int getItemCount() {
        return  places.size();
    }
    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new CustomViewHolder(LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false), mListener);
    }

    @Override
    public void onBindViewHolder(CustomViewHolder holder, int position) {
      String url=  context.getString(R.string.photo_url)
                +"maxwidth=400"+"&photoreference="+places.get(position).getImageID()+
                "&key="+context.getString(R.string.google_map_api_key);
        holder.placeImage.setImageDrawable(context.getDrawable(R.drawable.simple_placeholder));
        holder.placeName.setText(places.get(position).getPlaceName());
        holder.textViewAddress.setText(places.get(position).getAddress());

         if(places.get(position).isOpen()){
             holder.textViewOpenClose.setText("Open");
             holder.textViewOpenClose.setTextColor(holder.kmAway.getContext().getColor(R.color.green));
         }else {
             holder.textViewOpenClose.setText("Close");
             holder.textViewOpenClose.setTextColor(holder.kmAway.getContext().getColor(R.color.red));
         }

         if(places.get(position).getImageID().equals("none")){
             holder.placeImage.setImageDrawable(context.getDrawable(R.drawable.ic_default_place));
         }else{
             Picasso.get().load(url).into(holder.placeImage);
         }

        String dis = String.format("%.2f", places.get(position).getDistance());
        holder.kmAway.setText(dis+" miles");
        holder.ratting.setText(String.valueOf(places.get(position).getRating()));

        if (places.get(position).getTotalRating()==0) {
            holder.ratting.setText("Ratting");
            holder.totalRatting.setText("NA");
        }else {
            holder.ratting.setText(String.valueOf(places.get(position).getRating()));
            holder.totalRatting.setText("(" + places.get(position).getTotalRating() + ")");
            Double dRate = Double.parseDouble(String.valueOf(places.get(position).getRating()));
            int Irate = dRate.intValue();
            double point = dRate - Irate;

            int totalStar = 5;
            int i = 0;
            for (; i < Irate; i++) {
                holder.star[i].setImageResource(R.drawable.ic_full_star);
                totalStar--;
            }

            if (totalStar != 0) {
                if (point >= 0.3) {
                    holder.star[i].setImageResource(R.drawable.ic_half_star);
                    i++;
                    totalStar--;
                }
            }
            for (; i < 5; i++) {
                holder.star[i].setImageResource(R.drawable.ic_empty_star);
            }
        }
      }
}
