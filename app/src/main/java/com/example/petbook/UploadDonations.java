package com.example.petbook;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class UploadDonations extends AppCompatActivity {

    private Button uploadButton;
    private ImageView uploadImage, back;
    EditText petName;
    EditText contact;
    EditText description;
    ProgressBar progressBar;
    Spinner spinner;
    String status;
    private Uri imageUri;
    private SharedPreferences preferences;
    final  private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users");

    final private StorageReference storageReference = FirebaseStorage.getInstance().getReference();

    private void showAlertDialog(String title, String message) {
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, null)
                .show();
    }
    private boolean isValidContactNumber(String contactnum) { return contactnum.matches("^09\\d{9}$"); }
    private boolean isAlphabetic(String text) {
        return text.matches("^[a-zA-Z ]+$");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_donations);
        preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        uploadButton = findViewById(R.id.uploadButton);
        petName = findViewById(R.id.petname);
        contact = findViewById(R.id.contact);
        description = findViewById(R.id.descriptiondonate);
        uploadImage = findViewById(R.id.uploadImage);
        progressBar = findViewById(R.id.progressBar);
        back = findViewById(R.id.back);
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

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                FragmentManager fragmentManager = getSupportFragmentManager();
//
//                // Begin FragmentTransaction
//                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//
//                // Replace or Add Fragment
//                DonationFragment fragment = new DonationFragment(); // Initialize your Fragment instance
//                fragmentTransaction.replace(R.id.mainlayout, fragment); // Use replace() if you want to replace existing fragment, use add() if you want to add it to existing fragments
//
//                // Add to Back Stack (Optional)
//                fragmentTransaction.addToBackStack(null); // You can provide a tag for identification
//
//                // Commit Transaction
//                fragmentTransaction.commit();
                Intent intent = new Intent(UploadDonations.this,MainActivity.class);
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
                String pet_name = petName.getText().toString();
                String contactnum = contact.getText().toString();
                String descriptiontext = description.getText().toString();

                if (NetworkUtils.isInternetConnected(getApplicationContext())) {
                    if (imageUri != null) {
                        if (pet_name != null && !pet_name.isEmpty() && isAlphabetic(pet_name)) {
                            if (contactnum != null && !contactnum.isEmpty() && isValidContactNumber(contactnum)) {
                                if (descriptiontext != null && !descriptiontext.isEmpty()) {
                                    uploadToFirebase(imageUri);
                                } else {
                                    showAlertDialog("Invalid Description", "Please input a description for you fundraising activity.");
                                }
                            } else {
                                showAlertDialog("Invalid Contact Number", "Please input your proper contact info");
                            }
                        } else {
                            showAlertDialog("Invalid Pet Name", "Please input your proper pet name");
                        }
                    } else {
                        showAlertDialog("Image Missing", "Please upload your image");
                    }
                } else {
                    Toast.makeText(getApplicationContext(),"Please ensure network connectivity",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    //Outside onCreate
    private void uploadToFirebase(Uri uri){
        String caption = petName.getText().toString();
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
    @Override
    public void onBackPressed() {
        // Do nothing

    }
}