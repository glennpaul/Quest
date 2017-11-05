package com.example.paul.quest;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;

public class QuestAdapter extends RecyclerView.Adapter<QuestAdapter.CustomViewHolder>{

    private ArrayList<Quest> quests;

    QuestAdapter(ArrayList<Quest> quests) {
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

    class CustomViewHolder extends RecyclerView.ViewHolder {
        TextView text;
        CheckBox box;

        CustomViewHolder(View view) {
            super(view);
            text = view.findViewById(R.id.textView);
            box = view.findViewById(R.id.checkBox);
            view.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    DatabaseReference reference = quests.get(getAdapterPosition()).parent_reference;
                    Toast.makeText(view.getContext(),"LONG CLICK",Toast.LENGTH_SHORT).show();
                    Home.removeFromQuestList(reference.child("QuestList").child("1"),getAdapterPosition());
                    notifyDataSetChanged();
                    return false;
                }
            });
        }
    }
}



