package com.example.petbook;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.FrameLayout;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FrameLayout framelayout = findViewById(R.id.mainlayout);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                int itemID = menuItem.getItemId();

                if (itemID == R.id.button_home) {
                    loadFragment(new HomeFragment(), false);
                } else if (itemID == R.id.button_donation) {
                    loadFragment(new DonationFragment(), false);
                } else if (itemID == R.id.button_lnf) {
                    loadFragment(new LostAndFoundFragment(), false);
                } else {
                    loadFragment(new MessagesFragment(), false);
                }

                return true;
            }
        });
        loadFragment(new HomeFragment(), true);
    }

    private void loadFragment(Fragment fragment, boolean isAppInitialized){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        if (isAppInitialized) {
            fragmentTransaction.add(R.id.mainlayout, fragment);
        } else {
            fragmentTransaction.replace(R.id.mainlayout, fragment);
        }
        fragmentTransaction.commit();
    }
}