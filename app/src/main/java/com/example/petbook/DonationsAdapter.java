package com.example.petbook;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class DonationsAdapter extends RecyclerView.Adapter<DonationsAdapter.MyViewHolder> {

    private ArrayList<DonationsDataClass> dataList;
    private Context context;
    private OnButtonClickListener mListener; // Step 1: Define an interface

    // Step 2: Modify the constructor
    public DonationsAdapter(Context context, ArrayList<DonationsDataClass> dataList, OnButtonClickListener listener) {
        this.context = context;
        this.dataList = dataList;
        this.mListener = listener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.donation_items, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Glide.with(context).load(dataList.get(position).getPet_image()).into(holder.recyclerImage);
        holder.recyclerCaption.setText(dataList.get(position).getPet_name());
        holder.recyclercontact.setText(dataList.get(position).getContact());
        holder.recyclerdesc.setText(dataList.get(position).getDescriptiom());

        // Step 5: Set click listener for the button
        holder.yourButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onButtonClick(position); // Invoke interface method
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    // Step 1: Define an interface
    public interface OnButtonClickListener {
        void onButtonClick(int position);
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView recyclerImage;
        TextView recyclerCaption;
        TextView recyclercontact;
        TextView recyclerdesc;
        Button yourButton; // Add your button here

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            recyclerImage = itemView.findViewById(R.id.pet_prof);
            recyclerCaption = itemView.findViewById(R.id.titleTextView);
            recyclercontact = itemView.findViewById(R.id.contact);
            recyclerdesc = itemView.findViewById(R.id.donationdesc);
            yourButton = itemView.findViewById(R.id.donate); // Initialize your button
        }
    }
}
