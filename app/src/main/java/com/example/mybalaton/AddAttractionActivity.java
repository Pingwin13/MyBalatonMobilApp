package com.example.mybalaton;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.textfield.TextInputEditText;

import java.io.IOException;

public class AddAttractionActivity extends AppCompatActivity {

    private static final String TAG = "AddAttractionActivity";
    private static final int PICK_IMAGE_REQUEST = 1;
    
    private ImageView attractionImagePreview;
    private Button selectImageButton;
    private TextInputEditText nameEditText, descriptionEditText;
    private Button saveButton;
    
    private Uri imageUri;

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

        attractionImagePreview = findViewById(R.id.attractionImagePreview);
        selectImageButton = findViewById(R.id.selectImageButton);
        nameEditText = findViewById(R.id.nameEditText);
        descriptionEditText = findViewById(R.id.descriptionEditText);
        saveButton = findViewById(R.id.saveButton);

        selectImageButton.setOnClickListener(view -> openFileChooser());

        saveButton.setOnClickListener(view -> saveAttraction());
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        
        // Hide all menu items except main/home
        for (int i = 0; i < menu.size(); i++) {
            MenuItem item = menu.getItem(i);
            if (item.getItemId() != R.id.main) {
                item.setVisible(false);
            }
        }
        
        return true;
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
        String name = nameEditText.getText().toString().trim();
        String description = descriptionEditText.getText().toString().trim();

        saveButton.setEnabled(false);
        
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

        Toast.makeText(AddAttractionActivity.this, "Látnivaló létrehozva", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(AddAttractionActivity.this, AttractionsActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.main) {
            Intent intent = new Intent(AddAttractionActivity.this, MainActivity.class);
            startActivity(intent);
            return true;
        }
        
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        
        return super.onOptionsItemSelected(item);
    }
} 