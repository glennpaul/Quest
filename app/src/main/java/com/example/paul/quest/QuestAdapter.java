package com.example.paul.quest;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.ArrayList;

public class QuestAdapter extends RecyclerView.Adapter<QuestAdapter.CustomViewHolder>{

    Context context;
    ArrayList<Quest> quests;

    public QuestAdapter(Context context, ArrayList<Quest> quests) {
        this.context = context;
        this.quests = quests;
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_view,parent,false);
        return new CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CustomViewHolder holder, int position) {
        Quest quest = quests.get(position);
        holder.text.setText(quest.name);
        holder.box.setChecked(quest.isDone);

    }

    @Override
    public int getItemCount() {
        return quests.size();
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {
        TextView text;
        CheckBox box;

        public  CustomViewHolder(View view) {
            super(view);
            text = view.findViewById(R.id.textView);
            box = view.findViewById(R.id.checkBox);
        }
    }
}



