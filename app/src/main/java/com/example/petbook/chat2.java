package com.example.petbook;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Base64;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.petbook.adapters.ChatAdapter;
import com.example.petbook.adapters.Recent_Conversation_Adapter;
import com.example.petbook.databinding.ActivityChat2Binding;
import com.example.petbook.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.EventListener;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class chat2 extends AppCompatActivity {
private User receiveruser;
private ActivityChat2Binding binding;
private ChatAdapter chatAdapter;
Recent_Conversation_Adapter conversationAdapter;
FirebaseAuth mAuth ;
private List<ChatMessage> chatMessages;
    private SharedPreferences preferences;




    String conversationid = null;
    private final ValueEventListener conversionValueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0) {
                // Assuming you're interested in the first child node
                DataSnapshot firstChildSnapshot = dataSnapshot.getChildren().iterator().next();
                conversationid = firstChildSnapshot.getKey();
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {
            // Handle onCancelled event
        }
    };

    FirebaseUser currentUser;

    String User_id;
    DatabaseReference messagesRef = FirebaseDatabase.getInstance().getReference("chat");




    ValueEventListener eventListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            // This method will be called whenever data at the specified database location is updated

            if (dataSnapshot.exists()) {
                // Loop through the children (chat messages)
                for (DataSnapshot messageSnapshot : dataSnapshot.getChildren()) {
                    // Extract the data from the snapshot
                    String senderId = messageSnapshot.child("senderID").getValue(String.class);
                    String receiverId = messageSnapshot.child("receiverID").getValue(String.class);
                    String message = messageSnapshot.child("message").getValue(String.class);
                    DataSnapshot timestampSnapshot = messageSnapshot.child("timestamp");

                    if (timestampSnapshot.child("time").exists()) {
                        long timestamp = timestampSnapshot.child("time").getValue(Long.class);
                        Date date = new Date(timestamp);

                        // Check if the message is from the sender or receiver
                        if (senderId.equals(User_id) && receiverId.equals(receiveruser.id)) {
                            // Message sent by the user to the receiver
                            ChatMessage chatMessage = new ChatMessage();
                            chatMessage.senderid = senderId;
                            chatMessage.receiverId = receiverId;
                            chatMessage.message = message;
                            chatMessage.dateobject = date;
                            chatMessages.add(chatMessage);
                        } else if (senderId.equals(receiveruser.id) && receiverId.equals(User_id)) {
                            // Message sent by the receiver to the user
                            ChatMessage chatMessage = new ChatMessage();
                            chatMessage.senderid = senderId;
                            chatMessage.receiverId = receiverId;
                            chatMessage.message = message;
                            chatMessage.dateobject = date;
                            chatMessages.add(chatMessage);
                        }
                    } else {
                        // Handle the case when the timestamp child doesn't exist
                    }
                    // Add chatMessage to your list or perform any other operation
                }
                Collections.sort(chatMessages, new Comparator<ChatMessage>() {
                    @Override
                    public int compare(ChatMessage obj1, ChatMessage obj2) {
                        return obj1.dateobject.compareTo(obj2.dateobject);
                    }
                });

                // Notify the adapter about the data change
                chatAdapter.notifyDataSetChanged();

                // Scroll to the last item in the RecyclerView
                if (!chatMessages.isEmpty()) {
                    binding.chatrv.smoothScrollToPosition(chatMessages.size() - 1);
                }

                // Make the RecyclerView visible
                binding.chatrv.setVisibility(View.VISIBLE);

                // Hide the progress bar
                binding.pbar2.setVisibility(View.GONE);
                if(conversationid != null){
                    checkforconvo();
                }
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            // This method will be called in case of any errors or if the listener is canceled
            // Handle errors or log the cancellation
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        binding = ActivityChat2Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        User_id = preferences.getString("loggedInUser", null);


        setListeners();
        loadreceiverdetails();
        init();
        listenmessages();


    }
    private void init(){
        chatMessages = new ArrayList<>();
        chatAdapter = new ChatAdapter(getUserImage(receiveruser.image),chatMessages, User_id);

        binding.chatrv.setAdapter(chatAdapter);
    }
    private void loadreceiverdetails(){
        receiveruser = (User) getIntent().getSerializableExtra("user");
        binding.textname2.setText(receiveruser.name);


    }

    private void listenmessages(){
        // Query for messages sent by User_id to receiveruser.id
        Query query1 = messagesRef.orderByChild("senderID").equalTo(User_id);
        query1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                handleMessagesDataChange(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle onCancelled
            }
        });

        // Query for messages sent by receiveruser.id to User_id
        Query query2 = messagesRef.orderByChild("senderID").equalTo(receiveruser.id);
        query2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                handleMessagesDataChange(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle onCancelled
            }
        });
    }

    private void handleMessagesDataChange(DataSnapshot dataSnapshot) {
        if (dataSnapshot.exists()) {
            for (DataSnapshot messageSnapshot : dataSnapshot.getChildren()) {
                String senderId = messageSnapshot.child("senderID").getValue(String.class);
                String receiverId = messageSnapshot.child("receiverID").getValue(String.class);
                String message = messageSnapshot.child("message").getValue(String.class);
                DataSnapshot timestampSnapshot = messageSnapshot.child("timestamp");

                if (timestampSnapshot.child("time").exists()) {
                    long timestamp = timestampSnapshot.child("time").getValue(Long.class);
                    Date date = new Date(timestamp);

                    // Check if the message is from the sender or receiver
                    if ((senderId.equals(User_id) && receiverId.equals(receiveruser.id)) ||
                            (senderId.equals(receiveruser.id) && receiverId.equals(User_id))) {
                        // Check if the message is already present in chatMessages
                        boolean isMessageExists = false;
                        for (ChatMessage existingMessage : chatMessages) {
                            if (existingMessage.message.equals(message) &&
                                    existingMessage.senderid.equals(senderId) &&
                                    existingMessage.receiverId.equals(receiverId)) {
                                isMessageExists = true;
                                break;
                            }
                        }

                        if (!isMessageExists) {
                            ChatMessage chatMessage = new ChatMessage();
                            chatMessage.senderid = senderId;
                            chatMessage.receiverId = receiverId;
                            chatMessage.message = message;
                            chatMessage.dateobject = date;
                            chatMessages.add(chatMessage);
                        }
                    }
                }
            }

            Collections.sort(chatMessages, new Comparator<ChatMessage>() {
                @Override
                public int compare(ChatMessage obj1, ChatMessage obj2) {
                    return obj1.dateobject.compareTo(obj2.dateobject);
                }
            });

            chatAdapter.notifyDataSetChanged();

            if (!chatMessages.isEmpty()) {
                binding.chatrv.smoothScrollToPosition(chatMessages.size() - 1);
            }

            binding.chatrv.setVisibility(View.VISIBLE);
            binding.pbar2.setVisibility(View.GONE);
        }
    }

    private Bitmap getUserImage(String encodedImage) {
        byte[] bytes = Base64.decode(encodedImage, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

    private void setListeners(){
    binding.imageback.setOnClickListener(view -> onBackPressed());
    binding.layoutSend.setOnClickListener(view -> sendMessage());}

    private void sendMessage(){
        if (conversationid != null){
            updateMessages(binding.inputMessage.getText().toString());
        }
        else {
            checkForExistingConversation();


        }
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("chat");
        String userId = usersRef.push().getKey();
        usersRef.child(userId).child("senderID").setValue(User_id);
        usersRef.child(userId).child("receiverID").setValue(receiveruser.id);
        usersRef.child(userId).child("message").setValue(binding.inputMessage.getText().toString());
        usersRef.child(userId).child("timestamp").setValue(new Date());







    }

    private void checkforconvo(){
        if(chatMessages.size() != 0){
            checkForConversationRemotely2(preferences.getString("loggedInUser", ""),receiveruser.id);
            checkForConversationRemotely2(receiveruser.id,preferences.getString("loggedInUser", ""));
        }


    }



    private String getreadabledate(Date date){
        return new SimpleDateFormat("MMMM dd, yyyy - hh:mm a", Locale.getDefault()).format(date);
    }

//    private void checkForConversationRemotely(String senderId, String receiverId) {
//        DatabaseReference conversationsRef = FirebaseDatabase.getInstance().getReference("conversations");
//
//        conversationsRef.orderByChild("senderId").equalTo(senderId)
//                .addListenerForSingleValueEvent(conversionValueEventListener);
//    }

    private void addToFirebaseDatabase(HashMap<String, Object> conversation) {
        DatabaseReference conversationsRef = FirebaseDatabase.getInstance().getReference("conversations");
        conversationsRef.push().setValue(conversation, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError == null) {
                    String conversationId = databaseReference.getKey();
                    // Do something with the conversationId if needed
                } else {
                    // Handle the error
                }
            }
        });
    }

    private void updateMessages(String message) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("conversations").child(conversationid);

        Map<String, Object> updates = new HashMap<>();
        updates.put("lastMessage", message);
        updates.put("timestamp",new Date());

        databaseReference.updateChildren(updates);
    }


    public void checkForConversationRemotely2(String senderId, String receiverId) {


        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference conversationsRef = database.getReference("conversations");

        conversationsRef.orderByChild("senderId").equalTo(senderId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String receiverId = snapshot.child("receiverId").getValue(String.class);
                    if (receiverId != null && receiverId.equals(receiverId)) {
                        conversationid = snapshot.getKey();
                        // Do something with the conversationId

                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle potential errors here
                System.err.println("Error: " + databaseError.getMessage());
            }
        });
    }
    private void checkForExistingConversation() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference conversationsRef = database.getReference("conversations");

        conversationsRef.orderByChild("senderId").equalTo(User_id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String receiverId = snapshot.child("receiverId").getValue(String.class);
                    if (receiverId != null && receiverId.equals(receiveruser.id)) {
                        conversationid = snapshot.getKey();
                        // Update existing conversation
                        updateMessages(binding.inputMessage.getText().toString());
                        binding.inputMessage.setText(null);
                        return;
                    }
                }
                // No existing conversation found, create a new one
                createNewConversation();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle potential errors here
                System.err.println("Error: " + databaseError.getMessage());
            }
        });
    }

    private void createNewConversation() {
        // Create a new conversation entry
        HashMap<String, Object> conversation = new HashMap<>();
        conversation.put("senderId", User_id);
        conversation.put("receiverId", receiveruser.id);
        conversation.put("lastMessage", binding.inputMessage.getText().toString());
        conversation.put("timestamp", new Date());
        conversation.put("senderImage",preferences.getString("userprof", ""));
        conversation.put("receiverName", receiveruser.name);
        conversation.put("receiverImage",receiveruser.image);
        conversation.put("senderName",preferences.getString("name", ""));
        addToFirebaseDatabase(conversation);
        binding.inputMessage.setText(null);
    }

}

