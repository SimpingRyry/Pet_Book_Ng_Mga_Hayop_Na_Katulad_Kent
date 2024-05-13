package com.example.petbook;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.Firebase;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.makeramen.roundedimageview.RoundedImageView;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class shelter_signup extends AppCompatActivity {
    EditText signup_email,name,username,password,address;
    TextView signin_redirect, addimagetext;
    ImageButton signup;
    FirebaseDatabase database;
    DatabaseReference databaseReference;
    RoundedImageView imageprofile;
    private  String encodedImage;

    FrameLayout prof_frame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        String data = intent.getStringExtra("key");
        setContentView(R.layout.activity_shelter_signup);

        signup_email = findViewById(R.id.email);
        name = findViewById(R.id.name);

        username = findViewById(R.id.username);

        password = findViewById(R.id.password);

        address = findViewById(R.id.address);
        signup = findViewById(R.id.signuobtn);
        signin_redirect = findViewById(R.id.signin);
        imageprofile = findViewById(R.id.imageprof);
        addimagetext = findViewById(R.id.textaddimage);
        prof_frame = findViewById(R.id.profile_frame);

        prof_frame.setOnClickListener(view -> {
            Intent intent1 = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent1.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            pickimage.launch(intent1);
        });


        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                database = FirebaseDatabase.getInstance();
                databaseReference = database.getReference("users").child("shelter");

                String email = signup_email.getText().toString();
                String signup_name = name.getText().toString();

                String signup_username = username.getText().toString();

                String signup_pass = password.getText().toString();

                String signup_address = address.getText().toString();

                helperclass helperclass = new helperclass(signup_username,email,signup_pass,signup_address,signup_name,encodedImage);
                databaseReference.child(signup_username).setValue(helperclass);
                Log.d("toast", "success");
                Toast.makeText(com.example.petbook.shelter_signup.this, "Sign Up Successful", Toast.LENGTH_SHORT);

                Intent intent = new Intent(com.example.petbook.shelter_signup.this, shelter_login.class);
                intent.putExtra("name", data);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);



            }
        });
        signin_redirect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), shelter_login.class));
                overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
                finish();
            }
        });

    }
    private String encodeImage(Bitmap bitmap){
        int previewwidth = 150;
        int previewheight = bitmap.getHeight() * previewwidth / bitmap.getWidth();
        Bitmap previewBitmap = Bitmap.createScaledBitmap(bitmap, previewwidth, previewheight, false);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        previewBitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] bytes = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(bytes, Base64.DEFAULT);
    }

    private final ActivityResultLauncher<Intent> pickimage = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),result -> {
                if (result.getResultCode()== RESULT_OK){
                    if (result.getData() != null){
                        Uri imageuri = result.getData().getData();

                        try {
                            InputStream inputStream = getContentResolver().openInputStream(imageuri);
                            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                            imageprofile.setImageBitmap(bitmap);
                            addimagetext.setVisibility(View.GONE);
                            encodedImage = encodeImage(bitmap);



                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }



                    }
                }
            }
    );
}