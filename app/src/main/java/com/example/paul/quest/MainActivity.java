package com.example.paul.quest;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.AuthResult;


public class MainActivity extends AppCompatActivity {

    private CallbackManager callbackManager;
    private FirebaseAuth mAuth;
        //private TextView fb_prompt, email_prompt;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //setup buttons and prompts
        LoginButton fb_button;
        Button email_login_btn;
        fb_button = findViewById(R.id.login_button);
        email_login_btn = findViewById(R.id.email_login_btn);
            //fb_prompt = findViewById(R.id.fb_login_prompt);
            //email_prompt = findViewById(R.id.email_login_prompt);

        //set FB permissions and callback manager
        fb_button.setReadPermissions("email", "public_profile");
        callbackManager = CallbackManager.Factory.create();

        fb_button.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                handleFacebookAccessToken(loginResult.getAccessToken());
            }
            @Override
            public void onCancel() {
                Log.d("GPS","Login was cancelled.");
            }
            @Override
            public void onError(FacebookException e) {
                Log.d("GPS","Login failed.");
            }
        });

        email_login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                Intent intent = new Intent(MainActivity.this, LoginEmail.class);
                startActivity(intent);
            }
        });
    }
    @Override
    public void onStart() {
        super.onStart();

        mAuth = FirebaseAuth.getInstance();

        if(mAuth.getCurrentUser()!=null) {
            Intent intent = new Intent(this, Home.class);
            startActivity(intent);
            //Toast.makeText(MainActivity.this,mAuth.getCurrentUser().getUid(),Toast.LENGTH_SHORT).show();
            //finish();
        } else {
            Log.d("GPS","No user logged in."); //Toast.makeText(MainActivity.this,"user is null",Toast.LENGTH_SHORT).show();//add for debugging
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void handleFacebookAccessToken(AccessToken token) {
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            //finish();
                            //Toast.makeText(MainActivity.this,"SUCCESS",Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(MainActivity.this, Home.class);
                            startActivity(intent);

                        } else {
                            Toast.makeText(MainActivity.this, "Authentication failed.",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

}
