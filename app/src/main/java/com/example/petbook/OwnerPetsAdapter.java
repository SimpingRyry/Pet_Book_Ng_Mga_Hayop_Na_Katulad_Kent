package com.example.petbook;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class OwnerPetsAdapter extends RecyclerView.Adapter<OwnerPetsAdapter.MyViewHolder>{

    ArrayList<OwnerPetsDataClass> dataList;
    Context context;
    public OwnerPetsAdapter(ArrayList<OwnerPetsDataClass> dataList, Context context) {
        this.dataList = dataList;
        this.context = context;
    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.owner_pets, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OwnerPetsAdapter.MyViewHolder holder, int position) {
        Glide.with(context).load(dataList.get(position).getImage_url()).into(holder.petImg);
        holder.ownerPetname.setText(dataList.get(position).getPet_name());
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }
    public class MyViewHolder extends RecyclerView.ViewHolder{
        ImageView petImg;
        TextView ownerPetname;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            petImg = itemView.findViewById(R.id.pet_image);
            ownerPetname = itemView.findViewById(R.id.owner_pet_name);
        }
    }
}
