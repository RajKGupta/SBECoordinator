package com.example.rajk.leasingmanagers.DiscussionActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.CalendarView;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.rajk.leasingmanagers.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Comment extends AppCompatActivity {
    RelativeLayout.LayoutParams layoutParams_commentView,layoutParams_sendComment,layoutParams_typeComment,layoutParams_sendButton;
    private int height,width;
    private AutoCompleteTextView typeComment;
    private ImageButton sendButton;
    RelativeLayout commentView,sendComment;
    Intent intent;
    DatabaseReference dbTopic;
    String topic_id;
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        height = displaymetrics.heightPixels;
        width = displaymetrics.widthPixels;
        intent = getIntent();
        topic_id = intent.getStringExtra("topic_id");
        dbTopic = FirebaseDatabase.getInstance().getReference().child("Topic").child(topic_id).getRef();
        commentView = (RelativeLayout)findViewById(R.id.commentView);
        layoutParams_commentView = new RelativeLayout.LayoutParams(width,height*75/100);
        layoutParams_commentView.topMargin = height*1/100;
        layoutParams_commentView.leftMargin = width*1/100;
        layoutParams_commentView.rightMargin = width*1/100;
        commentView.setLayoutParams(layoutParams_commentView);


        sendComment = (RelativeLayout)findViewById(R.id.sendComment);
        layoutParams_sendComment = new RelativeLayout.LayoutParams(width,height*20/100);
        layoutParams_sendComment.topMargin = height*78/100;
        layoutParams_sendComment.leftMargin = width*0/100;
        sendComment.setBackgroundColor(Color.WHITE);
        sendComment.setLayoutParams(layoutParams_sendComment);

        typeComment = (AutoCompleteTextView)findViewById(R.id.typeComment);
        layoutParams_typeComment = new RelativeLayout.LayoutParams(width*78/100,height*16/100);
        layoutParams_typeComment.topMargin = height*1/100;
        layoutParams_typeComment.leftMargin = width*2/100;
        typeComment.setLayoutParams(layoutParams_typeComment);


        sendButton = (ImageButton)findViewById(R.id.sendButton);
        layoutParams_sendButton = new RelativeLayout.LayoutParams(width*16/100,height*16/100);
        layoutParams_sendButton.topMargin = height*1/100;
        layoutParams_sendButton.leftMargin = width*80/100;
        sendButton.setElevation(4.0F);
        sendButton.setLayoutParams(layoutParams_sendButton);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String commentString = typeComment.getText().toString().trim();
                if(TextUtils.isEmpty(commentString))
                {
                    Toast.makeText(Comment.this,"What?? No Comment!!",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    ProgressDialog progressDialog=new ProgressDialog(Comment.this);
                    progressDialog.setMessage("Posting Comment...");
                    progressDialog.show();
                    long time = Calendar.getInstance().getTimeInMillis();
                    DatabaseReference dbNewComment = dbTopic.child(String.valueOf(time));
                    SharedPreferences sharedPreferences = getSharedPreferences("SESSION",MODE_PRIVATE);
                    SimpleDateFormat curFormater = new SimpleDateFormat("dd/MM/yyyy hh:mm");
                    String timestamp = curFormater.format(Calendar.getInstance().getTime());
                    String sender = sharedPreferences.getString("username","username");
                    dbNewComment.child("Sender").setValue(sender);
                    dbNewComment.child("Timestamp").setValue(timestamp);
                    dbNewComment.child("commentString").setValue(commentString);
                    progressDialog.dismiss();
                }
            }
        });

    }
}
