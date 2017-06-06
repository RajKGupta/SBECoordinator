package com.example.rajk.leasingmanagers.MainViews;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ExpandableListView;
import com.example.rajk.leasingmanagers.R;
import com.example.rajk.leasingmanagers.adapter.completedByAdapter;
import com.example.rajk.leasingmanagers.model.CompletedBy;
import com.example.rajk.leasingmanagers.model.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TaskDetail extends AppCompatActivity {

    private DatabaseReference dbRef, dbTask,dbCompleted,dbAssigned;
    private String task_id;
    private Task task;
    private String customername;
    completedByAdapter listAdapter;
    ExpandableListView expListView;
    List<String> listDataHeader;
    HashMap<String, List<CompletedBy>> listDataChild;
    private List<CompletedBy>  completedByList = new ArrayList<CompletedBy>();
    private List<CompletedBy>  assignedToList = new ArrayList<CompletedBy>();
    EditText startDate,endDate,custId,taskName,quantity,description;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_detail);
        dbRef = FirebaseDatabase.getInstance().getReference().child("MeChat").getRef();

        taskName = (EditText) findViewById(R.id.taskName);
        startDate = (EditText) findViewById(R.id.startDate);
        endDate = (EditText) findViewById(R.id.endDate);
        quantity=(EditText) findViewById(R.id.quantity);
        description = (EditText) findViewById(R.id.description);
        custId = (EditText) findViewById(R.id.custId);

        Intent intent = getIntent();
        task_id = intent.getStringExtra("task_id");

        expListView = (ExpandableListView) findViewById(R.id.lvExp);

        prepareListData();

        listAdapter = new completedByAdapter(this, listDataHeader, listDataChild,task_id);
        expListView.setAdapter(listAdapter);
        dbTask = dbRef.child("Task").child(task_id).getRef();

        dbCompleted = dbTask.child("CompletedBy").getRef();
        dbAssigned = dbTask.child("AssignedTo").getRef();

        dbTask.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                task = dataSnapshot.getValue(Task.class);
                setValue(task);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                task = dataSnapshot.getValue(Task.class);
                setValue(task);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        getSupportActionBar().setTitle(task.getName());
        DatabaseReference dbCustomerName = FirebaseDatabase.getInstance().getReference().child("MeChat").child("Customer").child(task.getCustomerId()).getRef();
        dbCustomerName.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                customername = dataSnapshot.child("name").getValue(String.class);
                getSupportActionBar().setSubtitle(customername);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void prepareListData() {
        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, List<CompletedBy>>();

        // Adding child data
        listDataHeader.add("Completed By");
        listDataHeader.add("Assigned To");


        dbCompleted.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                CompletedBy item = dataSnapshot.getValue(CompletedBy.class);
                completedByList.add(item);
                listAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        dbAssigned.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                CompletedBy item = dataSnapshot.getValue(CompletedBy.class);
                assignedToList.add(item);
                listAdapter.notifyDataSetChanged();


            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                CompletedBy item = dataSnapshot.getValue(CompletedBy.class);
                assignedToList.remove(item);
                listAdapter.notifyDataSetChanged();

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        listDataChild.put(listDataHeader.get(0), completedByList); // Header, Child data
        listDataChild.put(listDataHeader.get(1), assignedToList);
    }

    void setValue(Task task)
    {
        custId.setText(task.getCustomerId()+ ": "+customername);
        startDate.setText(task.getStartDate());
        endDate.setText(task.getExpEndDate());
        taskName.setText(task.getName());
        quantity.setText(task.getQty());
        description.setText(task.getDesc());
    }
}
