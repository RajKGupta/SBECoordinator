package com.example.rajk.leasingmanagers.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.example.rajk.leasingmanagers.R;
import com.example.rajk.leasingmanagers.model.Discussions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.ArrayList;

/**
 * Created by RajK on 16-05-2017.
 */

public class topicAdapter extends  RecyclerView.Adapter<topicAdapter.MyViewHolder>
        {
        ArrayList<Discussions> list = new ArrayList<>();
        private Context context;
        private TopicAdapterListener listener;


            public topicAdapter(ArrayList<Discussions> list, Context context,TopicAdapterListener listener)
            {
            this.list = list;
            this.listener = listener;
            }

            public class MyViewHolder extends RecyclerView.ViewHolder {
                TextView topic,author,message,timestamp,icon_text;
                ImageView imgProfile;
                public LinearLayout messageContainer;

                public MyViewHolder(View itemView) {
                    super(itemView);
                    topic = (TextView) itemView.findViewById(R.id.topic);
                    author = (TextView) itemView.findViewById(R.id.author);
                    message = (TextView) itemView.findViewById(R.id.message);
                    timestamp = (TextView) itemView.findViewById(R.id.timestamp);
                    icon_text =(TextView)itemView.findViewById(R.id.icon_text);
                    imgProfile = (ImageView)itemView.findViewById(R.id.icon_profile);
                    messageContainer = (LinearLayout)itemView.findViewById(R.id.message_container);
                }

            }

            @Override
            public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
            {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.topic_list_row,parent,false);
                return new MyViewHolder(view);

            }

            @Override
            public void onBindViewHolder(final topicAdapter.MyViewHolder holder, int position) {
                Discussions topic = list.get(position);
                holder.topic.setText(topic.getName());
                String iconText = topic.getName().toUpperCase();
                holder.icon_text.setText(iconText.charAt(0)+"");
                holder.imgProfile.setImageResource(R.drawable.bg_circle);
                holder.imgProfile.setColorFilter(topic.getColor());
                applyClickEvents(holder, position);
                final DatabaseReference dbTopic = FirebaseDatabase.getInstance().getReference().child("MeChat").child("Topic").child(topic.getPlace_id()).child(topic.getName()).getRef();
                        DatabaseReference dbTopicLastComment  = dbTopic.child("Comment").getRef();
                        dbTopicLastComment.limitToLast(1).addChildEventListener(new ChildEventListener() {
                            @Override
                            public void onChildAdded(DataSnapshot dataSnapshot, String s)
                            {
                                if (dataSnapshot.exists())
                                {
                                holder.message.setText(dataSnapshot.child("commentString").getValue(String.class));
                                holder.author.setText(dataSnapshot.child("sender").getValue(String.class)+" : ");
                                holder.timestamp.setText(dataSnapshot.child("timestamp").getValue(String.class));
                                }
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

            @Override
            public int getItemCount() {
                return list.size();
            }
            public interface TopicAdapterListener {
                void onTopicRowClicked(int position);
}
            private void applyClickEvents(MyViewHolder holder, final int position) {

                holder.messageContainer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        listener.onTopicRowClicked(position);
                    }
                });

                }

        }
