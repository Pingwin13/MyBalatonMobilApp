package com.example.mybalaton;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class MainActivity extends AppCompatActivity {

    // Felhasználói adatok (email, jelszó), amelyeket a SharedPreferences-ből kell átadni
    private String email, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Az adatokat át kell venni a SharedPreferences-ből
        SharedPreferences sharedPreferences = getSharedPreferences("UserDetails", MODE_PRIVATE);
        email = sharedPreferences.getString("email", null);
        password = sharedPreferences.getString("password", null);

        // Ha nincs email vagy jelszó, akkor logolj ki
        if (email == null || password == null) {
            Toast.makeText(this, "Nincs bejelentkezve!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
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

        if (id == R.id.profile) {
            // Az email és jelszó átadása a Profile activity-be
            Intent intent = new Intent(MainActivity.this, Profile.class);
            intent.putExtra("email", email);  // Email átadása
            intent.putExtra("password", password);  // Jelszó átadása
            startActivity(intent);
        }

        if (id == R.id.search) {
            Toast.makeText(this, "Keresés", Toast.LENGTH_SHORT).show();
        }

        if (id == R.id.add) {
            Toast.makeText(this, "Hozzáadás", Toast.LENGTH_SHORT).show();
        }

        if (id == R.id.exit) {
            Toast.makeText(this, "Kilépés", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }

        return true;
    }
}
