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
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;

public class AdoptionAdapter extends RecyclerView.Adapter<AdoptionAdapter.MyViewHolder> {
    ArrayList<AdoptionDataClass> dataList;
    Context context;
    public AdoptionAdapter(ArrayList<AdoptionDataClass> dataList, Context context) {
        this.dataList = dataList;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.staggered_item, parent, false);
        return new MyViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Glide.with(context).load(dataList.get(position).getPet_image()).into(holder.staggeredImages);
        holder.petage.setText(dataList.get(position).getPetage());
        holder.staggeredCaption.setText(dataList.get(position).getPetname());
        holder.staggeredContact.setText(dataList.get(position).getContact());
        holder.staggeredStatus.setText(dataList.get(position).getOwner());

    }
    @Override
    public int getItemCount() {
        return dataList.size();
    }
    public class MyViewHolder extends RecyclerView.ViewHolder{
        ImageView staggeredImages;
        TextView staggeredCaption, staggeredContact, staggeredStatus,petage;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            petage = itemView.findViewById(R.id.staggeredage);
            staggeredImages = itemView.findViewById(R.id.staggeredImages);
            staggeredCaption = itemView.findViewById(R.id.staggeredCaption);
            staggeredContact = itemView.findViewById(R.id.staggeredContact);
            staggeredStatus = itemView.findViewById(R.id.staggeredStatus);
    }
    }
}
