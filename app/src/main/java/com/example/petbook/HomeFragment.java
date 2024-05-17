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
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.preference.PreferenceManager;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;

public class HomeFragment extends Fragment implements RecyclerViewInterface, OnItemListener {
    private RecyclerView recyclerView, recyclerView1, shelterpets;
    private ArrayList<shelterDataClass> dataList;
    private ArrayList<DataClass> dataList1;
    private ArrayList<AdoptionDataClass> dataList2;
    private ArrayList<AdoptionDataClass> dataList3;
    private ShelterAdapter adapter;
    private AdoptionAdapter adapter1;
    private AdoptionAdapter2 adapter2;
    FloatingActionButton fab;

    private DatabaseReference databaseReference, databaseReference1;
    private SharedPreferences preferences;
    RoundedImageView roundedImageView;

    ImageView profile;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        return view;
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fab = view.findViewById(R.id.fab);
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

        shelterpets = view.findViewById(R.id.shelterpets);
        shelterpets.setHasFixedSize(true);
        shelterpets.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        dataList3 = new ArrayList<>();
        adapter2 = new AdoptionAdapter2(dataList3,getContext(),this::onItemClick1);
        shelterpets.setAdapter(adapter2);
        databaseReference = FirebaseDatabase.getInstance().getReference("users");
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), UploadAdoption.class);
                startActivity(intent);
                getActivity().finish();
            }
        });
        databaseReference1 = FirebaseDatabase.getInstance().getReference("users").child("shelter");
        databaseReference1.addValueEventListener(new ValueEventListener() {

            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                dataList2.clear(); // Clear existing data
                // Iterate through each child of "users"
                    // Check if the child is "shelter" or "user_account"
                        // Iterate through each account under "shelter" or "user_account"
                for (DataSnapshot userChild : snapshot.getChildren()) {
                    // Check if the child is "shelter" or "user_account"

                        // Iterate through each account under "shelter" or "user_account"

                            // Get the "images" node under each account
                            DataSnapshot imagesNode = userChild.child("adoptions");
                            // Iterate through each image under the "images" node
                            for (DataSnapshot imageSnapshot : imagesNode.getChildren()) {
                                String imageUrl = imageSnapshot.child("pet_image").getValue(String.class);
                                String caption = imageSnapshot.child("petname").getValue(String.class);
                                String petage = imageSnapshot.child("petage").getValue(String.class);
                                String contact = imageSnapshot.child("contact").getValue(String.class);
                                String owner = imageSnapshot.child("owner").getValue(String.class);
                                // Create DataClass object for each image
                                AdoptionDataClass dataClass = new AdoptionDataClass(imageUrl, caption, petage,contact,owner);

                                dataClass.setOwnerid(userChild.getKey());
                                dataClass.setOwnerprof(userChild.child("image").getValue(String.class));
                                dataList3.add(dataClass);
                            }


                }


                adapter2.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle possible errors
            }
        });




        recyclerView1 = view.findViewById(R.id.adoptionRecycler);
        recyclerView1.setHasFixedSize(true);
        recyclerView1.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        dataList2 = new ArrayList<>();
        adapter1 = new AdoptionAdapter(dataList2, getContext(),this::onItemClick);
        recyclerView1.setAdapter(adapter1);

        databaseReference = FirebaseDatabase.getInstance().getReference("users");
        databaseReference.addValueEventListener(new ValueEventListener() {

            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                dataList2.clear(); // Clear existing data
                // Iterate through each child of "users"
                for (DataSnapshot userChild : snapshot.getChildren()) {
                    // Check if the child is "shelter" or "user_account"
                    if (userChild.getKey().equals("shelter") || userChild.getKey().equals("user_account")) {
                        // Iterate through each account under "shelter" or "user_account"
                        for (DataSnapshot accountSnapshot : userChild.getChildren()) {
                            // Get the "images" node under each account
                            DataSnapshot imagesNode = accountSnapshot.child("adoptions");
                            // Iterate through each image under the "images" node
                            for (DataSnapshot imageSnapshot : imagesNode.getChildren()) {
                                String imageUrl = imageSnapshot.child("pet_image").getValue(String.class);
                                String caption = imageSnapshot.child("petname").getValue(String.class);
                                String petage = imageSnapshot.child("petage").getValue(String.class);
                                String contact = imageSnapshot.child("contact").getValue(String.class);
                                String owner = imageSnapshot.child("owner").getValue(String.class);
                                // Create DataClass object for each image
                                AdoptionDataClass dataClass = new AdoptionDataClass(imageUrl, caption, petage,contact,owner);
                                dataClass.setOwnerid(userChild.getKey());
                                dataClass.setOwnerprof(userChild.child("image").getValue(String.class));
                                dataList2.add(dataClass);
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


        roundedImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getParentFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.mainlayout, new ProfileFragment());
                transaction.commit();
            }
        });
    }

    @Override
    public void onItemClick(int position) {
        // Example: start a new activity or display a detail fragment
     Intent intent = new Intent(getContext(), RecyclerView_MainLayout.class);
        intent.putExtra("petimage",dataList2.get(position).getPet_image());
     intent.putExtra("owner",dataList2.get(position).getOwner());
        intent.putExtra("petname",dataList2.get(position).getPetname());
        intent.putExtra("petage",dataList2.get(position).getPetage());
        intent.putExtra("contact",dataList2.get(position).getContact());
        intent.putExtra("ownerid",dataList2.get(position).getOwnerid());
        intent.putExtra("ownerprof",dataList2.get(position).getOwnerprof());

        startActivity(intent);
    }

    @Override
    public void onItemClick1(int position) {
        Intent intent = new Intent(getContext(), RecyclerView_MainLayout.class);
        intent.putExtra("petimage",dataList3.get(position).getPet_image());
        intent.putExtra("owner",dataList3.get(position).getOwner());
        intent.putExtra("petname",dataList3.get(position).getPetname());
        intent.putExtra("petage",dataList3.get(position).getPetage());
        intent.putExtra("contact",dataList3.get(position).getContact());
        intent.putExtra("ownerid",dataList3.get(position).getOwnerid());
        intent.putExtra("ownerprof",dataList3.get(position).getOwnerprof());

        startActivity(intent);
    }
}
