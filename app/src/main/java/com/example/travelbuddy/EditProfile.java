package com.example.travelbuddy;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class EditProfile extends AppCompatActivity {
    EditText FullName, EmailAddress, PhoneNumber;
    Button Save;
    ImageView ProfileIm;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    FirebaseUser user;
    StorageReference storageReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        FullName = findViewById(R.id.fullNa);
        EmailAddress = findViewById(R.id.emailAd);
        PhoneNumber = findViewById(R.id.PhoneNu);

        Save = findViewById(R.id.save);
        ProfileIm = findViewById(R.id.changePic);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        user = fAuth.getCurrentUser();
        storageReference = FirebaseStorage.getInstance().getReference();

        // getting the data that is being process from current user on Profile
        Intent data = getIntent();
        String fullname = data.getStringExtra("Full Name");
        String email = data.getStringExtra("Email");
        String phone = data.getStringExtra("Phone Number");

        Log.d("TAG", "Create:" + fullname + " " + email + " " + phone);

        FullName.setText(fullname);
        EmailAddress.setText(email);
        PhoneNumber.setText(phone);

        // to display the profile picture
        StorageReference ProfilePic = storageReference.child("users/" + fAuth.getCurrentUser().getUid() + "/Profile.jpg");
        ProfilePic.getDownloadUrl().addOnSuccessListener(uri ->
                Picasso.get().load(uri).into(ProfileIm));

        // open the gallery for pictures
        ProfileIm.setOnClickListener(v -> {
            Intent GalleryInt = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(GalleryInt, 1000);
        });

        // Edit Profile information Process start here ...
        Save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(FullName.getText().toString().isEmpty() || EmailAddress.getText().toString().isEmpty() || PhoneNumber.getText().toString().isEmpty()) {
                    Toast.makeText(EditProfile.this, "One Or More Fields Are Empty", Toast.LENGTH_SHORT).show();
                    return;
                }

                // To update email address
                final String email = EmailAddress.getText().toString();
                user.updateEmail(email).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // To update the information inside the database
                        DocumentReference Reference = fStore.collection("users").document(user.getUid());
                        Map<String,Object> Updated = new HashMap<>();
                        Updated.put("Full Name", FullName.getText().toString());
                        Updated.put("Email", email); //updating the new email
                        Updated.put("Phone", PhoneNumber.getText().toString());
                        Reference.update(Updated).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(EditProfile.this, "Profile Was Updated", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(getApplicationContext(), Profile.class));
                                finish();
                            }
                        });
                      //  Toast.makeText(EditProfile.this, "Email Has Been Changed.", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(EditProfile.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

                // go back to Profile Class
                startActivity(new Intent(getApplicationContext(), Profile.class));
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1000) {
            if (resultCode == Activity.RESULT_OK){
                Uri imageUri = data.getData();

                uploadImage(imageUri);

            }
        }
    }

    private void uploadImage(Uri imageUri) {
        // upload images to the firebase storage
        StorageReference fileRef = storageReference.child("users/"+fAuth.getCurrentUser().getUid()+"/Profile.jpg");
        fileRef.putFile(imageUri).addOnSuccessListener(taskSnapshot ->
                fileRef.getDownloadUrl().addOnSuccessListener(uri ->
                        Picasso.get().load(uri).into(ProfileIm))).addOnFailureListener(e ->
                Toast.makeText(getApplicationContext(), "Image Failed", Toast.LENGTH_SHORT).show());
    }
}