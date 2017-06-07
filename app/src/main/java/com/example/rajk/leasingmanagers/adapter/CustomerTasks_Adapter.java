package com.example.rajk.leasingmanagers.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.rajk.leasingmanagers.MainViews.TaskDetail;
import com.example.rajk.leasingmanagers.R;
import com.example.rajk.leasingmanagers.model.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by RajK on 16-05-2017.
 */

public class CustomerTasks_Adapter extends  RecyclerView.Adapter<CustomerTasks_Adapter.MyViewHolder>
{
    List<String> list = new ArrayList<>();
    private Context context;

    public CustomerTasks_Adapter(List<String> list, Context c)
    {
        this.list = list;
        this.context = c;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView taskname,timestamp,icon_text;
        ImageView imgProfile;
        Button viewdetail;

        public MyViewHolder(View itemView) {
            super(itemView);
            taskname = (TextView) itemView.findViewById(R.id.tv_taskname);
            timestamp = (TextView) itemView.findViewById(R.id.timestamp);
            icon_text =(TextView)itemView.findViewById(R.id.icon_text);
            imgProfile = (ImageView)itemView.findViewById(R.id.icon_profile);
            viewdetail = (Button)itemView.findViewById(R.id.viewdetails);
        }

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.customertasks_row,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final CustomerTasks_Adapter.MyViewHolder holder, final int position) {
        DatabaseReference refh = FirebaseDatabase.getInstance().getReference().child("MeChat").child("Task").child(list.get(position)).getRef();

        refh.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                    Task task = dataSnapshot.getValue(Task.class);
                    holder.taskname.setText(task.getName());
                    String iconText = task.getName().toUpperCase();
                    holder.icon_text.setText(iconText.charAt(0) + "");
                    holder.imgProfile.setImageResource(R.drawable.bg_circle);
                    holder.imgProfile.setColorFilter(task.getColor());
                    holder.timestamp.setText(task.getStartDate());

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        holder.viewdetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context,TaskDetail.class);
                intent.putExtra("task_id",list.get(position));
                context.startActivity(intent);
            }
        });
    }
    @Override
    public int getItemCount() {
        return list.size();
    }

}