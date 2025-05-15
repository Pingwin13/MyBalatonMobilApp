package com.example.mybalaton;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;

public class Profile extends AppCompatActivity {
    private TextView emailTextView, passwordTextView;
    private Button btnBackToMain;
    private RecyclerView userAttractionsRecyclerView;
    private AttractionAdapter adapter;
    private List<AttractionModel> userAttractions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        emailTextView = findViewById(R.id.emailTextView);
        passwordTextView = findViewById(R.id.passwordTextView);
        userAttractionsRecyclerView = findViewById(R.id.userAttractionsRecyclerView);

        String email = getIntent().getStringExtra("email");
        String password = getIntent().getStringExtra("password");

        if (email == null || password == null) {
            SharedPreferences sharedPreferences = getSharedPreferences("UserDetails", MODE_PRIVATE);
            email = sharedPreferences.getString("email", "Nincs bejelentkezve");
            password = sharedPreferences.getString("password", "Nincs bejelentkezve");
        }

        emailTextView.setText("Email: " + email);
        passwordTextView.setText("Jelszó: " + password);

        userAttractions = new ArrayList<>();
        SharedPreferences prefs = getSharedPreferences("UserDetails", MODE_PRIVATE);
        String userEmail = prefs.getString("email", "");
        adapter = new AttractionAdapter(this, userAttractions, userEmail);
        adapter.setOnItemClickListener(new AttractionAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(AttractionModel attraction, int position) {
            }
            @Override
            public void onEditClick(AttractionModel attraction, int position) {
                editAttraction(attraction);
            }
            @Override
            public void onDeleteClick(AttractionModel attraction, int position) {
                deleteAttraction(position);
            }
        });
        
        userAttractionsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        userAttractionsRecyclerView.setAdapter(adapter);

        loadUserAttractions();

        btnBackToMain = findViewById(R.id.btnBackToMain);
        btnBackToMain.setOnClickListener(view -> {
            Intent intent = new Intent(Profile.this, MainActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void loadUserAttractions() {
        SharedPreferences prefs = getSharedPreferences("UserDetails", MODE_PRIVATE);
        String userEmail = prefs.getString("email", "");
        userAttractions.clear();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("attractions")
          .whereEqualTo("userEmail", userEmail)
          .get()
          .addOnSuccessListener(queryDocumentSnapshots -> {
              userAttractions.clear();
              for (DocumentSnapshot doc : queryDocumentSnapshots) {
                  AttractionModel attraction = doc.toObject(AttractionModel.class);
                  if (attraction != null) {
                      attraction.setId(doc.getId());
                      userAttractions.add(attraction);
                  }
              }
              adapter.notifyDataSetChanged();
          })
          .addOnFailureListener(e -> {
              Toast.makeText(Profile.this, "Hiba: " + e.getMessage(), Toast.LENGTH_SHORT).show();
          });
    }

    private void showAttractionOptionsDialog(AttractionModel attraction, int position) {
        String[] options = {"Szerkesztés", "Törlés"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Látnivaló kezelése");
        builder.setItems(options, (dialog, which) -> {
            if (which == 0) {
                editAttraction(attraction);
            } else {
                deleteAttraction(position);
            }
        });
        builder.show();
    }

    private void editAttraction(AttractionModel attraction) {
        Intent intent = new Intent(this, AddAttractionActivity.class);
        intent.putExtra("attraction_name", attraction.getName());
        intent.putExtra("attraction_description", attraction.getDescription());
        intent.putExtra("attraction_image_url", attraction.getImageUrl());
        intent.putExtra("attraction_id", attraction.getId());
        intent.putExtra("is_edit", true);
        startActivity(intent);
    }

    private void deleteAttraction(int position) {
        AttractionModel attraction = userAttractions.get(position);
        if (attraction.getId() != null) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("attractions").document(attraction.getId())
                .delete()
                .addOnSuccessListener(aVoid -> {
                    userAttractions.remove(position);
                    adapter.notifyItemRemoved(position);
                    Toast.makeText(Profile.this, "Látnivaló törölve", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(Profile.this, "Hiba: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        
        if (id == R.id.main) {
            Intent intent = new Intent(Profile.this, MainActivity.class);
            startActivity(intent);
            return true;
        }

        if (id == R.id.attractions) {
            Intent intent = new Intent(Profile.this, AttractionsActivity.class);
            startActivity(intent);
            return true;
        }
        
        if (id == R.id.profile) {
            Toast.makeText(this, "Már a profil oldalon vagy", Toast.LENGTH_SHORT).show();
            return true;
        } 
        
        if (id == R.id.search) {
            Toast.makeText(this, "Keresés", Toast.LENGTH_SHORT).show();
            return true;
        } 
        
        if (id == R.id.add) {
            Intent intent = new Intent(Profile.this, AddAttractionActivity.class);
            startActivity(intent);
            return true;
        } 
        
        if (id == R.id.exit) {
            SharedPreferences sharedPreferences = getSharedPreferences("UserDetails", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear();
            editor.apply();

            Toast.makeText(this, "Kilépés", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
