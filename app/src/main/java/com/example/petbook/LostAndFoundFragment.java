package com.example.petbook;

import android.annotation.SuppressLint;
import android.content.Context;
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

import android.preference.PreferenceManager;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.makeramen.roundedimageview.RoundedImageView;
import com.stripe.android.PaymentConfiguration;
import com.stripe.android.paymentsheet.PaymentSheet;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class LostAndFoundFragment extends Fragment {

    public interface OnButton1ClickListener {
        void onButton1Click();
    }

    private OnButton1ClickListener mListener;
    FloatingActionButton fab;
    private RecyclerView recyclerView;
    private ArrayList<DataClass> dataList;
    private LostAndFoundAdapter adapter;
    Button lostbtn;

    private DatabaseReference databaseReference;
    PaymentSheet paymentsheet;
    String customerID;
    String EphericalKey;
    String ClientSecret;
    RoundedImageView roundedImageView;
    private SharedPreferences preferences;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_lost_and_found, container, false);



        return view;
    }

//    @Override
//    public void onAttach(@NonNull Context context) {
//        super.onAttach(context);
//
//    }

//@Override
//public void onCreate(@Nullable Bundle savedInstanceState) {
//    super.onCreate(savedInstanceState);
//    // Access PaymentSheet from MainActivity
//    if (getActivity() != null && getActivity() instanceof MainActivity) {
//        paymentsheet = ((MainActivity) getActivity()).paymentsheet;
//    }
//}


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        preferences = PreferenceManager.getDefaultSharedPreferences(requireContext());
        roundedImageView = view.findViewById(R.id.top_bar_image);
        String userProf = preferences.getString("userprof", "");


        byte[] bytes = Base64.decode(userProf,Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes,0, bytes.length);
        roundedImageView.setImageBitmap(bitmap);
        if (NetworkUtils.isInternetConnected(getContext())) {

            // Device is connected to the internet
        } else {
            Toast.makeText(getContext(),"Please ensure network connectivity",Toast.LENGTH_SHORT).show();
            // Device is not connected to the internet
        }



        fab = view.findViewById(R.id.fab);
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
                                // Create DataClass object for each image
                                DataClass dataClass = new DataClass(imageUrl, caption, contact,status);
                                dataClass.setProfimage(image);
                                dataList.add(dataClass);
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

        roundedImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getParentFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.mainlayout, new ProfileFragment());
                transaction.commit();
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), UploadPetImage.class);
                startActivity(intent);
                getActivity().finish();
            }
        });
    }





}
