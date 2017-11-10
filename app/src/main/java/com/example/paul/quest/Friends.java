package com.example.paul.quest;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import static android.widget.Toast.LENGTH_SHORT;

public class Friends extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.friends);

        StorageReference mStorage;
        TextView Name = findViewById(R.id.tv_Name);
        TextView ID = findViewById(R.id.tv_ID);
        FirebaseAuth mAuth;
        String userID, userName;

        //grab current user and ID for use of grabbing quest list
        mAuth = FirebaseAuth.getInstance();
        if(mAuth.getCurrentUser()!=null) {
            FirebaseUser current_user = mAuth.getCurrentUser();
            userID = current_user.getUid();
            userName = current_user.getDisplayName();

            String temp = "Name: " + userName;
            Name.setText(temp);
            temp = "ID: " + userID;
            ID.setText(temp);
            mStorage = FirebaseStorage.getInstance().getReference().child(userID + "_profile_picture.jpg");
            ImageView profile_pic = findViewById(R.id.imageView);
            Glide.with(this).using(new FirebaseImageLoader()).load(mStorage).into(profile_pic);
        } else {
            Toast.makeText(Friends.this,"Current user is null.", LENGTH_SHORT).show();
        }




    }
}
