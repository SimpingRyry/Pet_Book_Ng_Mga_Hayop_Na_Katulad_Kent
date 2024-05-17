package com.example.petbook;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
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
import java.util.Objects;


public class load_user extends Fragment implements UserListerner {

    // TODO: Rename parameter arguments, choose names that match
    SharedPreferences preferences;
    EditText search;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        TextView errormsg;
        ProgressBar pbar;
        RecyclerView userrecycler;
        ImageView back;


        // Inflate the layout for this fragment
        View rootview = inflater.inflate(R.layout.fragment_load_user, container, false);
        errormsg = rootview.findViewById(R.id.texterror);
        pbar = rootview.findViewById(R.id.pbar);
        search = rootview.findViewById(R.id.user_input);
        back = rootview.findViewById(R.id.back);
        userrecycler = rootview.findViewById(R.id.rv1);
        GetUsers("", pbar, userrecycler);
        preferences = PreferenceManager.getDefaultSharedPreferences(requireContext());
        GetUsers2(pbar,userrecycler);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = new MessagesFragment();
                FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.mainlayout, fragment);
                transaction.addToBackStack(null); // Optional: Adds the transaction to the back stack
                transaction.commit();
            }
        });
        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Do nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Call GetUsers with the search query
                userrecycler.setVisibility(View.INVISIBLE);
                GetUsers(s.toString(), pbar, userrecycler);
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Do nothing
            }
        });
        if (NetworkUtils.isInternetConnected(getContext())) {

            // Device is connected to the internet
        } else {
            Toast.makeText(getContext(),"Please ensure network connectivity",Toast.LENGTH_SHORT).show();
            // Device is not connected to the internet
        }


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

    private void GetUsers(String query, ProgressBar pbar, RecyclerView userrecycler) {
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
                    if (!Objects.equals(snapshot.getKey(), preferences.getString("loggedInUser", ""))){
                        User user = new User();
                        user.name = snapshot.child("name").getValue(String.class);
                        user.email = snapshot.child("email").getValue(String.class);
                        user.image = snapshot.child("image").getValue(String.class);
                        user.id = snapshot.getKey();

                        if (user != null && user.name.toLowerCase().contains(query.toLowerCase())) {
                            users.add(user);
                        }
                    }

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
                    if (!Objects.equals(snapshot.getKey(), preferences.getString("loggedInUser", ""))){
                        User user = new User();
                        user.name = snapshot.child("name").getValue(String.class);
                        user.email = snapshot.child("email").getValue(String.class);
                        user.image = snapshot.child("image").getValue(String.class);
                        user.id = snapshot.getKey();

                        if (user != null && user.name.toLowerCase().contains(query.toLowerCase())) {
                            users.add(user);
                        }
                    }

                }
                // Ch
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


