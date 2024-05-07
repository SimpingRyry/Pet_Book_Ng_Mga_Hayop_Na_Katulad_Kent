package com.example.petbook;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.petbook.databinding.ActivityChat2Binding;
import com.example.petbook.models.User;

public class chat2 extends AppCompatActivity {
private User receiveruser;
private ActivityChat2Binding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityChat2Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setListeners();
        loadreceiverdetails();


    }

    private void loadreceiverdetails(){
        receiveruser = (User) getIntent().getSerializableExtra("user");
        binding.textname2.setText(receiveruser.name);


    }

    private void setListeners(){
    binding.imageback.setOnClickListener(view -> onBackPressed());}
}