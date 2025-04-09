package com.example.mybalaton;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AttractionsActivity extends AppCompatActivity {

    private static final String TAG = "AttractionsActivity";
    private RecyclerView recyclerView;
    private AttractionAdapter adapter;
    private List<AttractionModel> attractionList;
    private List<AttractionModel> filteredList;
    private DatabaseReference databaseReference;
    private ProgressBar progressBar;
    private TextView emptyView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attractions);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        progressBar = findViewById(R.id.progressBar);
        emptyView = findViewById(R.id.emptyView);
        recyclerView = findViewById(R.id.attractionsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        attractionList = new ArrayList<>();
        filteredList = new ArrayList<>();

        adapter = new AttractionAdapter(this, filteredList);
        recyclerView.setAdapter(adapter);

        try {
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            databaseReference = database.getReference("attractions");
            Log.d(TAG, "Firebase database reference initialized successfully");

            if (emptyView != null) {
                emptyView.setVisibility(View.GONE);
            }
            
            loadAttractionsFromFirebase();
        } catch (Exception e) {
            Log.e(TAG, "Error initializing Firebase: " + e.getMessage());
            Toast.makeText(this, "Hiba az adatbázishoz való csatlakozás során: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (databaseReference != null) {
            loadAttractionsFromFirebase();
        }
    }

    private void loadAttractionsFromFirebase() {
        showProgressBar();
        
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                attractionList.clear();
                
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    AttractionModel attraction = snapshot.getValue(AttractionModel.class);
                    if (attraction != null) {
                        attractionList.add(attraction);
                    }
                }
                
                if (attractionList.isEmpty()) {
                    Log.d(TAG, "No attractions found in Firebase");
                    if (emptyView != null) {
                        emptyView.setVisibility(View.VISIBLE);
                        emptyView.setText("Nincsenek látnivalók. Használd a + gombot új hozzáadásához!");
                    }
                } else {
                    Log.d(TAG, "Loaded " + attractionList.size() + " attractions from Firebase");
                    if (emptyView != null) {
                        emptyView.setVisibility(View.GONE);
                    }
                }

                filteredList.clear();
                filteredList.addAll(attractionList);
                adapter.notifyDataSetChanged();
                
                hideProgressBar();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "Error loading attractions: " + databaseError.getMessage());
                Toast.makeText(AttractionsActivity.this, 
                        "Hiba az adatok betöltése során: " + databaseError.getMessage(), 
                        Toast.LENGTH_LONG).show();
                hideProgressBar();
            }
        });
    }
    
    private void showProgressBar() {
        if (progressBar != null) {
            progressBar.setVisibility(View.VISIBLE);
        }
    }
    
    private void hideProgressBar() {
        if (progressBar != null) {
            progressBar.setVisibility(View.GONE);
        }
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
        
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        
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
        
        return super.onOptionsItemSelected(item);
    }
} 