package com.example.paul.quest;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class LoginEmail extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.login_email);
        Button login_btn, register;
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        login_btn = findViewById(R.id.login_btn);
        register = findViewById(R.id.register_btn);

        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() != null) {
            finish();
            startActivity(new Intent(this, Home.class));

        }

        //listener for login button
        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                String getEmail = etEmail.getText().toString().trim();//grab text from edit text for username
                String getPassword = etPassword.getText().toString().trim();//grab text from edit text for password
                callLogin(getEmail,getPassword);//call function to login with grabbed username and login
            }
        });
        //listener for register button
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                String getEmail = etEmail.getText().toString().trim();//grab text from edit text for username
                String getPassword = etPassword.getText().toString().trim();//grab text from edit text for password
                callRegister(getEmail,getPassword);//call function to register with grabbed username and password
            }
        });
    }

    //Start sign in process
    private void callLogin(String email,String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d("TESTING", "Sign In Successful" + task.isSuccessful());
                        if (!task.isSuccessful()) {//if failed login, notify user
                            Toast.makeText(LoginEmail.this,"Failed",Toast.LENGTH_SHORT).show();
                        }
                        else {//if successful sign in, go to home home screen
                            Intent intent = new Intent(LoginEmail.this, Home.class);
                            startActivity(intent);
                            finish();
                        }
                    }
                });
    }

    //Function to register a new account
    private void callRegister(String email,String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d("TESTING", "createUserWithEmail:onComplete:" + task.isSuccessful());
                        if (!task.isSuccessful()) { //if fail, display fail message to user
                            Toast.makeText(LoginEmail.this,"Registration Failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                        else { //if successful
                            CreateProfile();//call function to create profile in database
                            Toast.makeText(LoginEmail.this,"Registration Complete.", Toast.LENGTH_SHORT).show();//notify user successful
                            Log.d("TESTING","Created Account");
                        }
                    }
                });
    }

    //Create Account
    private void CreateProfile() {
        FirebaseUser user = mAuth.getCurrentUser();
        if(user != null) {
            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                    .setDisplayName(etEmail.getText().toString().trim())//set display name for user
                    .build();
            user.updateProfile(profileUpdates)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {//log successful creation of user
                                Log.d("TESTING", "User Profile created.");
                            } else {
                                Log.d("TESTING", "User Profile not created.");

                            }
                        }
                    });
        }
    }
}