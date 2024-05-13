package com.example.petbook;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class HomeFragment extends Fragment {
    private RecyclerView recyclerView;
    private ArrayList<shelterDataClass> dataList;
    private ShelterAdapter adapter;
    private DatabaseReference databaseReference;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        return view;
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);



        recyclerView = view.findViewById(R.id.shelterrecycler);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        dataList = new ArrayList<>();
        adapter = new ShelterAdapter(getContext(), dataList);
        recyclerView.setAdapter(adapter);











        databaseReference = FirebaseDatabase.getInstance().getReference("users").child("shelter");

        databaseReference.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                dataList.clear(); // Clear existing data

                // Iterate through each account under "shelter"
                for (DataSnapshot accountSnapshot : snapshot.getChildren()) {
                    // Get the "images" node under each account
                    String image = accountSnapshot.child("image").getValue(String.class);

                    String name = accountSnapshot.child("name").getValue(String.class);
                    String email = accountSnapshot.child("email").getValue(String.class);
                    String address = accountSnapshot.child("address").getValue(String.class);
                    shelterDataClass dataClass = new shelterDataClass(image, name, email,address);
                    dataList.add(dataClass);
                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle possible errors
            }
        });
}

}