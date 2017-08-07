package com.example.rajk.leasingmanagers.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.rajk.leasingmanagers.R;
import com.example.rajk.leasingmanagers.model.CompletedJob;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import static com.example.rajk.leasingmanagers.LeasingManagers.DBREF;

public class completedBy_adapter extends  RecyclerView.Adapter<completedBy_adapter.MyViewHolder>
{
    List<CompletedJob> list = new ArrayList<>();
    private Context context;
    SharedPreferences sharedPreferences ;
    String taskId;
    public CompletedJob emp = new CompletedJob();

    public completedBy_adapter(List<CompletedJob> list, Context context, String taskId) {
        this.list = list;
        this.context = context;
        sharedPreferences = context.getSharedPreferences("SESSION",Context.MODE_PRIVATE);
        this.taskId=taskId;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.completedby_listrow,parent,false);
        return new MyViewHolder(view);

    }

    @Override
    public void onBindViewHolder(final completedBy_adapter.MyViewHolder holder, final int position)
    {
        emp = list.get(position);

        DatabaseReference dbEmp = DBREF.child("Employee").child(emp.getEmpId()).getRef();
        dbEmp.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                holder.dateassigned.setText(emp.getDateassigned());
                holder.dateCompleted.setText(emp.getDatecompleted());
                holder.noteString.setText(emp.getCoordinatorNote());
                holder.assignedby.setText(emp.getAssignedByName());
                holder.employeeNote.setText(emp.getEmpployeeNote());
                String empname = dataSnapshot.child("name").getValue(String.class);
                holder.employeename.setText(empname);
                String empdesig = dataSnapshot.child("designation").getValue(String.class);
                holder.employeeDesig.setText(empdesig);


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

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView dateCompleted,employeename,employeeDesig,dateassigned,tv_dateCompleted,noteAuthor,noteString,assignedby,employeeNote;

        public MyViewHolder(View itemView) {
            super(itemView);
            employeeNote = (TextView)itemView.findViewById(R.id.employeeNote);
            dateCompleted = (TextView) itemView.findViewById(R.id.dateCompleted);

            employeename = (TextView)
                    itemView.findViewById(R.id.employeeName);


            employeeDesig = (TextView)
                    itemView.findViewById(R.id.employeeDesig);

            dateassigned = (TextView)
                    itemView.findViewById(R.id.dateAssign);


            tv_dateCompleted = (TextView)
                    itemView.findViewById(R.id.tv_datecompleted);

            noteAuthor = (TextView)itemView.findViewById(R.id.noteAuthor);
            noteString = (TextView) itemView.findViewById(R.id.noteString);
            assignedby = (TextView)itemView.findViewById(R.id.assignedBy);
        }
    }

}
