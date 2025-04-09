package com.example.mybalaton;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;

public class AddAttractionActivity extends AppCompatActivity {

    private static final String TAG = "AddAttractionActivity";
    private static final int PICK_IMAGE_REQUEST = 1;
    
    private ImageView attractionImagePreview;
    private Button selectImageButton;
    private TextInputEditText nameEditText, descriptionEditText;
    private Button saveButton;
    
    private Uri imageUri;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_attraction);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        try {
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            databaseReference = database.getReference("attractions");
            Log.d(TAG, "Firebase database reference initialized successfully");
        } catch (Exception e) {
            Log.e(TAG, "Error initializing Firebase: " + e.getMessage());
            Toast.makeText(this, "Hiba az adatbázishoz való csatlakozás során: " + e.getMessage(), Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        attractionImagePreview = findViewById(R.id.attractionImagePreview);
        selectImageButton = findViewById(R.id.selectImageButton);
        nameEditText = findViewById(R.id.nameEditText);
        descriptionEditText = findViewById(R.id.descriptionEditText);
        saveButton = findViewById(R.id.saveButton);

        selectImageButton.setOnClickListener(view -> openFileChooser());

        saveButton.setOnClickListener(view -> saveAttraction());
    }
    
    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Válassz képet"), PICK_IMAGE_REQUEST);
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                attractionImagePreview.setImageBitmap(bitmap);
                attractionImagePreview.setVisibility(View.VISIBLE);
                selectImageButton.setText("Kép módosítása");
            } catch (IOException e) {
                Log.e(TAG, "Error loading image: " + e.getMessage());
                Toast.makeText(this, "Hiba a kép betöltése során", Toast.LENGTH_SHORT).show();
            }
        }
    }
    
    private void saveAttraction() {
        if (databaseReference == null) {
            Toast.makeText(this, "Adatbázis hiba: nem sikerült csatlakozni a Firebase-hez", Toast.LENGTH_LONG).show();
            return;
        }
        
        saveButton.setEnabled(false); // Prevent multiple clicks
        
        String name = nameEditText.getText().toString().trim();
        String description = descriptionEditText.getText().toString().trim();
        
        // Validate inputs
        if (name.isEmpty()) {
            nameEditText.setError("Kérlek add meg a látnivaló nevét!");
            nameEditText.requestFocus();
            saveButton.setEnabled(true);
            return;
        }
        
        if (description.isEmpty()) {
            descriptionEditText.setError("Kérlek adj meg leírást!");
            descriptionEditText.requestFocus();
            saveButton.setEnabled(true);
            return;
        }
        
        // Create attraction with default image (for now)
        AttractionModel attraction = new AttractionModel(name, description, R.drawable.balatonbackg);
        
        // Generate unique key for the attraction
        String attractionId = databaseReference.push().getKey();
        
        if (attractionId == null) {
            Toast.makeText(this, "Hiba a látnivaló azonosító generálása során", Toast.LENGTH_SHORT).show();
            saveButton.setEnabled(true);
            return;
        }
        
        // Save attraction to Firebase
        databaseReference.child(attractionId).setValue(attraction)
            .addOnSuccessListener(aVoid -> {
                Log.d(TAG, "Attraction saved successfully with ID: " + attractionId);
                Toast.makeText(AddAttractionActivity.this, "Látnivaló sikeresen hozzáadva!", Toast.LENGTH_SHORT).show();
                
                // Go back to attractions list
                Intent intent = new Intent(AddAttractionActivity.this, AttractionsActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            })
            .addOnFailureListener(e -> {
                Log.e(TAG, "Error saving attraction: " + e.getMessage());
                Toast.makeText(AddAttractionActivity.this, "Hiba a mentés során: " + e.getMessage(), Toast.LENGTH_LONG).show();
                saveButton.setEnabled(true);
            });
    }
    
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // Handle back button click
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
} 