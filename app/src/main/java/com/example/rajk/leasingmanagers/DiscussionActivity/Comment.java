package com.example.rajk.leasingmanagers.DiscussionActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.CalendarView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.rajk.leasingmanagers.R;
import com.example.rajk.leasingmanagers.adapter.commentAdapter;
import com.example.rajk.leasingmanagers.listener.EmptyRecyclerView;
import com.example.rajk.leasingmanagers.listener.EndlessRecyclerOnScrollListener;
import com.example.rajk.leasingmanagers.model.CommentModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class Comment extends AppCompatActivity {
    private static final int TOTAL_ITEM_EACH_LOAD = 20;
    // RelativeLayout.LayoutParams layoutParams_commentView,layoutParams_sendComment,layoutParams_typeComment,layoutParams_sendButton;
    private int height, width;
    private AutoCompleteTextView typeComment;
    private ImageButton sendButton;
    RelativeLayout commentView, sendComment;
    Intent intent;
    DatabaseReference dbTopic;
    String topic_id;
    private EmptyRecyclerView recyclerView;
    private ProgressBar progressBar;
    LinearLayoutManager linearLayoutManager;
    private int mRecyclerViewPosition = 0;
    private boolean endReached = false;
    private boolean requestInProgress = false;
    private int currentPage = 0;
    private long currentPageNumber = 1;// pageNumber starts from 1
    LinearLayout emptyView;
    SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy hh:mm");
    private String lastDate = "20-01-3000 00:00";
    private RecyclerView.Adapter mAdapter;
    private ArrayList<CommentModel> commentList= new ArrayList<>();
    private SharedPreferences sharedPreferences;

    String place_id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        height = displaymetrics.heightPixels;
        width = displaymetrics.widthPixels;

        sharedPreferences=getSharedPreferences("SESSION",MODE_PRIVATE);

        place_id=sharedPreferences.getString("place_id","449");
        intent = getIntent();
        topic_id = intent.getStringExtra("topic_id");
        dbTopic = FirebaseDatabase.getInstance().getReference().child(place_id).child("Topic").child(topic_id).child("Comment").getRef();


        recyclerView = (EmptyRecyclerView) findViewById(R.id.my_recycler_view);
        emptyView = (LinearLayout) findViewById(R.id.empty_view);
        recyclerView.setEmptyView(emptyView);
        progressBar = (ProgressBar) findViewById(R.id.progress);

        typeComment = (AutoCompleteTextView) findViewById(R.id.typeComment);
        sendButton = (ImageButton) findViewById(R.id.sendButton);

        linearLayoutManager=new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        mAdapter = new commentAdapter(commentList,this);
        recyclerView.setAdapter(mAdapter);
        recyclerView.setOnScrollListener(new EndlessRecyclerOnScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int current_page) { // when we have reached end of RecyclerView this event fired
                loadMoreData();
            }
        });
        loadData(); // load data here for first time launch app


/*

 }layoutParams_commentView = new RelativeLayout.LayoutParams(width,height*75/100);
                        });layoutParams_commentView.topMargin = height*1/100;
                        loadFollowedDataFirst(postPaths);layoutParams_commentView.leftMargin = width*1/100;
        layoutParams_commentView.rightMargin = width*1/100;
        commentView.setLayoutParams(layoutParams_commentView);


        sendComment = (RelativeLayout)findViewById(R.id.sendComment);
        layoutParams_sendComment = new RelativeLayout.LayoutParams(width,height*20/100);
        layoutParams_sendComment.topMargin = height*78/100;
        layoutParams_sendComment.leftMargin = width*0/100;
        sendComment.setBackgroundColor(Color.WHITE);
        sendComment.setLayoutParams(layoutParams_sendComment);

        layoutParams_typeComment = new RelativeLayout.LayoutParams(width*78/100,height*16/100);
        layoutParams_typeComment.topMargin = height*1/100;
        layoutParams_typeComment.leftMargin = width*2/100;
        typeComment.setLayoutParams(layoutParams_typeComment);


         layoutParams_sendButton = new RelativeLayout.LayoutParams(width*16/100,height*16/100);
        layoutParams_sendButton.topMargin = height*1/100;
        layoutParams_sendButton.leftMargin = width*80/100;
        sendButton.setElevation(4.0F);
        sendButton.setLayoutParams(layoutParams_sendButton);
    */
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String commentString = typeComment.getText().toString().trim();
                if (TextUtils.isEmpty(commentString)) {
                    Toast.makeText(Comment.this, "What?? No Comment!!", Toast.LENGTH_SHORT).show();
                } else {
                    try {

                        ProgressDialog progressDialog = new ProgressDialog(Comment.this);
                        progressDialog.setMessage("Posting Comment...");
                        progressDialog.show();

                        Date lastdate = formatter.parse(lastDate);
                        long ldInSec = lastdate.getTime();
                        long curTime = Calendar.getInstance().getTimeInMillis();
                        long id = ldInSec - curTime;


                        DatabaseReference dbNewComment = dbTopic.child(String.valueOf(id));

                        SharedPreferences sharedPreferences = getSharedPreferences("SESSION", MODE_PRIVATE);
                        String sender = sharedPreferences.getString("username", "username");

                        String timestamp = formatter.format(Calendar.getInstance().getTime());

                        dbNewComment.child("sender").setValue(sender);
                        dbNewComment.child("timestamp").setValue(timestamp);
                        dbNewComment.child("commentString").setValue(commentString);

                        /*CommentModel comment = new CommentModel();

                        comment.setCommentString(commentString);
                        comment.setSender(sender);
                        comment.setTimestamp(timestamp);

                        commentList.add(comment);
                        mAdapter.notifyDataSetChanged();
                        progressDialog.dismiss();*/
                    } catch (ParseException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                }
            }
        });

    }
    private void loadData() {
        dbTopic.child("Comment")
                .limitToFirst(TOTAL_ITEM_EACH_LOAD)
                .startAt(currentPage*TOTAL_ITEM_EACH_LOAD)
                .addValueEventListener(new ValueEventListener() {
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
    }

    private void loadMoreData(){
        currentPage++;
        loadData();
    }
}


