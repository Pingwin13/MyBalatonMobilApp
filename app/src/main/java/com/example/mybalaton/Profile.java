package com.example.mybalaton;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class Profile extends AppCompatActivity {
    private TextView emailTextView, passwordTextView;
    private Button btnBackToMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        emailTextView = findViewById(R.id.emailTextView);
        passwordTextView = findViewById(R.id.passwordTextView);

        String email = getIntent().getStringExtra("email");
        String password = getIntent().getStringExtra("password");

        if (email == null || password == null) {
            SharedPreferences sharedPreferences = getSharedPreferences("UserDetails", MODE_PRIVATE);
            email = sharedPreferences.getString("email", "Nincs bejelentkezve");
            password = sharedPreferences.getString("password", "Nincs bejelentkezve");
        }

        emailTextView.setText("Email: " + email);
        passwordTextView.setText("Jelszó: " + password);

        btnBackToMain = findViewById(R.id.btnBackToMain);
        btnBackToMain.setOnClickListener(view -> {
            Intent intent = new Intent(Profile.this, MainActivity.class);
            startActivity(intent);
            finish();
        });
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
