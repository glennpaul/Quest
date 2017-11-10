package com.example.paul.quest;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import static android.widget.Toast.LENGTH_SHORT;

public class Friends extends AppCompatActivity {

    private DatabaseReference myRef = FirebaseDatabase.getInstance().getReference(); //get reference for root of database

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.friends);

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
        } else {
            Toast.makeText(Friends.this,"Current user is null.", LENGTH_SHORT).show();
        }



//        RecyclerView rv = findViewById(R.id.rv);
//        rv.setHasFixedSize(true);
//        LinearLayoutManager llm = new LinearLayoutManager(this);
//        rv.setLayoutManager(llm);
//
//        ArrayList friends = new ArrayList<>();





    }
}
