package com.example.petbook;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.example.petbook.adapters.UserAdapters;
import com.example.petbook.listeners.UserListerner;
import com.example.petbook.models.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class load_user extends Fragment implements UserListerner {

    // TODO: Rename parameter arguments, choose names that match


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        TextView errormsg;
        ProgressBar pbar;
        RecyclerView userrecycler;

        // Inflate the layout for this fragment
        View rootview = inflater.inflate(R.layout.fragment_load_user, container, false);
        errormsg = rootview.findViewById(R.id.texterror);
        pbar = rootview.findViewById(R.id.pbar);
        userrecycler = rootview.findViewById(R.id.rv1);

        GetUsers(pbar,userrecycler);
        GetUsers2(pbar,userrecycler);

        return rootview;

    }

    private void GetUsers2(ProgressBar pbar, RecyclerView userrecycler) {

    }

    private void loading(ProgressBar pbar, Boolean Isloading) {
        if (Isloading) {
            pbar.setVisibility(View.VISIBLE);
        } else {
            pbar.setVisibility(View.INVISIBLE);
        }

    }

    private void GetUsers(ProgressBar pbar, RecyclerView userrecycler) {
        loading(pbar, true);
        final boolean[] userReferenceDataLoaded = {false}; // Using an array to hold boolean value
        final boolean[] shelterDataLoaded = {false};
        DatabaseReference userreference = FirebaseDatabase.getInstance().getReference("users").child("user_account");
        DatabaseReference shelterreference = FirebaseDatabase.getInstance().getReference("users").child("shelter");

        // Combining data from both queries
        List<User> users = new ArrayList<>();

        userreference.orderByChild("username").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userReferenceDataLoaded[0] = false; // Marking user account data as loaded

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    User user = new User();
                    user.name = snapshot.child("name").getValue(String.class);
                    user.email = snapshot.child("email").getValue(String.class);
                    user.image = snapshot.child("image").getValue(String.class);
                    user.id = snapshot.getKey();
                    users.add(user);
                }
                // Check if both queries are completed
                if (users.size() != 0 && !shelterDataLoaded[0]) {
                    loading(pbar, false);
                    setUserAdapter(users, userrecycler);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle onCancelled event
            }
        });

        shelterreference.orderByChild("username").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                shelterDataLoaded[0] = false; // Flag to indicate shelter data loaded
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    User user = new User();
                    user.name = snapshot.child("name").getValue(String.class);
                    user.email = snapshot.child("email").getValue(String.class);
                    user.image = snapshot.child("image").getValue(String.class);
                    user.id = snapshot.getKey();
                    users.add(user);
                }
                // Check if both queries are completed
                if (users.size() != 0 && !userReferenceDataLoaded[0]) {
                    loading(pbar, false);
                    setUserAdapter(users, userrecycler);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle onCancelled event
            }
        });
    }



    @Override
    public void OnUserClicked(User user) {
        Intent intent = new Intent(getActivity(), chat2.class);
        intent.putExtra("user", user); // Replace "key" with your desired key and "value" with the actual value you want to pass
        startActivity(intent);

    }

    private void setUserAdapter(List<User> users, RecyclerView userrecycler) {
        UserAdapters userAdapters = new UserAdapters(users, load_user.this::OnUserClicked);
        userrecycler.setAdapter(userAdapters);
        userrecycler.setVisibility(View.VISIBLE);
    }



}


