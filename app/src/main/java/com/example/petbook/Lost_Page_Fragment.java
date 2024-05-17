package com.example.petbook;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

public class Lost_Page_Fragment extends Fragment {
    private RecyclerView recyclerView;
    private ArrayList<DataClass> dataList;
    private LostAndFoundAdapter adapter;
    private DatabaseReference databaseReference;
    AppCompatButton appCompatButton1, appCompatButton2;
    RoundedImageView roundedImageView;
    private SharedPreferences preferences;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_lost_page_, container, false);

        appCompatButton1 = view.findViewById(R.id.btnAll);
        appCompatButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = new LostAndFoundFragment();
                FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.mainlayout, fragment);
                transaction.addToBackStack(null); // Optional: Adds the transaction to the back stack
                transaction.commit();
            }
        });

        appCompatButton2 = view.findViewById(R.id.btnFound);
        appCompatButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = new Found_Page_Fragment();
                FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.mainlayout, fragment);
                transaction.addToBackStack(null); // Optional: Adds the transaction to the back stack
                transaction.commit();
            }
        });
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
        roundedImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getParentFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.mainlayout, new ProfileFragment());
                transaction.commit();
            }
        });

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        dataList = new ArrayList<>();
        adapter = new LostAndFoundAdapter(getContext(), dataList);
        recyclerView.setAdapter(adapter);
        databaseReference = FirebaseDatabase.getInstance().getReference("users");
        databaseReference.addValueEventListener(new ValueEventListener() {

            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                dataList.clear(); // Clear existing data

                // Iterate through each child of "users"
                for (DataSnapshot userChild : snapshot.getChildren()) {
                    // Check if the child is "shelter" or "user_account"
                    if (userChild.getKey().equals("shelter") || userChild.getKey().equals("user_account")) {
                        // Iterate through each account under "shelter" or "user_account"
                        for (DataSnapshot accountSnapshot : userChild.getChildren()) {
                            // Get the "images" node under each account
                            DataSnapshot imagesNode = accountSnapshot.child("images");
                            String image = accountSnapshot.child("image").getValue(String.class);
                            // Iterate through each image under the "images" node
                            for (DataSnapshot imageSnapshot : imagesNode.getChildren()) {
                                String imageUrl = imageSnapshot.child("imageURL").getValue(String.class);
                                String caption = imageSnapshot.child("caption").getValue(String.class);
                                String contact = imageSnapshot.child("contact").getValue(String.class);
                                String status = imageSnapshot.child("status").getValue(String.class);

                                if (status.equals("Lost")) {
                                    // Create DataClass object for each image
                                    DataClass dataClass = new DataClass(imageUrl, caption, contact,status);
                                    dataClass.setProfimage(image);
                                    dataList.add(dataClass);
                                }

                            }
                        }
                    }
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