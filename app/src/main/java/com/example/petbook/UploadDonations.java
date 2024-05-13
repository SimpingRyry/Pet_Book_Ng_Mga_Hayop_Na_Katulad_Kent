package com.example.petbook;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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

public class UploadDonations extends AppCompatActivity {

    private Button uploadButton;
    private ImageView uploadImage;
    EditText uploadCaption;
    EditText contact;
    EditText description;
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
        setContentView(R.layout.activity_upload_donations);
        preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        uploadButton = findViewById(R.id.uploadButton);
        uploadCaption = findViewById(R.id.petname);
        contact = findViewById(R.id.contact);
        description = findViewById(R.id.descriptiondonate);
        uploadImage = findViewById(R.id.uploadImage);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);

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
                            Toast.makeText(UploadDonations.this, "No Image Selected", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );



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
                String descriptiontext = description.getText().toString();
                if (imageUri != null && caption != null && contactnum != null && descriptiontext != null ){
                    uploadToFirebase(imageUri);
                } else  {
                    Toast.makeText(UploadDonations.this, "Please fill up required fields", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    //Outside onCreate
    private void uploadToFirebase(Uri uri){
        String caption = uploadCaption.getText().toString();
        String contactnum = contact.getText().toString();
        String descriptiontext = description.getText().toString();
        final StorageReference imageReference = storageReference.child(System.currentTimeMillis() + "." + getFileExtension(uri));
        imageReference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                imageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        DonationsDataClass dataClass = new DonationsDataClass(uri.toString(), caption,contactnum,descriptiontext);
                        String key = databaseReference.push().getKey();
                        databaseReference.child(preferences.getString("account_type","")).child(preferences.getString("loggedInUser", "")).child("donations").child(key).setValue(dataClass);
                        progressBar.setVisibility(View.INVISIBLE);
                        Toast.makeText(UploadDonations.this, "Uploaded", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(UploadDonations.this, MainActivity.class);
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
                Toast.makeText(UploadDonations.this, "Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private String getFileExtension(Uri fileUri){
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(contentResolver.getType(fileUri));
    }
}