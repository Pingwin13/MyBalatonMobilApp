package com.example.mybalaton;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
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
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import android.content.SharedPreferences;
import java.util.UUID;

public class AddAttractionActivity extends AppCompatActivity {

    private static final String TAG = "AddAttractionActivity";
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int CAMERA_REQUEST = 2;
    private static final int PERMISSION_REQUEST_CODE = 3;
    
    private ImageView attractionImagePreview;
    private Button selectImageButton;
    private TextInputEditText nameEditText, descriptionEditText;
    private Button saveButton;
    
    private Uri imageUri;
    private Bitmap cameraBitmap;
    private String editAttractionId = null;
    private String existingImageUrl = null;
    private boolean isEdit = false;

    private StorageReference storageReference;


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

        storageReference = FirebaseStorage.getInstance().getReference("attraction_images");

        selectImageButton.setOnClickListener(view -> showImageSourceDialog());
        saveButton.setOnClickListener(view -> saveAttraction());

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            isEdit = extras.getBoolean("is_edit", false);
            if (isEdit) {
                editAttractionId = extras.getString("attraction_id", null);
                nameEditText.setText(extras.getString("attraction_name", ""));
                descriptionEditText.setText(extras.getString("attraction_description", ""));
                existingImageUrl = extras.getString("attraction_image_url", null);

                if (!TextUtils.isEmpty(existingImageUrl)) {
                    Glide.with(this)
                            .load(existingImageUrl)
                            .placeholder(R.drawable.balatonbackg)
                            .error(R.drawable.balatonbackg)
                            .into(attractionImagePreview);
                    attractionImagePreview.setVisibility(View.VISIBLE);
                    selectImageButton.setText("Kép módosítása");
                } else {
                    int imageRes = extras.getInt("attraction_image", 0);
                     if (imageRes != 0) {
                        attractionImagePreview.setImageResource(imageRes);
                        attractionImagePreview.setVisibility(View.VISIBLE);
                        selectImageButton.setText("Kép módosítása");
                    }
                }
                setTitle("Látnivaló szerkesztése");
            } else {
                 setTitle("Új látnivaló hozzáadása");
            }
        } else {
            setTitle("Új látnivaló hozzáadása");
        }
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        MenuItem mainItem = menu.findItem(R.id.main);
        menu.clear();
        if (mainItem != null) {
             menu.add(Menu.NONE, R.id.main, Menu.NONE, "Főoldal").setIcon(R.drawable.home_24).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        } else {
        }
        return true;
    }
    
    private void showImageSourceDialog() {
        String[] options = {"Kamera", "Galéria"};
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setTitle("Kép választása");
        builder.setItems(options, (dialog, which) -> {
            if (which == 0) {
                if (checkCameraPermission()) {
                    openCamera();
                } else {
                    requestCameraPermission();
                }
            } else {
                if (checkStoragePermission()) {
                    openGallery();
                } else {
                    requestStoragePermission();
                }
            }
        });
        builder.show();
    }

    private boolean checkCameraPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }

    private boolean checkStoragePermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, PERMISSION_REQUEST_CODE);
    }

    private void requestStoragePermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
    }

    private void openCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(cameraIntent, CAMERA_REQUEST);
        } else {
            Toast.makeText(this, "Nincs kamera alkalmazás telepítve.", Toast.LENGTH_SHORT).show();
        }
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (permissions[0].equals(Manifest.permission.CAMERA)) {
                    openCamera();
                } else if (permissions[0].equals(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    openGallery();
                }
            } else {
                Toast.makeText(this, "Engedély megtagadva", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        cameraBitmap = null;
        imageUri = null;

        if (resultCode == RESULT_OK) {
            if (requestCode == PICK_IMAGE_REQUEST && data != null && data.getData() != null) {
                imageUri = data.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                    attractionImagePreview.setImageBitmap(bitmap);
                    attractionImagePreview.setVisibility(View.VISIBLE);
                    selectImageButton.setText("Kép módosítása");
                } catch (IOException e) {
                    Log.e(TAG, "Error loading image from gallery: " + e.getMessage());
                    Toast.makeText(this, "Hiba a kép betöltése során (Galéria)", Toast.LENGTH_SHORT).show();
                }
            } else if (requestCode == CAMERA_REQUEST && data != null && data.getExtras() != null) {
                cameraBitmap = (Bitmap) data.getExtras().get("data");
                if (cameraBitmap != null) {
                    attractionImagePreview.setImageBitmap(cameraBitmap);
                    attractionImagePreview.setVisibility(View.VISIBLE);
                    selectImageButton.setText("Kép módosítása");
                } else {
                     Log.e(TAG, "Camera data is null");
                     Toast.makeText(this, "Hiba a kép rögzítése során (Kamera)", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
    
    private void saveAttraction() {
        String name = nameEditText.getText().toString().trim();
        String description = descriptionEditText.getText().toString().trim();

        if (TextUtils.isEmpty(name)) {
            nameEditText.setError("Kérlek add meg a látnivaló nevét!");
            nameEditText.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(description)) {
            descriptionEditText.setError("Kérlek adj meg leírást!");
            descriptionEditText.requestFocus();
            return;
        }

        saveButton.setEnabled(false);
        Toast.makeText(this, "Mentés folyamatban...", Toast.LENGTH_SHORT).show();

        if (imageUri != null) {
            uploadImageToStorage(imageUri, name, description);
        } else if (cameraBitmap != null) {
            uploadBitmapToStorage(cameraBitmap, name, description);
        } else if (isEdit && existingImageUrl != null) {
            saveAttractionDataToDatabase(name, description, existingImageUrl);
        } else {
            String defaultImageUrl = "";
            saveAttractionDataToDatabase(name, description, defaultImageUrl);
        }
    }

    private void uploadBitmapToStorage(Bitmap bitmap, final String name, final String description) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        final String imageFileName = UUID.randomUUID().toString() + ".jpg";
        final StorageReference fileReference = storageReference.child(imageFileName);

        Log.d(TAG, "Attempting to upload camera bitmap to path: " + fileReference.getPath());

        UploadTask uploadTask = fileReference.putBytes(data);
        uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    Log.e(TAG, "UploadTask (putBytes) for camera image was not successful for " + fileReference.getPath(), task.getException());
                    throw task.getException();
                }
                Log.d(TAG, "UploadTask (putBytes) for camera image successful for " + fileReference.getPath() + ". Bytes transferred: " + task.getResult().getBytesTransferred());
                return fileReference.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                    Log.d(TAG, "Camera image GetDownloadUrl successful. URL: " + downloadUri.toString());
                    saveAttractionDataToDatabase(name, description, downloadUri.toString());
                } else {
                    Log.e(TAG, "GetDownloadUrl failed after camera image upload attempt for path: " + fileReference.getPath(), task.getException());
                    Toast.makeText(AddAttractionActivity.this, "Kép URL lekérdezés sikertelen (Kamera): " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    saveButton.setEnabled(true);
                }
            }
        });
    }


    private void uploadImageToStorage(Uri imageFileUri, final String name, final String description) {
        if (imageFileUri == null) {
            saveButton.setEnabled(true);
            Toast.makeText(this, "Nincs kép kiválasztva a feltöltéshez.", Toast.LENGTH_SHORT).show();
            return;
        }
        final String imageFileName = UUID.randomUUID().toString() + "." + getFileExtension(imageFileUri);
        final StorageReference fileReference = storageReference.child(imageFileName);
        Log.d(TAG, "Attempting to upload gallery image from URI: " + imageFileUri.toString() + " to path: " + fileReference.getPath());

        UploadTask uploadTask = fileReference.putFile(imageFileUri);

        uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    Log.e(TAG, "UploadTask (putFile) for gallery image was not successful for " + fileReference.getPath(), task.getException());
                    throw task.getException();
                }
                Log.d(TAG, "UploadTask (putFile) for gallery image successful for " + fileReference.getPath() + ". Bytes transferred: " + task.getResult().getBytesTransferred());
                return fileReference.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                    String imageUrl = downloadUri.toString();
                    Log.d(TAG, "Gallery image GetDownloadUrl successful. URL: " + imageUrl);
                    saveAttractionDataToDatabase(name, description, imageUrl);
                } else {
                    Log.e(TAG, "GetDownloadUrl failed after gallery image upload attempt for path: " + fileReference.getPath(), task.getException());
                    Toast.makeText(AddAttractionActivity.this, "Kép URL lekérdezés sikertelen (Galéria): " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    saveButton.setEnabled(true);
                }
            }
        });
    }

    private String getFileExtension(Uri uri) {
        try {
            String path = uri.getPath();
            return path.substring(path.lastIndexOf(".") + 1);
        } catch (Exception e) {
            return "jpg";
        }
    }

    private void saveAttractionDataToDatabase(String name, String description, String imageUrl) {
        Log.d(TAG, "Mentés indult: name=" + name + ", description=" + description + ", imageUrl=" + imageUrl);
        Toast.makeText(this, "Mentés indult: " + name, Toast.LENGTH_SHORT).show();
        SharedPreferences prefs = getSharedPreferences("UserDetails", MODE_PRIVATE);
        String userEmail = prefs.getString("email", "UnknownUser");

        AttractionModel attraction = new AttractionModel(name, description, imageUrl);
        attraction.setUserEmail(userEmail);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        if (isEdit && editAttractionId != null) {
            attraction.setId(editAttractionId);
            db.collection("attractions").document(editAttractionId)
                .set(attraction, SetOptions.merge())
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Sikeres frissítés: " + editAttractionId);
                    Toast.makeText(AddAttractionActivity.this, "Látnivaló frissítve!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(AddAttractionActivity.this, AttractionsActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to update attraction: ", e);
                    Toast.makeText(AddAttractionActivity.this, "Hiba a látnivaló frissítésekor: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    saveButton.setEnabled(true);
                });
        } else {
            db.collection("attractions")
                .add(attraction)
                .addOnSuccessListener(documentReference -> {
                    Log.d(TAG, "Sikeres mentés: " + documentReference.getId());
                    Toast.makeText(AddAttractionActivity.this, "Látnivaló mentve!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(AddAttractionActivity.this, AttractionsActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to save attraction: ", e);
                    Toast.makeText(AddAttractionActivity.this, "Hiba a látnivaló mentésekor: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    saveButton.setEnabled(true);
                });
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        } else if (item.getItemId() == R.id.main) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
} 