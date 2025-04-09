package com.example.mybalaton;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AttractionAdapter extends RecyclerView.Adapter<AttractionAdapter.AttractionViewHolder> {
    
    private List<AttractionModel> attractionList;
    private Context context;
    
    public AttractionAdapter(Context context, List<AttractionModel> attractionList) {
        this.context = context;
        this.attractionList = attractionList;
    }
    
    @NonNull
    @Override
    public AttractionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.attraction_item, parent, false);
        return new AttractionViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull AttractionViewHolder holder, int position) {
        AttractionModel attraction = attractionList.get(position);
        
        holder.attractionName.setText(attraction.getName());
        holder.attractionDescription.setText(attraction.getDescription());
        holder.attractionImage.setImageResource(attraction.getImageResource());
        
        holder.cardView.setOnClickListener(v -> {
            android.widget.Toast.makeText(context, 
                "Kiválasztva: " + attraction.getName(), 
                android.widget.Toast.LENGTH_SHORT).show();
        });
    }
    
    @Override
    public int getItemCount() {
        return attractionList.size();
    }
    
    public static class AttractionViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        ImageView attractionImage;
        TextView attractionName;
        TextView attractionDescription;
        
        public AttractionViewHolder(@NonNull View itemView) {
            super(itemView);
            
            cardView = (CardView) itemView;
            attractionImage = itemView.findViewById(R.id.attractionImage);
            attractionName = itemView.findViewById(R.id.attractionName);
            attractionDescription = itemView.findViewById(R.id.attractionDescription);
        }
    }
} 