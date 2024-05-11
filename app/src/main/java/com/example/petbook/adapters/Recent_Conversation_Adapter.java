package com.example.petbook.adapters;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.petbook.ChatMessage;
import com.example.petbook.databinding.ItemConttainerRecentConversationBinding;
import com.example.petbook.listeners.ConversationListener;
import com.example.petbook.models.User;

import java.util.List;

public class Recent_Conversation_Adapter extends RecyclerView.Adapter<Recent_Conversation_Adapter.ConversationViewHolder>{
    @NonNull
    @Override
    public ConversationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ConversationViewHolder(ItemConttainerRecentConversationBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ConversationViewHolder holder, int position) {
            holder.setData(chatMessages.get(position));
    }

    @Override
    public int getItemCount() {
        return chatMessages.size();
    }

    public Recent_Conversation_Adapter(List<ChatMessage> chatMessages, ConversationListener conversationListener) {

        this.chatMessages = chatMessages;
        this.conversationListener = conversationListener;
    }

    private List<ChatMessage> chatMessages;
    private final ConversationListener conversationListener;

    class ConversationViewHolder extends RecyclerView.ViewHolder{
        ItemConttainerRecentConversationBinding binding;

        ConversationViewHolder(ItemConttainerRecentConversationBinding itemConttainerRecentConversationBinding){
            super(itemConttainerRecentConversationBinding.getRoot());

            binding = itemConttainerRecentConversationBinding;
        }

        void setData(ChatMessage chatMessage){
        binding.imageprog.setImageBitmap(getConvoImage(chatMessage.conversationImage));
        binding.textname.setText(chatMessage.conversationname);
        binding.textrecentmessage.setText(chatMessage.message);
        binding.getRoot().setOnClickListener(view -> {
            User user = new User();
            user.id = chatMessage.conversationId;
            user.name  = chatMessage.conversationname;
            user.image = chatMessage.conversationImage;
            conversationListener.OnConversationClicked(user);
        });
        }
    }

    private Bitmap getConvoImage(String encodedImage) {
        byte[] bytes = Base64.decode(encodedImage, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }
}
