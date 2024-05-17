package com.example.petbook;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.petbook.listeners.UserListerner;
import com.example.petbook.models.User;
import com.makeramen.roundedimageview.RoundedImageView;

public class RecyclerView_MainLayout extends AppCompatActivity implements UserListerner {
Button inquire;
RoundedImageView petimage1;
TextView petname,owner,age,contact;
ImageView back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycler_view_main_layout);
        inquire = findViewById(R.id.inquireButton);
        petimage1 = findViewById(R.id.top_bar_juris);
        petname = findViewById(R.id.petname);
        owner = findViewById(R.id.owner);
        age = findViewById(R.id.age);
        contact = findViewById(R.id.contact);
        back = findViewById(R.id.imageback);
        Intent intent = getIntent();
        String owner1 = intent.getStringExtra("owner");
        String petname1 = intent.getStringExtra("petname");
        String petage1 = intent.getStringExtra("petage");
        String petimage = intent.getStringExtra("petimage");
        String contact1 = intent.getStringExtra("contact");
        String ownerid = intent.getStringExtra("ownerid");
        String ownerprof = intent.getStringExtra("ownerprof");
        Glide.with(getApplicationContext()).load(petimage).into(petimage1);

        petname.setText(petname1);
        age.setText(petage1);
        owner.setText(owner1);
        contact.setText(contact1);

        inquire.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                User user = new User();
                user.name = owner1;
                user.id = ownerid;
                user.image = ownerprof;
                OnUserClicked(user);
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
              // Replace "key" with your desired key and "value" with the actual value you want to pass
                startActivity(intent);
            }
        });
    }   @Override
    public void OnUserClicked(User user) {
        Intent intent = new Intent(getApplicationContext(), chat2.class);
        intent.putExtra("user", user); // Replace "key" with your desired key and "value" with the actual value you want to pass
        startActivity(intent);
    }

    }



