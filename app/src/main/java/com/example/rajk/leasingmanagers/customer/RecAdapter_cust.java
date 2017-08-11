package com.example.rajk.leasingmanagers.customer;

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

import java.util.ArrayList;
import java.util.List;
import static com.example.rajk.leasingmanagers.LeasingManagers.DBREF;

public class RecAdapter_cust extends RecyclerView.Adapter<RecAdapter_cust.RecHolder>{

    public List<Customer> list;
    public List<Customer> filterlist;
    Context context;

    RecAdapter_cust(List<Customer> list ,Context c){
        this.list = list;
        this.context = c;
    }

    @Override
    public RecHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cust_row,parent,false);
        return new RecHolder(view);
    }

    @Override
    public void onBindViewHolder(final RecHolder holder, int position) {

        final Customer item = list.get(position);
        holder.name.setText(item.getName());
        String iconText = item.getName().toUpperCase();
        holder.icon_text.setText(iconText.charAt(0) + "");
        holder.imgProfile.setImageResource(R.drawable.bg_circle);
        holder.imgProfile.setColorFilter(item.getColor());
        DatabaseReference dbTask = DBREF.child("Customer").child(item.getId()).child("Task").getRef();
        final Integer pendingJobs[] = {0};
        dbTask.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                if(dataSnapshot.exists()) {
                        String status = dataSnapshot.getValue(String.class);
                        if (status.equals("pending")) {
                            pendingJobs[0]++;
                            holder.pendingJobCount.setText(String.valueOf(pendingJobs[0]));
                            DBREF.child("Customer").child(item.getId()).child("pendingTask").setValue(1000-pendingJobs[0]);
                    }
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                if(dataSnapshot.exists()) {
                    String status = dataSnapshot.getValue(String.class);
                    if (status.equals("pending")) {
                        pendingJobs[0]++;
                        holder.pendingJobCount.setText(String.valueOf(pendingJobs[0]));
                        DBREF.child("Customer").child(item.getId()).child("pendingTask").setValue(1000-pendingJobs[0]);
                    }
                    else
                    {
                        pendingJobs[0]--;
                        holder.pendingJobCount.setText(String.valueOf(pendingJobs[0]));
                        DBREF.child("Customer").child(item.getId()).child("pendingTask").setValue(1000-pendingJobs[0]);
                    }
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    String status = dataSnapshot.getValue(String.class);
                    if (status.equals("pending")) {
                        pendingJobs[0]--;
                        holder.pendingJobCount.setText(String.valueOf(pendingJobs[0]));
                        DBREF.child("Customer").child(item.getId()).child("pendingTask").setValue(1000-pendingJobs[0]);
                    }
                }
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public void setFilter(List<Customer> countryModels) {
        filterlist = new ArrayList<>();
        filterlist.addAll(countryModels);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void setItem(Customer item , int p){
        this.list.set(p,item);
    }

    public class RecHolder extends RecyclerView.ViewHolder{

        TextView name,icon_text,pendingJobCount;
        ImageView imgProfile;

        public RecHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.name);
            icon_text =(TextView)itemView.findViewById(R.id.icon_text);
            imgProfile = (ImageView)itemView.findViewById(R.id.icon_profile);
            pendingJobCount=(TextView)itemView.findViewById(R.id.pendingTasks);
        }
    }
    
}
