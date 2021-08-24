package com.example.travelbuddy;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Register extends AppCompatActivity {
    public static final String TAG = "TAG";
    EditText mFullName, mEmail, mPassword, mRepeat,  mPhone;
    Button mRegisterBtn;
    TextView mLoginBtn;
    FirebaseAuth fAuth;
    ProgressBar progressBar;
    String userID;
    FirebaseFirestore fStore;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mFullName = findViewById(R.id.fullName);
        mEmail = findViewById(R.id.Email);
        mPassword = findViewById(R.id.password);
        mRegisterBtn = findViewById(R.id.registerBtn);
        mLoginBtn = findViewById(R.id.createText);
        mRepeat = findViewById(R.id.repeatPass);
        mPhone = findViewById(R.id.phone);

        progressBar = findViewById(R.id.progressBar);
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        if(fAuth.getCurrentUser() != null) {
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        }

        // the register process start here...
        mRegisterBtn.setOnClickListener(v -> {
            String email  = mEmail.getText().toString().trim();
            String password = mPassword.getText().toString().trim();
            String fullName =  mFullName.getText().toString();
            String repeatPass = mRepeat.getText().toString().trim();
            String phone = mPhone.getText().toString();

            if(TextUtils.isEmpty(email)){
                mEmail.setError("Email is Required.");
                return;
            }

            if(TextUtils.isEmpty(password)) {
                mPassword.setError("Password is Required.");
                return;
            }

            if(password.length() < 6) {
                mPassword.setError("Password must be 6 or more Characters.");
                return;
            }

            if(!password.equals(repeatPass)){
                mRepeat.setError("Password do not Match");
                return;
            }

            progressBar.setVisibility(View.VISIBLE);

            // Register the user in the firebase database
            fAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){

                        // Verify the user email address
                        FirebaseUser veriuser = fAuth.getCurrentUser(); // creating the object
                        veriuser.sendEmailVerification().addOnSuccessListener(aVoid ->
                                Toast.makeText(Register.this, "Verification Email Has Been sent.",
                                        Toast.LENGTH_SHORT).show()).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d(TAG, "onFailure: Email Was Not Sent " + e.getMessage());
                            }
                        });

                        Toast.makeText(Register.this, "User Have Been Created", Toast.LENGTH_SHORT).show();

                        userID = fAuth.getCurrentUser().getUid();

                        // creating a collection to store the user information
                        DocumentReference documentReference = fStore.collection("users").document(userID);
                        Map<String,Object> user = new HashMap<>();
                        user.put("Full Name", fullName);
                        user.put("Email", email);
                        user.put("Phone", phone);
                        documentReference.set(user).addOnSuccessListener(aVoid -> {
                            Log.d(TAG, "onSuccess: user profile is created for " + userID);
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d(TAG, "onFailure: "+ e.toString());
                            }
                        });
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    } else {
                        Toast.makeText(Register.this, "Error! "+ task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                    }
                }
            });
        });

        // go to the login page form
        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Login.class));
            }
        });

    }
}