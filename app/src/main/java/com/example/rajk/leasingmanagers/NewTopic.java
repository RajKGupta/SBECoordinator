package com.example.rajk.leasingmanagers;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.rajk.leasingmanagers.DiscussionActivity.Home;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class NewTopic extends AppCompatActivity {
    Button create;
    DatabaseReference dbTopic = FirebaseDatabase.getInstance().getReference().child("MeChat").child("Topic");
    EditText newTopic;
    Vibrator vibrate;
    session s ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_topic);
        create  = (Button) findViewById(R.id.create);
        newTopic = (EditText) findViewById(R.id.newtopic);
        vibrate = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
        s = new session(getApplicationContext());

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

                    Toast.makeText(NewTopic.this, s.getPlace_id() + "  " + s.getUsername() + "  " + s.place(), Toast.LENGTH_SHORT).show();
                    DatabaseReference dbNewTopic=dbTopic.child(s.getPlace_id()).child(topicName);
                    dbNewTopic.child("name").setValue(topicName);

                    progressDialog.dismiss();
                    startActivity(new Intent(NewTopic.this, Home.class));
                    finish();

                }
            }
        });
    }
}
