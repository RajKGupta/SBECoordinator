package com.example.rajk.leasingmanagers.DiscussionActivity;

import android.content.Intent;
<<<<<<< Updated upstream
=======
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.Color;
>>>>>>> Stashed changes
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.rajk.leasingmanagers.MainActivity;
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
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topic);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Topics");

        se = new session(getApplicationContext());
        Topic_list = (RecyclerView) findViewById(R.id.Topic_List);
        linearLayoutManager=new LinearLayoutManager(this);
        mAdapter = new topicAdapter(TopicList,this);
        Topic_list.setAdapter(mAdapter);
        dbTopic = FirebaseDatabase.getInstance().getReference().child("MeChat").child("Topic").getRef();

        LoadData();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Home.this,NewTopic.class));
                finish();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId())
        {
            case R.id.signout:
                se.clearoldusersession();
                //se.create_signedin("false");
                SharedPreferences sharedPreferences = getSharedPreferences("SIGNEDOUT",MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("signedout","signedout");
                editor.commit();
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
        }
        return true;
    }

    void LoadData()
    {
        dbTopic.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(!dataSnapshot.hasChildren()){
                            Toast.makeText(Comment.this, "No more comments", Toast.LENGTH_SHORT).show();
                            currentPage--;
                        }
                        for (DataSnapshot data : dataSnapshot.getChildren()) {
                            CommentModel comment = data.getValue(CommentModel.class);
                            commentList.add(comment);
                            mAdapter.notifyDataSetChanged();
                        }
                    }

                    @Override public void onCancelled(DatabaseError databaseError) {}});

        dbTopic.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s)
            {
                Discussions topic = new Discussions(se.getPlace_id(),dataSnapshot.getKey());
                topic.setColor(getRandomMaterialColor("400"));
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
    private int getRandomMaterialColor(String typeColor) {
        int returnColor = Color.GRAY;
        int arrayId = getResources().getIdentifier("mdcolor_" + typeColor, "array", getPackageName());

        if (arrayId != 0) {
            TypedArray colors = getResources().obtainTypedArray(arrayId);
            int index = (int) (Math.random() * colors.length());
            returnColor = colors.getColor(index, Color.GRAY);
            colors.recycle();
        }
        return returnColor;
    }

    @Override
    public void onTopicRowClicked(int position) {
        Intent intent = new Intent(Home.this,Comment.class);
        Discussions topic = TopicList.get(position);
        intent.putExtra("topic_id",topic.getName());
        startActivity(intent);
        finish();
    }
}
