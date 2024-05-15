package com.example.petbook;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.preference.PreferenceManager;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;


public class ProfileFragment extends Fragment {
    private RecyclerView recyclerView;
    private ArrayList<OwnerPetsDataClass> dataList;
    private OwnerPetsAdapter adapter;
    RoundedImageView roundedImageView;
    ImageView lalabasanNa;
    private SharedPreferences preferences;
    TextView name1,address1,email1;
    DatabaseReference databaseReference;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        return view;
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        preferences = PreferenceManager.getDefaultSharedPreferences(requireContext());
        roundedImageView = view.findViewById(R.id.top_bar_juris);
        name1= view.findViewById(R.id.name);
        address1 = view.findViewById(R.id.address);
        email1 = view.findViewById(R.id.email);
        String userProf = preferences.getString("userprof", "");
        String name = preferences.getString("name", "");
        String address = preferences.getString("address", "");
        String email = preferences.getString("email", "");
        String accountType = preferences.getString("account_type", "");
        String loggedInUser = preferences.getString("loggedInUser", "");
        name1.setText(name);
        address1.setText(address);
        email1.setText(email);
        byte[] bytes = Base64.decode(userProf,Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes,0, bytes.length);
        roundedImageView.setImageBitmap(bitmap);

        recyclerView = view.findViewById(R.id.adoptionRecycler);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        dataList = new ArrayList<>();
        adapter = new OwnerPetsAdapter(dataList, getContext());
        recyclerView.setAdapter(adapter);
        databaseReference = FirebaseDatabase.getInstance().getReference("users").child(preferences.getString("account_type","")).child(preferences.getString("loggedInUser", "")).child("adoptions");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Check if the "adoptions" node exists
                if (snapshot.exists()) {
                    // Iterate through each child node under "adoptions"
                    for (DataSnapshot adoptionSnapshot : snapshot.getChildren()) {
                        // Retrieve the pet image URL and pet name
                        String petImageUrl = adoptionSnapshot.child("pet_image").getValue(String.class);
                        String petName = adoptionSnapshot.child("petname").getValue(String.class);
                        OwnerPetsDataClass dataClass = new OwnerPetsDataClass(petImageUrl, petName);
                        dataList.add(dataClass);
                        // Do something with the retrieved data (e.g., display in UI, store in a list)
                    }
                } else {
                    // "adoptions" node does not exist or is empty
                    // Handle the case accordingly
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle any errors that occur during the data retrieval process
            }
        });

        lalabasanNa = view.findViewById(R.id.lalabasan);
        lalabasanNa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(requireContext())
                        .setTitle("Logout")
                        .setMessage("Are you sure you want to logout?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // User clicked "Yes" button, proceed with logout
                                preferences = PreferenceManager.getDefaultSharedPreferences(requireContext());
                                SharedPreferences.Editor editor = preferences.edit();
                                editor.remove("loggedInUser");
                                editor.remove("userprof");
                                editor.remove("name");
                                editor.remove("account_type");
                                editor.remove("address");
                                editor.remove("email");
                                editor.apply();
                                Intent intent = new Intent(getActivity(), account_select.class);
                                startActivity(intent);
                            }
                        })
                        .setNegativeButton(android.R.string.no, null) // User clicked "No" button, do nothing
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
        });

    }
}