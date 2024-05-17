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

public class AdoptionAdapter2  extends RecyclerView.Adapter<AdoptionAdapter2.MyViewHolder>{
    ArrayList<AdoptionDataClass> dataList;
    Context context;
    private OnItemListener listener;
    public AdoptionAdapter2(ArrayList<AdoptionDataClass> dataList, Context context, OnItemListener listener)   {
        this.dataList = dataList;
        this.context = context;
        this.listener = listener;
    }
    @NonNull
    @Override
    public AdoptionAdapter2.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.adoption_items, parent, false);
        return new AdoptionAdapter2.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdoptionAdapter2.MyViewHolder holder, int position) {
        Glide.with(context).load(dataList.get(position).getPet_image()).into(holder.staggeredImages);
        holder.staggeredCaption.setText(dataList.get(position).getPetname());
        holder.petage.setText(dataList.get(position).getPetage());


    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        ImageView staggeredImages;
        TextView staggeredCaption, staggeredContact, owner,petage;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            staggeredImages = itemView.findViewById(R.id.pet_image);
            staggeredCaption = itemView.findViewById(R.id.staggeredCaption);
            petage = itemView.findViewById(R.id.age);
//            staggeredContact = itemView.findViewById(R.id.staggeredcontact);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemClick1(position);
                        }
                    }
                }
            });
//            owner = itemView.findViewById(R.id.owner);
        }
    }
}
