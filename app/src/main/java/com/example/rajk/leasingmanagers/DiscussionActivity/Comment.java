package com.example.rajk.leasingmanagers.DiscussionActivity;

import android.graphics.Color;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import com.example.rajk.leasingmanagers.R;

public class Comment extends AppCompatActivity {
    RelativeLayout.LayoutParams layoutParams_commentView,layoutParams_sendComment,layoutParams_typeComment,layoutParams_sendButton;
    private int height,width;
    private AutoCompleteTextView typeComment;
    private ImageButton sendButton;
    RelativeLayout commentView,sendComment;
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        height = displaymetrics.heightPixels;
        width = displaymetrics.widthPixels;

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

    }
}
