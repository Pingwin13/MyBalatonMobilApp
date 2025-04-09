package com.example.mybalaton;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;

public class MainActivity extends AppCompatActivity {

    private String email, password;
    private TextView welcomeText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        
        welcomeText = findViewById(R.id.textView);

        SharedPreferences sharedPreferences = getSharedPreferences("UserDetails", MODE_PRIVATE);
        email = sharedPreferences.getString("email", null);
        password = sharedPreferences.getString("password", null);

        if (email == null || password == null) {
            Toast.makeText(this, "Nincs bejelentkezve!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }

        welcomeText.setTranslationY(1000f);
        welcomeText.setAlpha(0f);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Create slide up animation with fade in
                welcomeText.animate()
                    .translationY(0f)
                    .alpha(1f)
                    .setDuration(1000)
                    .start();
            }
        }, 300);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        
        if (searchView != null) {
            searchView.setIconifiedByDefault(false);

            searchView.setSubmitButtonEnabled(true);

            searchView.setQueryHint("Keresés...");

            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    Toast.makeText(MainActivity.this, "Keresés: " + query, Toast.LENGTH_SHORT).show();
                    return true;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    return false;
                }
            });

            searchView.setOnQueryTextFocusChangeListener((v, hasFocus) -> {
                if (hasFocus) {
                    searchView.setIconified(false);
                }
            });
        }
        
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.attractions) {
            Intent intent = new Intent(MainActivity.this, AttractionsActivity.class);
            startActivity(intent);
            return true;
        }

        if (id == R.id.profile) {
            Intent intent = new Intent(MainActivity.this, Profile.class);
            intent.putExtra("email", email);  // Email átadása
            intent.putExtra("password", password);  // Jelszó átadása
            startActivity(intent);
            return true;
        }

        if (id == R.id.search) {
            return false;
        }

        if (id == R.id.add) {
            Intent intent = new Intent(MainActivity.this, AddAttractionActivity.class);
            startActivity(intent);
            return true;
        }

        if (id == R.id.exit) {
            Toast.makeText(this, "Kilépés", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
