package com.example.travelbuddy;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;


public class Profile extends AppCompatActivity {
    TextView FullName, Email, Phone, VerigyMg;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String UserId;
    Button DeleteBtn, VerifyBtn, ResetPass, ChangeProImage, ModifyPro;
    ImageView ProfileImage;
    StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        FullName = findViewById(R.id.Fullname);
        Email = findViewById(R.id.email);
        Phone = findViewById(R.id.phone);
        DeleteBtn = findViewById(R.id.delete);
        VerigyMg = findViewById(R.id.verifymg);
        VerifyBtn = findViewById(R.id.verifyBtn);
        ResetPass =  findViewById(R.id.resetPassword);
        ModifyPro = findViewById(R.id.ModifyProfile);
        ProfileImage = findViewById(R.id.profileImage);
        ChangeProImage = findViewById(R.id.ChangeImage);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        UserId = fAuth.getCurrentUser().getUid();
        final FirebaseUser user = fAuth.getCurrentUser();
        
        storageReference = FirebaseStorage.getInstance().getReference(); // to upload pictures

       // Store picture by the current user
        StorageReference ProfileRef = storageReference.child("users/"+fAuth.getCurrentUser().getUid()+"/Profile.jpg");
        ProfileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(ProfileImage);
            }
        });


        // if the user email is not verify
        if(!user.isEmailVerified()) {
            VerifyBtn.setVisibility(View.VISIBLE);

            VerifyBtn.setOnClickListener(v ->
                    user.sendEmailVerification().addOnSuccessListener(aVoid ->
                            Toast.makeText(v.getContext(), "Verification Email Has Been Sent. ", Toast.LENGTH_SHORT).show()
                    ).addOnFailureListener(e ->
                            Log.d("tag", "onFailure: Email Was Not Sent " + e.getMessage())));
        }

        // displaying the information inside the user collection
        DocumentReference documentReference = fStore.collection("users").document(UserId);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {

                FullName.setText(documentSnapshot.getString("Full Name"));
                Email.setText(documentSnapshot.getString("Email"));
                Phone.setText(documentSnapshot.getString("Phone"));

            }
        });

        // Change the password locally process start here..
        ResetPass.setOnClickListener(v -> {
            final EditText resetPassword = new EditText(v.getContext());
            final AlertDialog.Builder passwordReset = new AlertDialog.Builder(v.getContext());
            passwordReset.setTitle("Change Your Password?");
            passwordReset.setMessage("Enter your New Password, must be 6 or more characters long. ");
            passwordReset.setView(resetPassword);

            passwordReset.setPositiveButton("Change", (dialog, which) -> {
                // get the email and send the link
                String newPass = resetPassword.getText().toString();
                user.updatePassword(newPass).addOnSuccessListener(aVoid ->
                        Toast.makeText(Profile.this, "Password Reset Successfully", Toast.LENGTH_SHORT).show()
                ).addOnFailureListener(e ->
                        Toast.makeText(Profile.this, "Password Reset Failed", Toast.LENGTH_SHORT).show());

            });

            passwordReset.setNegativeButton("Close", (dialog, which) -> {
                // close the dialog box
            });

            passwordReset.create().show(); // show the dialog box

        });

        // Delete current login user account
        DeleteBtn.setOnClickListener(v -> {
            AlertDialog.Builder Delete = new AlertDialog.Builder(v.getContext());
            Delete.setTitle("Account Will Be Deleted Permanently");
            Delete.setMessage("Are you sure? ");

            Delete.setPositiveButton("Delete", (dialog, which) -> {

                // Delete the current user logging information from the database
                fAuth.getCurrentUser().delete();

                // Delete user itinerary
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child(UserId);
                ref.removeValue();

                // Delete user notes
                DatabaseReference note = FirebaseDatabase.getInstance().getReference().child("Notes").child(UserId);
                note.removeValue();

                // Delete the profile picture of the user that is been removed from the database
                storageReference.child("users/"+UserId+"/Profile.jpg").delete();

                // Delete the current logged user data information already stored
                fStore.collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).delete()

                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(Profile.this, "Account Has Been Deleted", Toast.LENGTH_SHORT).show();
                            fAuth.signOut();
                            startActivity(new Intent(getApplicationContext(), Register.class));
                            finish();
                        }).addOnFailureListener(e -> {
                            Toast.makeText(Profile.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(), Login.class));
                        });
            });

            Delete.setNegativeButton("Dismiss", null);
            Delete.create().show(); // show the dialog box
        });

        // Modify Profile information
        ModifyPro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(v.getContext(), EditProfile.class);
                // passing the data from the current user
                i.putExtra("Full Name", FullName.getText().toString());
                i.putExtra("Email", Email.getText().toString());
                i.putExtra("Phone Number", Phone.getText().toString());
                startActivity(i);
            }
        });

        // change profile picture process start here..
        ChangeProImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open the Gallery App on device
                Intent GalleryInt = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(GalleryInt, 1000);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1000) {
            if (resultCode == Activity.RESULT_OK){
                Uri imageUri = data.getData();
               // ProfileImage.setImageURI(imageUri);
                
                uploadImage(imageUri);

            }

        }
    }

    private void uploadImage(Uri imageUri) {
        // upload images to the firebase storage 
        StorageReference fileReferance = storageReference.child("users/"+fAuth.getCurrentUser().getUid()+"/Profile.jpg");
        fileReferance.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                fileReferance.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.get().load(uri).into(ProfileImage);
                    }
                });

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Profile.this, "Image Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Go back to the main menu
    public void back(View view) {
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
    }
}