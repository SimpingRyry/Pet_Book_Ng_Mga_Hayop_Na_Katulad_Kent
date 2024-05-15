package com.example.petbook;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class UploadPetImage extends AppCompatActivity {

    private Button uploadButton;
    private ImageView uploadImage,back;


    EditText uploadCaption;
    EditText contact;
    ProgressBar progressBar;
    Spinner spinner;
    String status;
    private Uri imageUri;
    private SharedPreferences preferences;
    final  private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users");

    final private StorageReference storageReference = FirebaseStorage.getInstance().getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_pet_image);
        preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        uploadButton = findViewById(R.id.uploadButton);
        uploadCaption = findViewById(R.id.petname);
        contact = findViewById(R.id.contact);
        uploadImage = findViewById(R.id.uploadImage);
        back = findViewById(R.id.back);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);
        spinner = findViewById(R.id.lnf);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.spinner_items,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                status = parent.getItemAtPosition(position).toString();
                // Do something with the selected item
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do something when nothing is selected
            }
        });

        ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK){
                            Intent data = result.getData();
                            imageUri = data.getData();
                            uploadImage.setImageURI(imageUri);
                        } else {
                            Toast.makeText(UploadPetImage.this, "No Image Selected", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );

back.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
//        FragmentManager fragmentManager = getSupportFragmentManager();
//
//        // Begin FragmentTransaction
//        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//
//        // Replace or Add Fragment
//        LostAndFoundFragment fragment = new LostAndFoundFragment(); // Initialize your Fragment instance
//        fragmentTransaction.replace(R.id.mainlayout, fragment); // Use replace() if you want to replace existing fragment, use add() if you want to add it to existing fragments
//
//        // Add to Back Stack (Optional)
//        fragmentTransaction.addToBackStack(null); // You can provide a tag for identification
//
//        // Commit Transaction
//        fragmentTransaction.commit();
        Intent intent = new Intent(UploadPetImage.this,MainActivity.class);
        startActivity(intent);
    }
});

        uploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent photoPicker = new Intent();
                photoPicker.setAction(Intent.ACTION_GET_CONTENT);
                photoPicker.setType("image/*");
                activityResultLauncher.launch(photoPicker);
            }
        });

        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String caption = uploadCaption.getText().toString();
                String contactnum = contact.getText().toString();
                if (NetworkUtils.isInternetConnected(getApplicationContext())) {
                    if (imageUri != null && caption != null && contactnum != null && status != null ){
                        uploadToFirebase(imageUri);
                    } else  {
                        Toast.makeText(UploadPetImage.this, "Please fill up required fields", Toast.LENGTH_SHORT).show();
                    }
                    // Device is connected to the internet
                } else {
                    Toast.makeText(getApplicationContext(),"Please ensure network connectivity",Toast.LENGTH_SHORT).show();
                    // Device is not connected to the internet
                }


            }
        });
    }
    //Outside onCreate
    private void uploadToFirebase(Uri uri){
        String caption = uploadCaption.getText().toString();
        String contactnum = contact.getText().toString();
        final StorageReference imageReference = storageReference.child(System.currentTimeMillis() + "." + getFileExtension(uri));
        imageReference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                imageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        DataClass dataClass = new DataClass(uri.toString(), caption,contactnum,status);
                        String key = databaseReference.push().getKey();
                        databaseReference.child(preferences.getString("account_type","")).child(preferences.getString("loggedInUser", "")).child("images").child(key).setValue(dataClass);
                        progressBar.setVisibility(View.INVISIBLE);
                        Toast.makeText(UploadPetImage.this, "Uploaded", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(UploadPetImage.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                progressBar.setVisibility(View.VISIBLE);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressBar.setVisibility(View.INVISIBLE);
                Toast.makeText(UploadPetImage.this, "Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private String getFileExtension(Uri fileUri){
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(contentResolver.getType(fileUri));
    }

    @Override
    public void onBackPressed() {
        // Do nothing

    }
}