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

        return rootview;

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
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");
        reference.orderByChild("username").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                loading(pbar, false);
                List<User> users = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    // Convert DataSnapshot to User object
                    User user = new User();
                    user.name = snapshot.child("name").getValue(String.class);
                    user.email = snapshot.child("email").getValue(String.class);

                    user.image = snapshot.child("image").getValue(String.class);
                    user.id = snapshot.getKey();
                    users.add(user);
                }

                if (users.size() != 0) {
                    UserAdapters userAdapters = new UserAdapters(users,load_user.this::OnUserClicked);
                    userrecycler.setAdapter(userAdapters);
                    userrecycler.setVisibility(View.VISIBLE);
                } else {
                    // Display a toast message if no users found
                    Toast.makeText(getContext(), "No users found", Toast.LENGTH_SHORT).show();
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
}


