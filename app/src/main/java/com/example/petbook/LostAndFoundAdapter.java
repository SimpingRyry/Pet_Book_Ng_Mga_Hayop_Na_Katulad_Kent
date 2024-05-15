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
import com.bumptech.glide.Glide;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
public class LostAndFoundAdapter extends RecyclerView.Adapter<LostAndFoundAdapter.MyViewHolder> {
    private ArrayList<DataClass> dataList;
    private Context context;
    public LostAndFoundAdapter(Context context, ArrayList<DataClass> dataList) {
        this.context = context;
        this.dataList = dataList;
    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycler_items, parent, false);
        return new MyViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Glide.with(context).load(dataList.get(position).getImageURL()).into(holder.recyclerImage);
        holder.recyclerCaption.setText(dataList.get(position).getCaption());
        holder.recyclercontact.setText(dataList.get(position).getContact());
        holder.recyclerstatus.setText(dataList.get(position).getStatus());
        holder.userprof.setImageBitmap(getUserImage(dataList.get(position).getProfimage()));
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView recyclerImage;
        TextView recyclerCaption;
        TextView recyclercontact;
        TextView recyclerstatus;
        RoundedImageView userprof;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            recyclerImage = itemView.findViewById(R.id.recyclerImage);
            recyclerCaption = itemView.findViewById(R.id.recyclerCaption);
            recyclercontact = itemView.findViewById(R.id.recyclercontact);
            recyclerstatus = itemView.findViewById(R.id.recyclerstatus);
            userprof = itemView.findViewById(R.id.userprof);
        }
    }
    private Bitmap getUserImage(String encodedImage) {
        byte[] bytes = Base64.decode(encodedImage, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }
}
