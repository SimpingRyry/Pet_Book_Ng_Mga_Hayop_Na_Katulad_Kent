package com.example.petbook;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class Splash_pet extends AppCompatActivity {

    private Handler handler = new Handler();

    private Runnable runnable;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_pet);

        runnable = new Runnable() {
            @Override
            public void run(){
                Intent intent = new Intent(Splash_pet.this, account_select.class);
                startActivity(intent);
                finish();
            }
        };
        handler.postDelayed(runnable, 4000);
    }
    @Override
    protected void onDestroy(){
        super.onDestroy();
        handler.removeCallbacks(runnable);
    }
}