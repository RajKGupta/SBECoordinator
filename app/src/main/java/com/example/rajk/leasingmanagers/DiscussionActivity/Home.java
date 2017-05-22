package com.example.rajk.leasingmanagers.DiscussionActivity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.example.rajk.leasingmanagers.NewTopic;
import com.example.rajk.leasingmanagers.R;

public class Home extends AppCompatActivity
{
    String place ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topic);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Home.this,NewTopic.class));
            }
        });

        place = getIntent().getStringExtra("place_id");
        Toast.makeText(this, place, Toast.LENGTH_SHORT).show();
    }
}
