package com.example.rajk.leasingmanagers;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.rajk.leasingmanagers.DiscussionActivity.Home;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.sql.Time;
import java.util.Calendar;

public class NewTopic extends AppCompatActivity {
    Button create;
    DatabaseReference dbTopic = FirebaseDatabase.getInstance().getReference().child("MeChat").child("Topic").getRef();
    EditText newTopic;
    Vibrator vibrate;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_topic);
        create  = (Button) findViewById(R.id.create);
        newTopic = (EditText) findViewById(R.id.newtopic);
        vibrate = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String topicName = newTopic.getText().toString().trim();
                if(TextUtils.isEmpty(topicName))
                {
                    Toast.makeText(NewTopic.this,"Duh!! Give me a topic first",Toast.LENGTH_SHORT).show();
                    vibrate.vibrate(50);

                }
                else
                {
                    ProgressDialog progressDialog = new ProgressDialog(NewTopic.this);
                    progressDialog.setMessage("Creating New Topic");
                    progressDialog.show();
                    SharedPreferences sharedPreferences = getSharedPreferences("SESSION",MODE_PRIVATE);
                    DatabaseReference dbNewTopic=dbTopic.child(topicName);
                    dbNewTopic.child("place_id").setValue(sharedPreferences.getString("place_id","449"));
                    dbNewTopic.child("name").setValue(topicName);

                    progressDialog.dismiss();
                    startActivity(new Intent(NewTopic.this, Home.class));
                    finish();

                }
            }
        });
    }
}
