package com.example.petbook;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.preference.PreferenceManager;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;

public class HomeFragment extends Fragment {
    private RecyclerView recyclerView, recyclerView1;
    private ArrayList<shelterDataClass> dataList;
    private ArrayList<DataClass> dataList1;
    private ShelterAdapter adapter;
    private AdoptionAdapter adapter1;
    private DatabaseReference databaseReference, databaseReference1;
    private SharedPreferences preferences;
    RoundedImageView roundedImageView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        return view;
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        preferences = PreferenceManager.getDefaultSharedPreferences(requireContext());
        roundedImageView = view.findViewById(R.id.top_bar_image);
        String userProf = preferences.getString("userprof", "");
        byte[] bytes = Base64.decode(userProf,Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes,0, bytes.length);
        roundedImageView.setImageBitmap(bitmap);

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

        recyclerView1 = view.findViewById(R.id.adoptionRecycler);
        recyclerView1.setHasFixedSize(true);
        recyclerView1.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        dataList1 = new ArrayList<>();
        adapter1 = new AdoptionAdapter(dataList1, getContext());
        recyclerView1.setAdapter(adapter1);

        databaseReference1 = FirebaseDatabase.getInstance().getReference("users");
        databaseReference1.addValueEventListener(new ValueEventListener() {

            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                dataList1.clear(); // Clear existing data
                // Iterate through each child of "users"
                for (DataSnapshot userChild : snapshot.getChildren()) {
                    // Check if the child is "shelter" or "user_account"
                    if (userChild.getKey().equals("shelter") || userChild.getKey().equals("user_account")) {
                        // Iterate through each account under "shelter" or "user_account"
                        for (DataSnapshot accountSnapshot : userChild.getChildren()) {
                            // Get the "images" node under each account
                            DataSnapshot imagesNode = accountSnapshot.child("images");
                            // Iterate through each image under the "images" node
                            for (DataSnapshot imageSnapshot : imagesNode.getChildren()) {
                                String imageUrl = imageSnapshot.child("imageURL").getValue(String.class);
                                String caption = imageSnapshot.child("caption").getValue(String.class);
                                String contact = imageSnapshot.child("contact").getValue(String.class);
                                String status = imageSnapshot.child("status").getValue(String.class);
                                // Create DataClass object for each image
                                DataClass dataClass = new DataClass(imageUrl, caption, contact,status);
                                dataList1.add(dataClass);
                            }
                        }
                    }
                }
                adapter1.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle possible errors
            }
        });

    }
}
