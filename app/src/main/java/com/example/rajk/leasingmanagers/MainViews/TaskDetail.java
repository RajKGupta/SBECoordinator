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
import android.widget.TextView;

import com.example.rajk.leasingmanagers.ForwardTask.forwardTask;
import com.example.rajk.leasingmanagers.R;
import com.example.rajk.leasingmanagers.adapter.assignedto_adapter;
import com.example.rajk.leasingmanagers.adapter.measurement_adapter;
import com.example.rajk.leasingmanagers.model.CompletedBy;
import com.example.rajk.leasingmanagers.model.Task;
import com.example.rajk.leasingmanagers.model.measurement;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class TaskDetail extends AppCompatActivity {

    private DatabaseReference dbRef, dbTask,dbCompleted,dbAssigned,dbMeasurement;
    private String task_id;
    private Task task;
    private String customername;
    EditText startDate,endDate,custId,taskName,quantity,description;
    RecyclerView rec_assignedto,rec_completedby,rec_measurement ;
    assignedto_adapter adapter_assignedto,adapter_completedby;
    List<CompletedBy> assignedtoList = new ArrayList<>();
    FloatingActionButton forward;
    List<CompletedBy> completedbyList = new ArrayList<>();
    ArrayList<measurement> measurementList = new ArrayList<>();
    measurement_adapter adapter_measurement;
    TextView open_assignedto,open_completedby,open_measurement;

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
        rec_measurement = (RecyclerView)findViewById(R.id.rec_measurement);
        open_assignedto = (TextView)findViewById(R.id.open_assignedto);
        open_completedby = (TextView)findViewById(R.id.open_completedby);
        open_measurement = (TextView)findViewById(R.id.open_measurement);

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

        rec_measurement.setLayoutManager(new LinearLayoutManager(this));
        rec_measurement.setItemAnimator(new DefaultItemAnimator());
        rec_measurement.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        adapter_measurement = new measurement_adapter(measurementList, this);
        rec_measurement.setAdapter(adapter_measurement);

        dbTask = dbRef.child("Task").child(task_id);

        dbCompleted = dbTask.child("CompletedBy").getRef();
        dbAssigned = dbTask.child("AssignedTo").getRef();
        dbMeasurement = dbTask.child("Measurement").getRef();

        prepareListData();

        open_measurement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if (rec_measurement.getVisibility()== View.GONE)
                {
                    rec_measurement.setVisibility(View.VISIBLE);
                }
                else
                {
                    rec_measurement.setVisibility(View.GONE);
                }
            }
        });


        open_completedby.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (rec_completedby.getVisibility()== View.GONE)
                {
                    rec_completedby.setVisibility(View.VISIBLE);
                }
                else
                {
                    rec_completedby.setVisibility(View.GONE);
                }            }
        });

        open_assignedto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (rec_assignedto.getVisibility()== View.GONE)
                {
                    rec_assignedto.setVisibility(View.VISIBLE);
                }
                else
                {
                    rec_assignedto.setVisibility(View.GONE);
                }
            }
        });
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

        dbMeasurement.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.exists())
                {
                    measurement item = dataSnapshot.getValue(measurement.class);
                    measurementList.add(item);
                    adapter_measurement.notifyDataSetChanged();
                }
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
