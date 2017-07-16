package com.example.rajk.leasingmanagers.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.rajk.leasingmanagers.R;
import com.example.rajk.leasingmanagers.employee.Employee;
import com.example.rajk.leasingmanagers.model.CompletedBy;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import static com.example.rajk.leasingmanagers.LeasingManagers.DBREF;

/**
 * Created by RajK on 16-05-2017.
 */

public class EmployeeTask_Adapter extends  RecyclerView.Adapter<EmployeeTask_Adapter.MyViewHolder>
{
    List<String> list = new ArrayList<>();
    private Context context;
    String empId;
    private EmployeeTask_AdapterListener listener;

    public EmployeeTask_Adapter(List<String> list, Context context, String empId,EmployeeTask_AdapterListener listener) {
        this.list = list;
        this.context = context;
        this.empId=empId;
        this.listener = listener;

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.employeetask_row,parent,false);
        return new MyViewHolder(view);

    }

    @Override
    public void onBindViewHolder(final EmployeeTask_Adapter.MyViewHolder holder, final int position)
    {
       // holder.button_rl.setVisibility(View.GONE);
        int p =position;
        holder.noteAuthor.setText("Coordinator's Note:");
        holder.tv_dateCompleted.setText("Deadline :");

        DatabaseReference refh = DBREF.child("Task").child(list.get(position)).child("AssignedTo").child(empId).getRef();

        refh.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {

                if (dataSnapshot.exists()) {
                    final CompletedBy emp = dataSnapshot.getValue(CompletedBy.class);

                    holder.dateassigned.setText(emp.getDateassigned());
                    holder.dateCompleted.setText(emp.getDatecompleted());
                    holder.noteString.setText(emp.getNote());

                    DatabaseReference dbEmp = DBREF.child("Task").child(list.get(position)).getRef();
                    dbEmp.addListenerForSingleValueEvent(
                            new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            String empname = dataSnapshot.child("name").getValue(String.class);
                            holder.employeename.setText(empname);

                            //String empdesig = dataSnapshot.child("designation").getValue(String.class);
                            holder.employeeDesig.setVisibility(View.GONE);
                            //holder.employeeDesig.setText(empdesig);

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        applyClickEvents(holder,position);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView dateCompleted,employeename,employeeDesig,dateassigned,tv_dateCompleted,noteAuthor,noteString;
        public ImageButton removeButton,remindButton,infoButton,dotmenu;
        public LinearLayout buttonshow;

        public MyViewHolder(View itemView) {
            super(itemView);

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

            removeButton = (ImageButton)itemView.findViewById(R.id.remove);
            remindButton = (ImageButton) itemView.findViewById(R.id.remind);
            infoButton = (ImageButton) itemView.findViewById(R.id.info);
            dotmenu = (ImageButton) itemView.findViewById(R.id.dotmenu);
            buttonshow = (LinearLayout)itemView.findViewById(R.id.buttonshow);
        }
    }

    public interface EmployeeTask_AdapterListener {
        void onEmployeeRemoveButtonClicked(int position);
        void onEmployeeRemindButtonClicked(int position);
        void onEmployeeInfoButtonClicked(int position);
        void onEmployeedotmenuButtonClicked(int position, MyViewHolder holder);
    }
    private void applyClickEvents(final MyViewHolder holder, final int position) {

        holder.removeButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                listener.onEmployeeRemoveButtonClicked(position);
            }
        });

        holder.remindButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onEmployeeRemindButtonClicked(position);
            }
        });

        holder.infoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onEmployeeInfoButtonClicked(position);
            }
        });

        holder.dotmenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onEmployeedotmenuButtonClicked(position,holder);
            }
        });
    }
}
