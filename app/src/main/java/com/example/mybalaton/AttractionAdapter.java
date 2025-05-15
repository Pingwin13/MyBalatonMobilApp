package com.example.mybalaton;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class AttractionAdapter extends RecyclerView.Adapter<AttractionAdapter.AttractionViewHolder> {
    
    private List<AttractionModel> attractionList;
    private Context context;
    private OnItemClickListener listener;
    private SimpleDateFormat dateFormat;
    private String currentUserEmail;
    
    public interface OnItemClickListener {
        void onItemClick(AttractionModel attraction, int position);
        void onEditClick(AttractionModel attraction, int position);
        void onDeleteClick(AttractionModel attraction, int position);
    }
    
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
    
    public AttractionAdapter(Context context, List<AttractionModel> attractionList, String currentUserEmail) {
        this.context = context;
        this.attractionList = attractionList;
        this.currentUserEmail = currentUserEmail;
        this.dateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm", new Locale("hu"));
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
        Glide.with(holder.itemView.getContext())
             .load(attraction.getImageUrl())
             .placeholder(R.drawable.balatonbackg)
             .error(R.drawable.balatonbackg)
             .into(holder.attractionImage);

        if (attraction.getCreatedAt() != null) {
            holder.attractionDate.setText(dateFormat.format(attraction.getCreatedAt()));
        } else {
            holder.attractionDate.setText("Ismeretlen dátum");
        }
        
        if (attraction.getUserEmail() != null && attraction.getUserEmail().equals(currentUserEmail)) {
            holder.btnEdit.setVisibility(View.VISIBLE);
            holder.btnDelete.setVisibility(View.VISIBLE);
        } else {
            holder.btnEdit.setVisibility(View.GONE);
            holder.btnDelete.setVisibility(View.GONE);
        }
        
        holder.btnEdit.setOnClickListener(v -> {
            if (listener != null) listener.onEditClick(attraction, position);
        });
        holder.btnDelete.setOnClickListener(v -> {
            if (listener != null) listener.onDeleteClick(attraction, position);
        });
        
        holder.cardView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(attraction, position);
            }
        });
    }
    
    @Override
    public int getItemCount() {
        return attractionList.size();
    }
    
    public void sortByName() {
        Collections.sort(attractionList, (a1, a2) -> a1.getName().compareToIgnoreCase(a2.getName()));
        notifyDataSetChanged();
    }

    public void sortByDate(boolean ascending) {
        Collections.sort(attractionList, (a1, a2) -> {
            if (ascending) {
                return a1.getCreatedAt().compareTo(a2.getCreatedAt());
            } else {
                return a2.getCreatedAt().compareTo(a1.getCreatedAt());
            }
        });
        notifyDataSetChanged();
    }
    
    public static class AttractionViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        ImageView attractionImage;
        TextView attractionName;
        TextView attractionDescription;
        TextView attractionDate;
        Button btnEdit;
        Button btnDelete;
        
        public AttractionViewHolder(@NonNull View itemView) {
            super(itemView);
            
            cardView = (CardView) itemView;
            attractionImage = itemView.findViewById(R.id.attractionImage);
            attractionName = itemView.findViewById(R.id.attractionName);
            attractionDescription = itemView.findViewById(R.id.attractionDescription);
            attractionDate = itemView.findViewById(R.id.attractionDate);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
} 