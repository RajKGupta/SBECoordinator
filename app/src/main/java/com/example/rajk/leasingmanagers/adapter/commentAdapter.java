package com.example.rajk.leasingmanagers.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.rajk.leasingmanagers.R;
import com.example.rajk.leasingmanagers.helper.CircleTransform;
import com.example.rajk.leasingmanagers.model.CommentModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * Created by RajK on 16-05-2017.
 */

public class commentAdapter extends  RecyclerView.Adapter<commentAdapter.MyViewHolder>
        {
        ArrayList<CommentModel> list = new ArrayList<>();
        private Context context;
        SharedPreferences sharedPreferences ;
            String place_id,topic_id;

        public commentAdapter(ArrayList<CommentModel> list, Context context, String place_id, String topic_id) {
            this.list = list;
            this.context = context;
            sharedPreferences = context.getSharedPreferences("SESSION",Context.MODE_PRIVATE);
            this.place_id = place_id;
            this.topic_id = topic_id;
        }

            @Override
            public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_row,parent,false);
                return new MyViewHolder(view);

            }

            @Override
            public void onBindViewHolder(commentAdapter.MyViewHolder holder, int position) {
                CommentModel comment = list.get(position);
                if(comment.getSender().equals(sharedPreferences.getString("username","abc")))
                {
                    holder.otherSender.setVisibility(View.GONE);
                    holder.meSender.setVisibility(View.VISIBLE);
                    holder.status.setVisibility(View.VISIBLE);
                    holder.meSender_sender.setText(comment.getSender());
                    holder.meSender_Timestamp.setText(comment.getTimestamp());
                    applyStatus(comment,holder);

                }
                else
                {
                    holder.meSender.setVisibility(View.GONE);
                    holder.otherSender.setVisibility(View.VISIBLE);
                    holder.otherSender_sender.setText(comment.getSender());
                    holder.otherSender_Timestamp.setText(comment.getTimestamp());
                    holder.status.setVisibility(View.GONE);
                }
                String type = comment.getType();
                switch (type)
                {
                    case "text":
                        holder.commentString.setText(comment.getCommentString());
                        holder.photo.setVisibility(View.GONE);
                        break;

                    case "photo":
                        holder.photo.setVisibility(View.VISIBLE);
                        Glide.with(context).load(comment.getImgurl())
                                .thumbnail(0.5f)
                                .crossFade()
                                .transform(new CircleTransform(context))
                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .into(holder.photo);


                        if(comment.getCommentString().equals(""))
                        {
                            holder.commentString.setVisibility(View.GONE);
                        }
                        else
                        {
                            holder.commentString.setVisibility(View.VISIBLE);
                            holder.commentString.setText(comment.getCommentString());
                        }
                        break;

                    case "video":
                        break;

                    case "doc" :
                        break;
                }

                }

            private void applyStatus(CommentModel comment, final MyViewHolder holder) {
                final DatabaseReference dbCommentStatus = FirebaseDatabase.getInstance().getReference().child("MeChat").child("Topic").child(place_id).child(topic_id).child("Comment").child(comment.getId()).child("status").getRef();
                dbCommentStatus.addValueEventListener(new ValueEventListener() {


                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String status = dataSnapshot.getValue(String.class);
                        switch (status) {
                            case "0":
                                holder.status.setImageResource(R.mipmap.ic_pending);
                                break;

                            case "1":
                                holder.status.setImageResource(R.mipmap.ic_sent);
                                break;

                            case "2":
                                holder.status.setImageResource(R.mipmap.ic_delivered);
                                break;

                            case "3":
                                holder.status.setImageResource(R.mipmap.ic_read);
                                dbCommentStatus.removeEventListener(this);
                                break;

                        }
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

            public class MyViewHolder extends RecyclerView.ViewHolder {
                TextView otherSender_sender, otherSender_Timestamp, meSender_sender, meSender_Timestamp, commentString;
                LinearLayout otherSender, meSender;
                ImageView status,photo;

                public MyViewHolder(View itemView) {
                    super(itemView);

                    otherSender_sender = (TextView) itemView.findViewById(R.id.otherSender_Sender);
                    otherSender_Timestamp = (TextView) itemView.findViewById(R.id.otherSender_TimeStamp);
                    otherSender = (LinearLayout) itemView.findViewById(R.id.otherSender);

                    status = (ImageView)itemView.findViewById(R.id.status);

                    meSender_sender = (TextView) itemView.findViewById(R.id.meSender_Sender);
                    meSender_Timestamp = (TextView) itemView.findViewById(R.id.meSender_TimeStamp);
                    meSender = (LinearLayout) itemView.findViewById(R.id.meSender);

                    commentString = (TextView) itemView.findViewById(R.id.commentString);

                    photo = (ImageView)itemView.findViewById(R.id.photo);

                }
            }
        }
