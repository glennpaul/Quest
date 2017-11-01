package com.example.paul.quest;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
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
import java.util.List;

public class Home extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private String userID;
    private ArrayAdapter<String> arrayAdapter;
    private TextView header;
    private EditText newQuestText, etFriendName;
    private List<String> quests;
    private DatabaseReference myRef = FirebaseDatabase.getInstance().getReference(); //get reference for root of database
    private String questListID,quest_count;
    private String referencedFriend;
    private boolean friend_activated;

    private ChildEventListener latest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);

        final ListView quest_list  = findViewById(R.id.quest_list);
        quests = new ArrayList<>();
        arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, quests);
        quest_list.setAdapter(arrayAdapter);


        header = findViewById(R.id.Quest_header);
        Button sign_out = findViewById(R.id.sign_out);
        Button add_quest_btn = findViewById(R.id.addQuestbtn);
        newQuestText = findViewById(R.id.etAddQuest);
        etFriendName = findViewById(R.id.friend_prompt);


        Button friend_quests_btn = findViewById(R.id.friendQuestsBtn);


        friend_activated = false;

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
        //listener for clicks in the array list for deleting quests
        quest_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(Home.this,String.valueOf(quests.size() - position), Toast.LENGTH_SHORT).show();
                removeFromQuestList(myRef.child("QuestList").child("1"),quests.size(), position);
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
                    myRef.child("QuestList").orderByChild("ID").equalTo(referencedFriend).addChildEventListener(latest = new ChildEventListener() {
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
                        }
                        @Override
                        public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                            quests.clear();
                            arrayAdapter.notifyDataSetChanged();
                            for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                if(!postSnapshot.getKey().equals("ID") & !postSnapshot.getKey().equals("Count") & postSnapshot.getValue() != null) {
                                    addToQuestList(postSnapshot.getValue().toString());
                                } else if (postSnapshot.getKey().equals("Count")){
                                    quest_count = postSnapshot.getValue().toString();
                                    String quest_count_header = "Quests: " + quest_count;
                                    header.setText(quest_count_header);
                                }
                            }
                        }
                        @Override
                        public void onChildRemoved(DataSnapshot dataSnapshot) {
                            quests.clear();
                            arrayAdapter.notifyDataSetChanged();
                        }
                        @Override
                        public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                        }
                        @Override
                        public void onCancelled(DatabaseError e) {
                        }
                    });

                } else {
                    myRef.child("QuestList").orderByChild("ID").equalTo(referencedFriend).removeEventListener(latest);
                    myRef.child("QuestList").orderByChild("ID").equalTo(userID).addChildEventListener(latest = new ChildEventListener() {
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
                        }
                        @Override
                        public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                            quests.clear();
                            arrayAdapter.notifyDataSetChanged();
                            for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                if(!postSnapshot.getKey().equals("ID") & !postSnapshot.getKey().equals("Count") & postSnapshot.getValue() != null) {
                                    addToQuestList(postSnapshot.getValue().toString());
                                } else if (postSnapshot.getKey().equals("Count")){
                                    quest_count = postSnapshot.getValue().toString();
                                    String quest_count_header = "Quests: " + quest_count;
                                    header.setText(quest_count_header);
                                }
                            }
                        }
                        @Override
                        public void onChildRemoved(DataSnapshot dataSnapshot) {
                            quests.clear();
                            arrayAdapter.notifyDataSetChanged();
                        }
                        @Override
                        public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                        }
                        @Override
                        public void onCancelled(DatabaseError e) {
                        }
                    });
                    friend_activated = false;
                }
            }
        });


        //grab current user and ID for use of grabbing quest list
        mAuth = FirebaseAuth.getInstance();         //get authorization instance
        if(mAuth.getCurrentUser()!=null) {
            FirebaseUser current_user = mAuth.getCurrentUser();
            userID = current_user.getUid();
            //Toast.makeText(Home.this,userID,Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(Home.this,"Current user is null.",Toast.LENGTH_SHORT).show();
        }
        //grab quest list and create listener for value change
        myRef.child("QuestList").orderByChild("ID").equalTo(userID).addChildEventListener(latest = new ChildEventListener() {
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
            }
            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                quests.clear();
                arrayAdapter.notifyDataSetChanged();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    if(!postSnapshot.getKey().equals("ID") & !postSnapshot.getKey().equals("Count") & postSnapshot.getValue() != null) {
                        addToQuestList(postSnapshot.getValue().toString());
                    } else if (postSnapshot.getKey().equals("Count")){
                        quest_count = postSnapshot.getValue().toString();
                        String quest_count_header = "Quests: " + quest_count;
                        header.setText(quest_count_header);
                    }
                }
            }
            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                quests.clear();
                arrayAdapter.notifyDataSetChanged();
            }
            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }
            @Override
            public void onCancelled(DatabaseError e) {
            }
        });

    }

    public void onStart() {
        super.onStart();
    }

    public void onDestroy() {
        super.onDestroy();
        //sign out before leaving app
        mAuth.signOut();
        LoginManager.getInstance().logOut();
    }

    public void addToQuestList(String item) {
        quests.add(0,item);//adds quest at position zero in display
        arrayAdapter.notifyDataSetChanged();
    }

    public void removeFromQuestList(DatabaseReference ref, Integer item, Integer position) {
        ref.child(String.valueOf(item - (1 + position))).removeValue();//removes item in quests list at signified position
        arrayAdapter.notifyDataSetChanged();
    }

}
