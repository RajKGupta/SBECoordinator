package com.example.rajk.leasingmanagers.DiscussionActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.example.rajk.leasingmanagers.R;
import com.example.rajk.leasingmanagers.adapter.commentAdapter;
import com.example.rajk.leasingmanagers.listener.EmptyRecyclerView;
import com.example.rajk.leasingmanagers.model.CommentModel;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class Comment extends AppCompatActivity {
    private static final int TOTAL_ITEM_EACH_LOAD = 20;
    private EditText typeComment;
    private ImageButton sendButton,attachment;
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
    SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy hh:mm aa");
    private String lastDate = "20-01-3000 00:00";
    private RecyclerView.Adapter mAdapter;
    private ArrayList<CommentModel> commentList= new ArrayList<>();
    private SharedPreferences sharedPreferences;
    String username;
    boolean clicked;
    View view;
    String place_id;
    LinearLayout layoutToAdd;
    LinearLayout commentView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        clicked = false;
        layoutToAdd = (LinearLayout)findViewById(R.id.attachmentpopup);
        getSupportActionBar().setTitle("Comments");
        sharedPreferences=getSharedPreferences("SESSION",MODE_PRIVATE);
        username = sharedPreferences.getString("username","username");
        place_id=sharedPreferences.getString("place_id","449");
        intent = getIntent();
        topic_id = intent.getStringExtra("topic_id");
        dbTopic = FirebaseDatabase.getInstance().getReference().child("MeChat").child("Topic").child(place_id).child(topic_id).child("Comment");

        commentView = (LinearLayout)findViewById(R.id.commentView);
        recyclerView = (EmptyRecyclerView) findViewById(R.id.my_recycler_view);
        emptyView = (LinearLayout) findViewById(R.id.empty_view);
        recyclerView.setEmptyView(emptyView);
        progressBar = (ProgressBar) findViewById(R.id.progress);

        typeComment = (EditText) findViewById(R.id.typeComment);
        sendButton = (ImageButton) findViewById(R.id.sendButton);
        attachment = (ImageButton) findViewById(R.id.attachment);

        attachment.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if(clicked==false) {

                    layoutToAdd.setVisibility(View.VISIBLE);
                    clicked =true;
                }
                else
                {
                    layoutToAdd.setVisibility(View.GONE);
                    clicked=false;
                }
            }
        });

        linearLayoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        mAdapter = new commentAdapter(commentList,this,place_id,topic_id);
        recyclerView.setAdapter(mAdapter);
/*        recyclerView.setOnScrollListener(new EndlessRecyclerOnScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int current_page) { // when we have reached end of RecyclerView this event fired
                loadMoreData();
            }
        });*/
        loadData(); // load data here for first time launch app

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String commentString = typeComment.getText().toString().trim();
                if (TextUtils.isEmpty(commentString)) {
                    Toast.makeText(Comment.this, "What?? No Comment!!", Toast.LENGTH_SHORT).show();
                } else {

                        ProgressDialog progressDialog = new ProgressDialog(Comment.this);
                        progressDialog.setMessage("Posting Comment...");
                        progressDialog.show();

                        //Date lastdate = formatter.parse(lastDate);
                        //long ldInSec = lastdate.getTime();
                        long curTime = Calendar.getInstance().getTimeInMillis();
                        long id = curTime;

                        SharedPreferences sharedPreferences = getSharedPreferences("SESSION", MODE_PRIVATE);
                        String sender = username;

                        String timestamp = formatter.format(Calendar.getInstance().getTime());

                        CommentModel cm = new CommentModel(commentString,sender,timestamp,"text",String.valueOf(id),"0");
                        dbTopic.child(String.valueOf(id)).setValue(cm);
                        progressDialog.dismiss();
                        typeComment.setText("");

                }
            }
        });

    }
    private void loadData() {
        dbTopic.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if(!dataSnapshot.hasChildren()){
                    Toast.makeText(Comment.this, "No more comments", Toast.LENGTH_SHORT).show();
                }

                    CommentModel comment = dataSnapshot.getValue(CommentModel.class);
                    if(!comment.getSender().equals(username)) {

                        dbTopic.child(comment.getId()).child("status").setValue("3");
                        comment.setStatus("3");  // all message status set to read
                    }
                    else
                    {
                        if(comment.getStatus().equals("0"))
                        dbTopic.child(comment.getId()).child("status").setValue("1");
                        comment.setStatus("1");  // all message status set to read
                    }
                    commentList.add(comment);
                    mAdapter.notifyDataSetChanged();
                    recyclerView.scrollToPosition(commentList.size() - 1);
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

    private void loadMoreData(){
        currentPage++;
        loadData();
    }

    @Override
    public void onBackPressed() {
        if(clicked ==true) {
            layoutToAdd.setVisibility(View.GONE);
            clicked =false;
        }
        else
            startActivity(new Intent(Comment.this,Home.class));
            finish();
    }
}