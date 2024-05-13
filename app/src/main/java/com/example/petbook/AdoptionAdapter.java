package com.example.petbook;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;

public class AdoptionAdapter extends RecyclerView.Adapter<AdoptionAdapter.MyViewHolder> {
    ArrayList<DataClass> dataList;
    Context context;
    public AdoptionAdapter(ArrayList<DataClass> dataList, Context context) {
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
        Glide.with(context).load(dataList.get(position).getImageURL()).into(holder.staggeredImages);
        holder.staggeredCaption.setText(dataList.get(position).getCaption());
        holder.staggeredContact.setText(dataList.get(position).getContact());
        holder.staggeredStatus.setText(dataList.get(position).getStatus());

    }
    @Override
    public int getItemCount() {
        return dataList.size();
    }
    public class MyViewHolder extends RecyclerView.ViewHolder{
        RoundedImageView staggeredImages;
        TextView staggeredCaption, staggeredContact, staggeredStatus;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            staggeredImages = itemView.findViewById(R.id.staggeredImages);
            staggeredCaption = itemView.findViewById(R.id.staggeredCaption);
            staggeredContact = itemView.findViewById(R.id.staggeredContact);
            staggeredStatus = itemView.findViewById(R.id.staggeredStatus);
        }
    }
}
