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
import android.util.Log;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class UploadAdoption extends AppCompatActivity {

    private Button uploadButton;
    private ImageView uploadImage, back;
    EditText petName;
    EditText contact;
    EditText description,petage;
    ProgressBar progressBar;
    Spinner ageDescSpinner;
    String ageDescription;
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
        setContentView(R.layout.activity_upload_adoption);
        preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        uploadButton = findViewById(R.id.uploadButton);
        petName = findViewById(R.id.petname);
        petage = findViewById(R.id.age);
        contact = findViewById(R.id.adoptioncontact);
        back = findViewById(R.id.back);
        ageDescSpinner = findViewById(R.id.ageDesc);
        uploadImage = findViewById(R.id.uploadImage);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.age_descriptions, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ageDescSpinner.setAdapter(adapter);
        ageDescSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ageDescription = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
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
                            Toast.makeText(UploadAdoption.this, "No Image Selected", Toast.LENGTH_SHORT).show();
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
//                HomeFragment fragment = new HomeFragment(); // Initialize your Fragment instance
//                fragmentTransaction.replace(R.id.mainlayout, fragment); // Use replace() if you want to replace existing fragment, use add() if you want to add it to existing fragments
//
//                // Add to Back Stack (Optional)
//                fragmentTransaction.addToBackStack(null); // You can provide a tag for identification
//
//                // Commit Transaction
//                fragmentTransaction.commit();\

                Intent intent = new Intent(UploadAdoption.this,MainActivity.class);
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
                String pet_age = petage.getText().toString();

                if (NetworkUtils.isInternetConnected(getApplicationContext())) {
                    if (imageUri != null) {
                        if (pet_name != null && !pet_name.isEmpty() && isAlphabetic(pet_name)) {
                            if (pet_age != null && !pet_age.isEmpty()) {
                                if (contactnum != null && !contactnum.isEmpty() && isValidContactNumber(contactnum)) {
                                    uploadToFirebase(imageUri);
                                } else {
                                    showAlertDialog("Invalid Contact Number", "Please input your proper contact info");
                                }
                            } else {
                                showAlertDialog("Invalid Age", "Please input your pet's proper age");
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
        String pet_name = petName.getText().toString();
        String contactnum = contact.getText().toString();
        String pet_age_num = petage.getText().toString();
        String pet_age = pet_age_num + " " + ageDescription;
        final StorageReference imageReference = storageReference.child(System.currentTimeMillis() + "." + getFileExtension(uri));
        imageReference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                imageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        AdoptionDataClass dataClass = new AdoptionDataClass(uri.toString(), pet_name,pet_age,contactnum,preferences.getString("name", ""));
                        String key = databaseReference.push().getKey();
                        databaseReference.child(preferences.getString("account_type","")).child(preferences.getString("loggedInUser", "")).child("adoptions").child(key).setValue(dataClass);
                        progressBar.setVisibility(View.INVISIBLE);
                        Toast.makeText(UploadAdoption.this, "Uploaded", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(UploadAdoption.this, MainActivity.class);
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
                Toast.makeText(UploadAdoption.this, "Failed", Toast.LENGTH_SHORT).show();
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