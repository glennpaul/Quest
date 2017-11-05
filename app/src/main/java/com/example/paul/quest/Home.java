package com.example.paul.quest;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;


public class Home extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private String userID;
    private TextView header;
    private EditText newQuestText, etFriendName;
    private DatabaseReference myRef = FirebaseDatabase.getInstance().getReference(); //get reference for root of database
    private String questListID,quest_count;
    private String referencedFriend;
    private boolean friend_activated;

    private ChildEventListener latest;
    private ArrayList<Quest> quests;
    private QuestAdapter questAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        RecyclerView rv = findViewById(R.id.rv);
        rv.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        rv.setLayoutManager(llm);

        quests = new ArrayList<>();
        questAdapter = new QuestAdapter(quests);
        rv.setAdapter(questAdapter);


        header = findViewById(R.id.Quest_header);
        Button sign_out = findViewById(R.id.sign_out);
        Button add_quest_btn = findViewById(R.id.addQuestbtn);
        newQuestText = findViewById(R.id.etAddQuest);
        etFriendName = findViewById(R.id.friend_prompt);


        Button friend_quests_btn = findViewById(R.id.friendQuestsBtn);
        friend_activated = false;

        //grab current user and ID for use of grabbing quest list
        mAuth = FirebaseAuth.getInstance();
        if(mAuth.getCurrentUser()!=null) {
            FirebaseUser current_user = mAuth.getCurrentUser();
            userID = current_user.getUid();
            //Toast.makeText(Home.this,userID,Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(Home.this,"Current user is null.",Toast.LENGTH_SHORT).show();
        }

        //grab quest list and create listener for value change
        setListener(userID);

        add_quest_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                quest_count = String.valueOf(Integer.parseInt(quest_count)+1);
                myRef.child("QuestList").child(questListID).child(quest_count).setValue(newQuestText.getText().toString());
                myRef.child("QuestList").child(questListID).child("Count").setValue(quest_count);
            }
        });
        //go to list of friend quests when friend quest button is pressed
        friend_quests_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                quests.clear();
                if (!friend_activated) {
                    friend_activated = true;
                    referencedFriend = etFriendName.getText().toString();
                    myRef.child("QuestList").orderByChild("ID").equalTo(userID).removeEventListener(latest);
                    setListener(referencedFriend);
                } else {
                    myRef.child("QuestList").orderByChild("ID").equalTo(referencedFriend).removeEventListener(latest);
                    setListener(userID);
                    friend_activated = false;
                }
            }
        });
        //listener for sign out button, if clicked, sign out of account and go back to login screen
        sign_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                LoginManager.getInstance().logOut();
                finish();
                startActivity(new Intent(getApplicationContext(),MainActivity.class));
            }
        });
        //listener for add_quest_btn to the specified quest in the edit text
        add_quest_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                quest_count = String.valueOf(Integer.parseInt(quest_count)+1);
                myRef.child("QuestList").child(questListID).child(quest_count).setValue(newQuestText.getText().toString());
                myRef.child("QuestList").child(questListID).child("Count").setValue(quest_count);
            }
        });

        Button fbtn = findViewById(R.id.friends_button);
        //listener for sign out button, if clicked, sign out of account and go back to login screen
        fbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),Friends.class));
            }
        });

    }

    public void onDestroy() {
        super.onDestroy();
        //sign out before leaving app
        mAuth.signOut();
        LoginManager.getInstance().logOut();
    }

    public void addToQuestList(String item) {
        quests.add(new Quest(item, true,myRef));//adds quest to end of list
    }

    public static void removeFromQuestList(DatabaseReference ref, Integer position) {
        ref.child(String.valueOf(position+1)).removeValue();//removes item in quests list at signified position, use +1 since position of quest list starts at 0
    }

    public void setListener(String ID) {
        //set the listener to constantly update quest list
        //takes in a String that indicates the UserID that indicates the correct quest list
        myRef.child("QuestList").orderByChild("ID").equalTo(ID).addChildEventListener(latest = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                questListID = dataSnapshot.getKey();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    if(!postSnapshot.getKey().equals("ID") & !postSnapshot.getKey().equals("Count") & postSnapshot.getValue() != null) {
                        addToQuestList(postSnapshot.getValue().toString());
                    } else if (postSnapshot.getKey().equals("Count")){
                        quest_count = postSnapshot.getValue().toString();
                        String quest_count_header = "Quests: " + quest_count;
                        header.setText(quest_count_header);
                    }
                }
                questAdapter.notifyDataSetChanged();
            }
            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                quests.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    if(!postSnapshot.getKey().equals("ID") & !postSnapshot.getKey().equals("Count") & postSnapshot.getValue() != null) {
                        addToQuestList(postSnapshot.getValue().toString());
                    } else if (postSnapshot.getKey().equals("Count")){
                        //if count value changes, update quest list count header
                        quest_count = postSnapshot.getValue().toString();
                        String quest_count_header = "Quests: " + quest_count;
                        header.setText(quest_count_header);
                    }
                }
                questAdapter.notifyDataSetChanged();
            }
            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
            }
            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }
            @Override
            public void onCancelled(DatabaseError e) {
            }
        });
    }
}
