
package com.example.petbook;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class shelter_login extends AppCompatActivity {
    EditText username,password;
    ImageButton signin;
    TextView signup_redirect;
    Intent intent;
    private FirebaseAuth mAuth;
    String data;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shelter_login);

        intent = getIntent();
        data = intent.getStringExtra("key");

        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        mAuth = FirebaseAuth.getInstance();
        signin = findViewById(R.id.signin);
        signup_redirect = findViewById(R.id.signup);

        signup_redirect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(shelter_login.this, shelter_signup.class);
                intent.putExtra("name", data);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (NetworkUtils.isInternetConnected(getApplicationContext())) {
                    if (validateUsername() && validatePassword()){
                        checkUser(data);
                    }
                    // Device is connected to the internet
                } else {
                    Toast.makeText(shelter_login.this,"Please ensure network connectivity",Toast.LENGTH_SHORT).show();
                    // Device is not connected to the internet
                }



            }
        });


    }

    public void saveLoggedInUser(Context context, String username, String imageprof,String name,String address, String email) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("loggedInUser", username);
        editor.putString("userprof",imageprof);
        editor.putString("name", name);
        editor.putString("account_type", "shelter");
        editor.putString("address", address);
        editor.putString("email", email);
        editor.apply();
    }

    public Boolean validateUsername(){
        String val = username.getText().toString();
        if (val.isEmpty()){
            username.setError("Username cannot be empty");
            return false;
        } else {
            username.setError(null);
            return true;
        }
    }

    public Boolean validatePassword(){
        String val = password.getText().toString();
        if (val.isEmpty()){
            password.setError("Password cannot be empty");
            return false;
        } else {
            password.setError(null);
            return true;
        }

    }

    public void checkUser(String key){
        String userUsername = username.getText().toString().trim();
        String userPassword = password.getText().toString().trim();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users").child("shelter");
        Query checkUserDatabase = reference.orderByChild("username").equalTo(userUsername);

        checkUserDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()) {
                    username.setError(null);
                    String passwordFromDB = snapshot.child(userUsername).child("password").getValue(String.class);

                    if (passwordFromDB.equals(userPassword)) {
                        username.setError(null);




                        //Pass the data using intent

                        String nameFromDB = snapshot.child(userUsername).child("name").getValue(String.class);
                        String emailFromDB = snapshot.child(userUsername).child("email").getValue(String.class);
                        String usernameFromDB = snapshot.child(userUsername).child("username").getValue(String.class);
                        String imageprof = snapshot.child(userUsername).child("image").getValue(String.class);
                        String address = snapshot.child(userUsername).child("address").getValue(String.class);

                        saveLoggedInUser(getApplicationContext(), userUsername,imageprof,nameFromDB,address,emailFromDB);


                        Intent intent = new Intent(shelter_login.this, MainActivity.class);
                        intent.putExtra("name", nameFromDB);
                        intent.putExtra("email", emailFromDB);
                        intent.putExtra("username", usernameFromDB);





                        startActivity(intent);
                    } else {
                        password.setError("Invalid Credentials");
                        password.requestFocus();
                    }
                } else {
                    username.setError("User does not exist");
                    username.requestFocus();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        });


    }


}
