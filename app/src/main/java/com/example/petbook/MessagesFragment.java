package com.example.petbook;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.example.petbook.adapters.Recent_Conversation_Adapter;
import com.example.petbook.databinding.ActivityMainBinding;
import com.example.petbook.listeners.ConversationListener;
import com.example.petbook.models.User;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class MessagesFragment extends Fragment implements ConversationListener {
    private List<ChatMessage>  conversations;


    FloatingActionButton floatingActionButton;

    private ActivityMainBinding binding;
    TextView username;
    RoundedImageView roundedImageView;
    private SharedPreferences preferences;


    private Recent_Conversation_Adapter recentConversationAdapter;


    RecyclerView convorecycler;

    DatabaseReference reference ;
    ProgressBar pbar;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        preferences = PreferenceManager.getDefaultSharedPreferences(requireContext());


        View rootview = inflater.inflate(R.layout.fragment_messages, container, false);
        convorecycler = rootview.findViewById(R.id.conversationrecycler);

        init(convorecycler);
        username = rootview.findViewById(R.id.acc_name);
        roundedImageView = rootview.findViewById(R.id.profile);
        pbar = rootview.findViewById(R.id.pbar3);


        String loggedInUser = preferences.getString("loggedInUser", "");
        String userProf = preferences.getString("userprof", "");
        username.setText(loggedInUser);

        byte[] bytes = Base64.decode(userProf,Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes,0, bytes.length);
        roundedImageView.setImageBitmap(bitmap);
        listenConvo(loggedInUser,listenmessages(pbar,convorecycler));

        floatingActionButton = rootview.findViewById(R.id.floatingActionButton);
        if (NetworkUtils.isInternetConnected(getContext())) {

            // Device is connected to the internet
        } else {
            Toast.makeText(getContext(),"Please ensure network connectivity",Toast.LENGTH_SHORT).show();
            // Device is not connected to the internet
        }

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = new load_user();
                FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.mainlayout, fragment);
                transaction.addToBackStack(null); // Optional: Adds the transaction to the back stack
                transaction.commit();
            }
        });
        return rootview;
    }

    private void init(RecyclerView convorecyclerView){
        conversations = new ArrayList<>();
        recentConversationAdapter = new Recent_Conversation_Adapter(conversations,this);
        convorecyclerView.setAdapter(recentConversationAdapter);
        reference = FirebaseDatabase.getInstance().getReference("users");

        ;


    }
    private ValueEventListener listenmessages(ProgressBar progressBar, RecyclerView recyclerView) {
        ValueEventListener chateventlistener = new ValueEventListener() {
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String senderId = snapshot.child("senderId").getValue(String.class);
                    String receiverId = snapshot.child("receiverId").getValue(String.class);
                    String lastMessage = snapshot.child("lastMessage").getValue(String.class);
                    Date timestamp = snapshot.child("timestamp").getValue(Date.class);

                    // Check if this conversation already exists in the list
                    boolean conversationExists = false;
                    for (ChatMessage existingMessage : conversations) {
                        if ((existingMessage.senderid.equals(senderId) && existingMessage.receiverId.equals(receiverId)) ||
                                (existingMessage.senderid.equals(receiverId) && existingMessage.receiverId.equals(senderId))) {
                            // Update the existing conversation
                            existingMessage.message = lastMessage;
                            existingMessage.dateobject = timestamp;
                            conversationExists = true;
                            break;
                        }
                    }

                    if (!conversationExists) {
                        // Create a new conversation
                        ChatMessage chatMessage = new ChatMessage();
                        chatMessage.senderid = senderId;
                        chatMessage.receiverId = receiverId;
                        if (preferences.getString("loggedInUser", "").equals(senderId)){
                            chatMessage.conversationImage = snapshot.child("receiverImage").getValue(String.class);
                            chatMessage.conversationname = snapshot.child("receiverName").getValue(String.class);
                            chatMessage.conversationId =  receiverId;
                        } else {
                            chatMessage.conversationImage = snapshot.child("senderImage").getValue(String.class);
                            chatMessage.conversationname = snapshot.child("senderName").getValue(String.class);
                            chatMessage.conversationId = senderId;
                        }
                        chatMessage.message = lastMessage;
                        chatMessage.dateobject = timestamp;
                        conversations.add(chatMessage);
                    }
                }

                // Sort conversations by timestamp
                Collections.sort(conversations , (obj1 , obj2) -> obj2.dateobject.compareTo(obj1.dateobject));
                // Notify adapter of data changes
                recentConversationAdapter.notifyDataSetChanged();
                recyclerView.smoothScrollToPosition(0);
                recyclerView.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle potential errors here
            }
        };

        return chateventlistener;
    }
    private void listenConvo(String userID, ValueEventListener valueEventListener) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("conversations");

        // Query for conversations where the logged-in user is the sender
        Query query1 = databaseReference.orderByChild("senderId").equalTo(userID);
        query1.addValueEventListener(valueEventListener);

        // Query for conversations where the logged-in user is the receiver
        Query query2 = databaseReference.orderByChild("receiverId").equalTo(userID);
        query2.addValueEventListener(valueEventListener);
    }


    @Override
    public void OnConversationClicked(User user) {
        Intent intent = new Intent(getActivity(), chat2.class);
        intent.putExtra("user", user); // Add extras if needed
        startActivity(intent);
    }
}