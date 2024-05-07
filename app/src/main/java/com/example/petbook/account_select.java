package com.example.petbook;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
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

        shelteracc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(account_select.this, login_acc.class);
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