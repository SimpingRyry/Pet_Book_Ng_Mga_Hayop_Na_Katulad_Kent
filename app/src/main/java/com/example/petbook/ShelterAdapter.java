package com.example.petbook;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class ShelterAdapter extends RecyclerView.Adapter<ShelterAdapter.MyViewHolder>{
    private ArrayList<shelterDataClass> dataList;
    private Context context;

    public ShelterAdapter(Context context, ArrayList<shelterDataClass> dataList) {
        this.context = context;
        this.dataList = dataList;
    }
    @NonNull
    @Override
    public ShelterAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.shelter_items, parent, false);
        return new ShelterAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ShelterAdapter.MyViewHolder holder, int position) {
        holder.recyclersheltername.setText(dataList.get(position).getSheltername());

        holder.recylershelteremail.setText(dataList.get(position).getEmail());
        holder.recyclerImage.setImageBitmap(getConvoImage(dataList.get(position).getImageurl()));
        holder.recycleraddress.setText(dataList.get(position).getAddress());
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView recyclerImage;
        TextView recyclersheltername;
        TextView recycleraddress;
        TextView recylershelteremail;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            recyclerImage = itemView.findViewById(R.id.shelterprof);
            recyclersheltername = itemView.findViewById(R.id.titleTextView);
            recylershelteremail = itemView.findViewById(R.id.email);
            recycleraddress = itemView.findViewById(R.id.shelteraddress);

        }
    }
    private Bitmap getConvoImage(String encodedImage) {
        byte[] bytes = Base64.decode(encodedImage, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }
}
