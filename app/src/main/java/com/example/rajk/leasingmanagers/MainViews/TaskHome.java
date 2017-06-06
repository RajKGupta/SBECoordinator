package com.example.rajk.leasingmanagers.MainViews;

import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.rajk.leasingmanagers.DiscussionActivity.Comment;
import com.example.rajk.leasingmanagers.MainActivity;
import com.example.rajk.leasingmanagers.NewTopic;
import com.example.rajk.leasingmanagers.R;
import com.example.rajk.leasingmanagers.adapter.taskAdapter;
import com.example.rajk.leasingmanagers.model.Task;
import com.example.rajk.leasingmanagers.session;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class TaskHome extends AppCompatActivity implements taskAdapter.TaskAdapterListener{
    RecyclerView task_list;
    DatabaseReference dbTask;
    LinearLayoutManager linearLayoutManager;
    private ArrayList<Task> TaskList= new ArrayList<>();
    private RecyclerView.Adapter mAdapter;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_task);

        task_list = (RecyclerView) findViewById(R.id.task_list);
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Task");

        dbTask = FirebaseDatabase.getInstance().getReference().child("MeChat").child("Task").getRef();
        LoadData();
        mAdapter = new taskAdapter(TaskList,this,this);
        linearLayoutManager=new LinearLayoutManager(this);
        task_list.setLayoutManager(linearLayoutManager);
        task_list.setItemAnimator(new DefaultItemAnimator());
        task_list.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));

        task_list.setAdapter(mAdapter);

      /*  FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(TaskHome.this,NewTopic.class));
            }
        });*/

    }
/*
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.signout:
                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                i.putExtra("SIGN_OUT","SIGN_OUT");
                startActivity(i);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }*/

    void LoadData()
    {

        dbTask.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s)
            {
                Task task = dataSnapshot.getValue(Task.class);
                TaskList.add(task);
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Task task = dataSnapshot.getValue(Task.class);
                TaskList.remove(task);
                mAdapter.notifyDataSetChanged();
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
    public void onTaskRowClicked(int position) {
        Intent intent = new Intent(TaskHome.this,TaskDetail.class);
        Task task = TaskList.get(position);
        intent.putExtra("task_id",task.getTaskId());
        startActivity(intent);

    }
}
