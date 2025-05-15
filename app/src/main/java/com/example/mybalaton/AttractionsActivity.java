package com.example.mybalaton;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;

public class AttractionsActivity extends AppCompatActivity {

    private static final String TAG = "AttractionsActivity";
    private static final String CHANNEL_ID = "attractions_channel";
    private static final int NOTIFICATION_ID = 1;
    
    private RecyclerView recyclerView;
    private AttractionAdapter adapter;
    private List<AttractionModel> attractionList;
    private List<AttractionModel> filteredList;
    private ProgressBar progressBar;
    private TextView emptyView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attractions);

        createNotificationChannel();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        progressBar = findViewById(R.id.progressBar);
        emptyView = findViewById(R.id.emptyView);
        recyclerView = findViewById(R.id.attractionsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        attractionList = new ArrayList<>();
        filteredList = new ArrayList<>();

        loadAttractions();

        if (emptyView != null) {
            emptyView.setVisibility(View.GONE);
        }
        
        if (progressBar != null) {
            progressBar.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadAttractions();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID,
                "Látnivalók értesítések",
                NotificationManager.IMPORTANCE_DEFAULT
            );
            channel.setDescription("Értesítések az új látnivalókról");
            
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void showNotification(String title, String message) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.icon)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }

    private void loadAttractions() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("attractions")
          .get()
          .addOnSuccessListener(queryDocumentSnapshots -> {
              attractionList.clear();
              for (DocumentSnapshot doc : queryDocumentSnapshots) {
                  AttractionModel attraction = doc.toObject(AttractionModel.class);
                  if (attraction != null) {
                      attraction.setId(doc.getId());
                      attractionList.add(attraction);
                  }
              }
              filteredList.clear();
              filteredList.addAll(attractionList);
              if (adapter == null) {
                  SharedPreferences preferences = getSharedPreferences("UserDetails", MODE_PRIVATE);
                  String currentUserEmail = preferences.getString("userEmail", "");
                  adapter = new AttractionAdapter(AttractionsActivity.this, filteredList, currentUserEmail);
                  recyclerView.setAdapter(adapter);
              } else {
                  adapter.notifyDataSetChanged();
              }
          })
          .addOnFailureListener(e -> {
              Toast.makeText(AttractionsActivity.this, "Hiba: " + e.getMessage(), Toast.LENGTH_SHORT).show();
          });
    }

    private void filterAttractions(String query) {
        filteredList.clear();
        
        if (query.isEmpty()) {
            filteredList.addAll(attractionList);
        } else {
            String lowerCaseQuery = query.toLowerCase();
            for (AttractionModel attraction : attractionList) {
                if (attraction.getName().toLowerCase().contains(lowerCaseQuery) || 
                    attraction.getDescription().toLowerCase().contains(lowerCaseQuery)) {
                    filteredList.add(attraction);
                }
            }
        }
        
        adapter.notifyDataSetChanged();
        
        if (filteredList.isEmpty()) {
            showEmptyView("Nincs találat a keresésre: " + query);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
        }
    }

    private void showEmptyView(String message) {
        if (emptyView != null) {
            emptyView.setText(message);
            emptyView.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        
        MenuItem searchItem = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        
        searchView.setIconifiedByDefault(false);
        searchView.setSubmitButtonEnabled(true);
        searchView.setQueryHint("Látnivalók keresése...");
        
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterAttractions(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterAttractions(newText);
                return true;
            }
        });


        menu.add(Menu.NONE, R.id.sort_name_asc, Menu.NONE, "Név szerint (A-Z)")
            .setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
        menu.add(Menu.NONE, R.id.sort_date_desc, Menu.NONE, "Dátum szerint (újabbak előre)")
            .setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
        menu.add(Menu.NONE, R.id.sort_date_asc, Menu.NONE, "Dátum szerint (régebbiek előre)")
            .setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
        
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        
        if (id == R.id.main) {
            startActivity(new Intent(this, MainActivity.class));
            return true;
        }
        
        if (id == R.id.attractions) {
            Toast.makeText(this, "Már a Látnivalók oldalon vagy", Toast.LENGTH_SHORT).show();
            return true;
        }
        
        if (id == R.id.profile) {
            startActivity(new Intent(this, Profile.class));
            return true;
        }
        
        if (id == R.id.search) {
            return true;
        }
        
        if (id == R.id.add) {
            startActivity(new Intent(this, AddAttractionActivity.class));
            return true;
        }
        
        if (id == R.id.exit) {
            SharedPreferences preferences = getSharedPreferences("UserDetails", MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.clear();
            editor.apply();

            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            return true;
        }
        
        if (id == R.id.sort_name_asc) {
            adapter.sortByName();
            return true;
        }
        
        if (id == R.id.sort_date_desc) {
            adapter.sortByDate(false);
            return true;
        }
        
        if (id == R.id.sort_date_asc) {
            adapter.sortByDate(true);
            return true;
        }
        
        return super.onOptionsItemSelected(item);
    }
} 