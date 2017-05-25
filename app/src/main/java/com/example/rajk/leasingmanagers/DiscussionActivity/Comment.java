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
import com.google.firebase.database.ChildEventListener;
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
    SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy hh:mm am");
    private String lastDate = "20-01-3000 00:00";
    private RecyclerView.Adapter mAdapter;
<<<<<<< Updated upstream
    private ArrayList<CommentModel> commentList= new ArrayList<>();

=======
    private ArrayList<CommentModel> commentList = new ArrayList<>();
    private SharedPreferences sharedPreferences;

    long ldInSec = 9999999999999999L;

    String place_id;
>>>>>>> Stashed changes

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        height = displaymetrics.heightPixels;
        width = displaymetrics.widthPixels;

<<<<<<< Updated upstream
        intent = getIntent();
        topic_id = intent.getStringExtra("topic_id");
        dbTopic = FirebaseDatabase.getInstance().getReference().child("MeChat").child("Topic").child(topic_id).child("Comment").getRef();

=======
        sharedPreferences = getSharedPreferences("SESSION", MODE_PRIVATE);

        place_id = sharedPreferences.getString("place_id", "449");
        intent = getIntent();
        topic_id = intent.getStringExtra("topic_id");
        dbTopic = FirebaseDatabase.getInstance().getReference().child("MeChat").child("Topic").child(place_id).child(topic_id).child("Comment");
>>>>>>> Stashed changes

        recyclerView = (EmptyRecyclerView) findViewById(R.id.my_recycler_view);
        emptyView = (LinearLayout) findViewById(R.id.empty_view);
        recyclerView.setEmptyView(emptyView);
        progressBar = (ProgressBar) findViewById(R.id.progress);

        typeComment = (AutoCompleteTextView) findViewById(R.id.typeComment);
        sendButton = (ImageButton) findViewById(R.id.sendButton);
<<<<<<< Updated upstream
        linearLayoutManager=new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        mAdapter = new commentAdapter(commentList,this);
=======
        final ProgressDialog progressDialog = new ProgressDialog(Comment.this);

        mAdapter = new commentAdapter(commentList, this);
        linearLayoutManager = new LinearLayoutManager(this);
        //linearLayoutManager.setReverseLayout(true);
        //linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
>>>>>>> Stashed changes
        recyclerView.setAdapter(mAdapter);
        /*recyclerView.setOnScrollListener(new EndlessRecyclerOnScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int current_page) { // when we have reached end of RecyclerView this event fired
                loadMoreData();
            }
        });*/
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
<<<<<<< Updated upstream
                    try {
=======
                    progressDialog.setMessage("Posting Comment...");
                    progressDialog.show();

                    //Date lastdate = formatter.parse(lastDate);
                    //long ldInSec = lastdate.getTime();
                    long curTime = Calendar.getInstance().getTimeInMillis();
                    //long id = ldInSec - curTime;
                    long id = curTime;

                    DatabaseReference dbNewComment;

                            SharedPreferences sharedPreferences = getSharedPreferences("SESSION", MODE_PRIVATE);
                    String sender = sharedPreferences.getString("username", "username");

                    String timestamp = formatter.format(Calendar.getInstance().getTime());

                    CommentModel cm = new CommentModel(commentString,sender,timestamp);
                    //dbNewComment.child("sender").setValue(sender);
                    //dbNewComment.child("timestamp").setValue(timestamp);
                    //dbNewComment.child("commentString").setValue(commentString);
                    dbTopic.child(String.valueOf(id)).setValue(cm);

                    progressDialog.dismiss();
                }
            }
        });
    }
>>>>>>> Stashed changes

    @Override
    public void onBackPressed()
    {
        Intent intent = new Intent(getApplicationContext(),Home.class);
        startActivity(intent);
        finish();
    }

    private void loadData()
    {
        dbTopic.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (!dataSnapshot.hasChildren())
                {
                    Toast.makeText(Comment.this, "No more comments", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    CommentModel comment = dataSnapshot.getValue(CommentModel.class);
                    commentList.add(comment);
                    mAdapter.notifyDataSetChanged();
                    recyclerView.scrollToPosition(commentList.size() - 1);
                }
            }

<<<<<<< Updated upstream

                        DatabaseReference dbNewComment = dbTopic.child(String.valueOf(id));
=======
            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
>>>>>>> Stashed changes

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

<<<<<<< Updated upstream
                        dbNewComment.child("Sender").setValue(sender);
                        dbNewComment.child("Timestamp").setValue(timestamp);
                        dbNewComment.child("commentString").setValue(commentString);
                        mAdapter.notifyDataSetChanged();
                        progressDialog.dismiss();
                    } catch (ParseException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
=======
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
>>>>>>> Stashed changes

            }
        });

/*    private void loadMoreData(){
        currentPage++;
        loadData();
    }*/
    }
}


