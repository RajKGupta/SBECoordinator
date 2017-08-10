package com.example.rajk.leasingmanagers.employee;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.rajk.leasingmanagers.R;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import static com.example.rajk.leasingmanagers.LeasingManagers.DBREF;

public class RecAdapter_emp extends RecyclerView.Adapter<RecAdapter_emp.RecHolder>{

    public List<Employee> list;
    Context context;

    public RecAdapter_emp(List<Employee> list, Context c){
        this.list = list;
        this.context = c;
    }

    @Override
    public RecHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.employee_row,parent,false);
        return new RecHolder(view);
    }

    @Override
    public void onBindViewHolder(final RecHolder holder, int position) {

        Employee item = list.get(position);
        holder.name.setText(item.getName());
        holder.desig.setText(item.getDesignation());
        String iconText = item.getName().toUpperCase();
        holder.icon_text.setText(iconText.charAt(0)+"");
        holder.imgProfile.setImageResource(R.drawable.bg_circle);
        holder.imgProfile.setColorFilter(item.getColor());
        DatabaseReference dbAssignedTask = DBREF.child("Employee").child(item.getUsername()).child("AssignedTask").getRef();
        final Integer pendingJobs[] = {0};
        dbAssignedTask.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                pendingJobs[0]++;
                holder.pendingTasks.setText(String.valueOf(pendingJobs[0]));

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                pendingJobs[0]--;
                holder.pendingTasks.setText(String.valueOf(pendingJobs[0]));
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    public void setItem(Employee item , int p){
        this.list.set(p,item);
    }

    public class RecHolder extends RecyclerView.ViewHolder{

        TextView name,desig,icon_text,pendingTasks;
        ImageView imgProfile;

        public RecHolder(View itemView) {
            super(itemView);

            name = (TextView) itemView.findViewById(R.id.name);
            desig = (TextView) itemView.findViewById(R.id.desig);
            icon_text =(TextView)itemView.findViewById(R.id.icon_text);
            imgProfile = (ImageView)itemView.findViewById(R.id.icon_profile);
            pendingTasks = (TextView)itemView.findViewById(R.id.tv_pendingTasks);
        }
    }
}
