package com.example.petbook.adapters;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.petbook.ChatMessage;
import com.example.petbook.databinding.ItemContainerReceivedMessageBinding;
import com.example.petbook.databinding.ItemContainerSentMessageBinding;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private final Bitmap receiverProfileImage;
    private final List<ChatMessage> chatMessages;
    private static int View_Type_Sent = 1;
    private static int View_Type_Received = 2;

    public ChatAdapter(Bitmap receiverProfileImage, List<ChatMessage> chatMessages, String senderID) {
        this.receiverProfileImage = receiverProfileImage;
        this.chatMessages = chatMessages;
        this.senderID = senderID;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == View_Type_Sent){
            return new SentMessageViewHolder(ItemContainerSentMessageBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false));
        }
       else {
           return new ReceiveMessageViewHolder(ItemContainerReceivedMessageBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(getItemViewType(position) == View_Type_Sent){
            ((SentMessageViewHolder) holder).setData(chatMessages.get(position));

        }else {
            ((ReceiveMessageViewHolder) holder).setData(chatMessages.get(position),receiverProfileImage);
        }

    }

    @Override
    public int getItemCount() {
        return chatMessages.size();
    }

    @Override
    public int getItemViewType(int position) {
        if(chatMessages.get(position).senderid.equals(senderID)){
            return View_Type_Sent;
        }else{
           return View_Type_Received;
        }
    }

    private final String senderID;
    static class SentMessageViewHolder extends RecyclerView.ViewHolder{
        private final ItemContainerSentMessageBinding binding;

        SentMessageViewHolder(ItemContainerSentMessageBinding itemContainerSentMessageBinding){
            super(itemContainerSentMessageBinding.getRoot());
            binding = itemContainerSentMessageBinding;
        }
        void setData(ChatMessage chatMessage){
            binding.textmessage.setText(chatMessage.message);
            binding.datetime2.setText(chatMessage.dateTime);
        }
    }

    static class ReceiveMessageViewHolder extends RecyclerView.ViewHolder{
        private final ItemContainerReceivedMessageBinding binding;

        ReceiveMessageViewHolder(ItemContainerReceivedMessageBinding itemContainerReceivedMessageBinding){
            super(itemContainerReceivedMessageBinding.getRoot());
            binding = itemContainerReceivedMessageBinding;
        }
        void setData(ChatMessage chatMessage, Bitmap receiverProfileImage){
            binding.textmessage2.setText(chatMessage.message);
            binding.datetime2.setText(chatMessage.dateTime);
            binding.imageprofile.setImageBitmap(receiverProfileImage);
        }
    }


}
