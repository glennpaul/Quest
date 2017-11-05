package com.example.paul.quest;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;

public class Friends extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.friends);

        RecyclerView rv = findViewById(R.id.rv);
        rv.setHasFixedSize(true);

        LinearLayoutManager llm = new LinearLayoutManager(this);
        rv.setLayoutManager(llm);

        ArrayList<Quest> quests = new ArrayList<>();

        Quest drink_water = new Quest("Drink some water", true);
        quests.add(drink_water);
        Quest eat = new Quest("Eat something.", true);
        quests.add(eat);
        Quest sleep = new Quest("Sleep", false);
        quests.add(sleep);

        QuestAdapter questAdapter = new QuestAdapter(getApplicationContext(),quests);
        rv.setAdapter(questAdapter);

    }

}
