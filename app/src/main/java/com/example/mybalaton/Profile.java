package com.example.mybalaton;

import android.content.Intent;
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

        // Az email és jelszó átvétele az Intent-ből
        String email = getIntent().getStringExtra("email");
        String password = getIntent().getStringExtra("password");

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

        if (id == R.id.profile) {
            Toast.makeText(this, "Profil", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.search) {
            Toast.makeText(this, "Keresés", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.add) {
            Toast.makeText(this, "Hozzáadás", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.exit) {
            Toast.makeText(this, "Kilépés", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }

        return true;
    }
}
