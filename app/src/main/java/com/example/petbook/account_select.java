package com.example.petbook;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.ImageButton;

public class account_select extends AppCompatActivity {
    ImageButton shelteracc,useracc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_select);

        shelteracc = findViewById(R.id.shelteracc);
        useracc = findViewById(R.id.useracc);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        // Retrieve the logged-in user from SharedPreferences
        String loggedInUser = preferences.getString("loggedInUser", null);

        // Check if loggedInUser is not null
        if (loggedInUser != null) {
            // Redirect to MainActivity
            Intent intent = new Intent(account_select.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }

        shelteracc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(account_select.this, shelter_login.class);
                intent.putExtra("name", "Shelter");
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

            }
        });
        useracc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(account_select.this, login_acc.class);
                intent.putExtra("name", "Personal");
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

            }
        });

    }
}