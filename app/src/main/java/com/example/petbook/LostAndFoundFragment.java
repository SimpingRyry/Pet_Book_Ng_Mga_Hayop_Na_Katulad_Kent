package com.example.petbook;

import android.annotation.SuppressLint;
import android.content.Context;
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
    private MyAdapter adapter;
    Button lostbtn;

    private DatabaseReference databaseReference;
    PaymentSheet paymentsheet;
    String customerID;
    String EphericalKey;
    String ClientSecret;

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
@Override
public void onAttach(Context context) {
    super.onAttach(context);
    try {
        mListener = (OnButton1ClickListener) context;
    } catch (ClassCastException e) {
        throw new ClassCastException(context.toString() + " must implement OnButton1ClickListener");
    }
}
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


        fab = view.findViewById(R.id.fab);
        recyclerView = view.findViewById(R.id.recyclerView);
        lostbtn = view.findViewById(R.id.btnlost);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        dataList = new ArrayList<>();
        adapter = new MyAdapter(getContext(), dataList);
        recyclerView.setAdapter(adapter);



        lostbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    mListener.onButton1Click();
            }
        });








        // Initialize database reference
        databaseReference = FirebaseDatabase.getInstance().getReference("users");

        databaseReference.addValueEventListener(new ValueEventListener() {

            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                dataList.clear(); // Clear existing data
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    // For each user
                    for (DataSnapshot imageSnapshot : dataSnapshot.child("images").getChildren()) {
                        String imageUrl = imageSnapshot.child("imageURL").getValue(String.class);
                        String caption = imageSnapshot.child("caption").getValue(String.class);
                        // Create DataClass object for each image
                        DataClass dataClass = new DataClass(imageUrl, caption);
                        dataList.add(dataClass);
                    }
                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle possible errors
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
