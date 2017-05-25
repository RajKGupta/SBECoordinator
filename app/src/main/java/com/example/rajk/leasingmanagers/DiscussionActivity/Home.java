package com.example.rajk.leasingmanagers.DiscussionActivity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.rajk.leasingmanagers.NewTopic;
import com.example.rajk.leasingmanagers.R;
import com.example.rajk.leasingmanagers.adapter.commentAdapter;
import com.example.rajk.leasingmanagers.adapter.topicAdapter;
import com.example.rajk.leasingmanagers.listener.EmptyRecyclerView;
import com.example.rajk.leasingmanagers.model.CommentModel;
import com.example.rajk.leasingmanagers.model.Discussions;
import com.example.rajk.leasingmanagers.session;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Home extends AppCompatActivity
{

    RecyclerView Topic_list;
    DatabaseReference dbTopic;
    LinearLayoutManager linearLayoutManager;
    private ArrayList<Discussions>  TopicList= new ArrayList<>();
    private RecyclerView.Adapter mAdapter;
    session se ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topic);

        se = new session(getApplicationContext());
        Topic_list = (RecyclerView) findViewById(R.id.Topic_List);
        linearLayoutManager=new LinearLayoutManager(this);
        mAdapter = new topicAdapter(TopicList,this);
        Topic_list.setAdapter(mAdapter);

        dbTopic = FirebaseDatabase.getInstance().getReference().child("MeChat").child("Topic").child(se.getPlace_id()).getRef();

        LoadData();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Home.this,NewTopic.class));
            }
        });

    }

    void LoadData()
    {

        dbTopic.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s)
            {
                Discussions topic = new Discussions(se.getPlace_id(),dataSnapshot.getKey());
                TopicList.add(topic);
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

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
    }
}
