package com.example.paul.quest;

import android.content.Intent;
import android.graphics.Canvas;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
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

import static android.widget.Toast.LENGTH_SHORT;
import static java.sql.Types.NULL;


public class Home extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private String userID;
    private TextView header;
    private EditText newQuestText, etFriendName;
    private DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();
    private String questListID,quest_count;
    private String referencedFriend;
    private boolean friend_activated;

    private ChildEventListener latest;
    private ArrayList<Quest> quests;
    private QuestAdapter questAdapter;

    int fromPosition=-1;
    int toPosition=-1;
    String oldValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //setup layout with widgets
        setContentView(R.layout.home);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        header = findViewById(R.id.Quest_header);
        Button sign_out = findViewById(R.id.sign_out);
        Button add_quest_btn = findViewById(R.id.addQuestbtn);
        Button friend_quests_btn = findViewById(R.id.friendQuestsBtn);
        Button fbtn = findViewById(R.id.profile);
        newQuestText = findViewById(R.id.etAddQuest);
        etFriendName = findViewById(R.id.friend_prompt);
        friend_activated = false;

        //setup recycler view and adaptor
        RecyclerView rv = findViewById(R.id.rv);
        rv.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        rv.setLayoutManager(llm);
        quests = new ArrayList<>();
        questAdapter = new QuestAdapter(quests);
        rv.setAdapter(questAdapter);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(rv);

        //grab current user and ID for use of grabbing quest list
        mAuth = FirebaseAuth.getInstance();
        if(mAuth.getCurrentUser()!=null) {
            FirebaseUser current_user = mAuth.getCurrentUser();
            userID = current_user.getUid();
            //Toast.makeText(Home.this,userID,Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(Home.this,"Current user is null.", LENGTH_SHORT).show();
        }

        //grab quest list and create listener for value change
        setListener(userID);

        //add listener for friend quest list button, if clicked it pulls up list fo quests for specified friend
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

        //add listener for sign out button, if clicked, sign out of account and go back to login screen
        sign_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sign_out();
                finish();
                startActivity(new Intent(getApplicationContext(),MainActivity.class));
            }
        });

        //add listener for add_quest_btn, if clicked it adds the specified quest in the edit text to the current quest list
        add_quest_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                quest_count = String.valueOf(Integer.parseInt(quest_count)+1);
                myRef.child("QuestList").child(questListID).child(quest_count).setValue(newQuestText.getText().toString());
                myRef.child("QuestList").child(questListID).child("Count").setValue(quest_count);
                addToQuestList(newQuestText.getText().toString(),NULL);
                String quest_count_header = "Quests: " + quest_count;
                header.setText(quest_count_header);
            }
        });

        //listener for sign out button, if clicked, sign out of account and go back to login screen
        fbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),Friends.class));
            }
        });
    }

    public void addToQuestList(String item, int position) {
        if (position==NULL) {
            quests.add(new Quest(item, false,myRef));//adds quest to end of list
        } else {
            quests.add(position,new Quest(item,false,myRef));
        }
        questAdapter.notifyDataSetChanged();
    }

    public void removeFromQuestList(DatabaseReference ref, final Integer position) {
        quest_count = String.valueOf(Integer.parseInt(quest_count)-1);
        ref.child("QuestList").child(questListID).child(String.valueOf(position)).removeValue();//removes item in quests list at signified position
        quests.remove(position-1);
        String quest_count_header = "Quests: " + quest_count;
        header.setText(quest_count_header);
        questAdapter.notifyDataSetChanged();
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
                        addToQuestList(postSnapshot.getValue().toString(),NULL);
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
            }
            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Toast.makeText(Home.this,"REMOVED",Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                Toast.makeText(Home.this,"MOVED",Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onCancelled(DatabaseError e) {
                Toast.makeText(Home.this,"ERROR",Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void moveItem(int oldPos, int newPos){
        if (fromPosition==-1) {
            fromPosition = oldPos;
            toPosition = oldPos;
            oldValue = quests.get(oldPos).name;
        }
        if (newPos>oldPos) {
            toPosition++;
        } else {
            toPosition--;
        }
        Quest selected = quests.get(oldPos);
        quests.remove(oldPos);
        quests.add(newPos, selected);
        questAdapter.notifyItemMoved(oldPos, newPos);
    }

    public void sign_out() {
        //sign out before leaving app
        myRef.child("QuestList").orderByChild("ID").equalTo(userID).removeEventListener(latest);
        mAuth.signOut();
        LoginManager.getInstance().logOut();
    }

    public void onDestroy() {
        super.onDestroy();
        //sign_out();
    }

    //ItemTouchHelper to deal with RecyclerView Gestures
    ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
                boolean slid;//boolean to tell onMoved function if movement trigger was a slide or drag
                @Override
                public boolean onMove(RecyclerView recyclerView, final RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                    moveItem(viewHolder.getAdapterPosition(),target.getAdapterPosition());
                    slid=false;
                    return true;
                }
                @Override
                public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                    removeFromQuestList(myRef,viewHolder.getAdapterPosition()+1);
                    slid = true;
                }
                @Override
                public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                    super.clearView(recyclerView, viewHolder);
                    if (!slid) {
                        if (fromPosition!=-1) {
                            if (fromPosition == toPosition) {
                                fromPosition = -1;
                                toPosition = -1;
                                return;
                            }
                            fromPosition++;
                            toPosition++;
                            int i = fromPosition;
                            if (fromPosition > toPosition) {
                                while (i >= toPosition) {
                                    myRef.child("QuestList").child(questListID).child(String.valueOf(i)).setValue(quests.get(i-1).name);
                                    i--;
                                }
                            } else {
                                while (i <= toPosition) {
                                    myRef.child("QuestList").child(questListID).child(String.valueOf(i)).setValue(quests.get(i-1).name);
                                    i++;
                                }
                            }
                            fromPosition = -1;
                            questAdapter.notifyDataSetChanged();
                        }
                    }
                }
                @Override
                public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive)
                {
                    float topY = viewHolder.itemView.getTop() + dY;
                    float bottomY = topY + viewHolder.itemView.getHeight();
                    if (topY < 0)
                    {
                        dY = 0;
                    }
                    else if (bottomY > recyclerView.getHeight())
                    {
                        dY = recyclerView.getHeight() - viewHolder.itemView.getHeight() - viewHolder.itemView.getTop();
                    }
                    super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                }
            };

}
