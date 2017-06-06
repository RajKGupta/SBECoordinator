package com.example.rajk.leasingmanagers.MainViews;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;

import com.example.rajk.leasingmanagers.ForwardTask.forwardTask;
import com.example.rajk.leasingmanagers.R;
import com.example.rajk.leasingmanagers.adapter.assignedto_adapter;
import com.example.rajk.leasingmanagers.model.CompletedBy;
import com.example.rajk.leasingmanagers.model.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class TaskDetail extends AppCompatActivity {

    private DatabaseReference dbRef, dbTask,dbCompleted,dbAssigned;
    private String task_id;
    private Task task;
    private String customername;
    EditText startDate,endDate,custId,taskName,quantity,description;
    RecyclerView rec_assignedto,rec_completedby ;
    assignedto_adapter adapter_assignedto,adapter_completedby;
    List<CompletedBy> assignedtoList = new ArrayList<>();
    FloatingActionButton forward;
    List<CompletedBy> completedbyList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_detail);
        dbRef = FirebaseDatabase.getInstance().getReference().child("MeChat");
        forward = (FloatingActionButton)findViewById(R.id.forward);
        taskName = (EditText) findViewById(R.id.taskName);
        startDate = (EditText) findViewById(R.id.startDate);
        endDate = (EditText) findViewById(R.id.endDate);
        quantity=(EditText) findViewById(R.id.quantity);
        description = (EditText) findViewById(R.id.description);
        custId = (EditText) findViewById(R.id.custId);
        rec_assignedto = (RecyclerView)findViewById(R.id.rec_assignedto);
        rec_completedby = (RecyclerView)findViewById(R.id.rec_completedby);

        Intent intent = getIntent();
        task_id = intent.getStringExtra("task_id");


        rec_assignedto.setLayoutManager(new LinearLayoutManager(this));
        rec_assignedto.setItemAnimator(new DefaultItemAnimator());
        rec_assignedto.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        adapter_assignedto = new assignedto_adapter(assignedtoList, this,"AssignedTo",task_id);
        rec_assignedto.setAdapter(adapter_assignedto);

        rec_completedby.setLayoutManager(new LinearLayoutManager(this));
        rec_completedby.setItemAnimator(new DefaultItemAnimator());
        rec_completedby.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        adapter_completedby = new assignedto_adapter(completedbyList, this,"CompletedBy",task_id);
        rec_completedby.setAdapter(adapter_completedby);

        dbTask = dbRef.child("Task").child(task_id);

        dbCompleted = dbTask.child("CompletedBy").getRef();
        dbAssigned = dbTask.child("AssignedTo").getRef();

        prepareListData();

        dbTask.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                task = dataSnapshot.getValue(Task.class);
                setValue(task);
                getSupportActionBar().setTitle(task.getName());
                DatabaseReference dbCustomerName = FirebaseDatabase.getInstance().getReference().child("MeChat").child("Customer").child(task.getCustomerId()).getRef();
                dbCustomerName.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        customername = dataSnapshot.child("name").getValue(String.class);
                        getSupportActionBar().setSubtitle(customername);
                        custId.setText(task.getCustomerId()+ ": "+customername);

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        forward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1  = new Intent(TaskDetail.this,forwardTask.class);
                intent1.putExtra("task_id",task_id);
                startActivity(intent1);
                finish();
            }
        });
    }

    private void prepareListData()
    {

        dbCompleted.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.exists()) {
                    CompletedBy item = dataSnapshot.getValue(CompletedBy.class);
                    completedbyList.add(item);
                    adapter_completedby.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        dbAssigned.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot.exists()) {
                    CompletedBy item = dataSnapshot.getValue(CompletedBy.class);
                    assignedtoList.add(item);
                    adapter_assignedto.notifyDataSetChanged();
                }

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                CompletedBy item = dataSnapshot.getValue(CompletedBy.class);
                assignedtoList.remove(item);
                adapter_assignedto.notifyDataSetChanged();

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    void setValue(Task task)
    {
        startDate.setText(task.getStartDate());
        endDate.setText(task.getExpEndDate());
        taskName.setText(task.getName());
        quantity.setText(task.getQty());
        description.setText(task.getDesc());
    }
}
