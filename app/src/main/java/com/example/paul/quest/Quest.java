package com.example.paul.quest;


import com.google.firebase.database.DatabaseReference;

public class Quest {

    String name;
    boolean isDone;
    DatabaseReference parent_reference;

    Quest(String name, boolean isDone, DatabaseReference parent_reference) {
        this.name = name;
        this.isDone = isDone;
        this.parent_reference = parent_reference;
    }
}
