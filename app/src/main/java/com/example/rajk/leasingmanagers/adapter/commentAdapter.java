package com.example.rajk.leasingmanagers.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.rajk.leasingmanagers.R;
import com.example.rajk.leasingmanagers.model.CommentModel;

import java.util.ArrayList;

/**
 * Created by RajK on 16-05-2017.
 */

public class commentAdapter extends  RecyclerView.Adapter<commentAdapter.MyViewHolder>
        {
        ArrayList<CommentModel> list = new ArrayList<>();
        private Context context;
        SharedPreferences sharedPreferences ;

public commentAdapter(ArrayList<CommentModel> list, Context context) {
        this.list = list;
        this.context = context;
        sharedPreferences = context.getSharedPreferences("SESSION",Context.MODE_PRIVATE);
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
                    holder.meSender_sender.setText(comment.getSender());
                    holder.meSender_Timestamp.setText(comment.getTimestamp());
                }
                else
                {
                    holder.meSender.setVisibility(View.GONE);
                    holder.otherSender_sender.setText(comment.getSender());
                    holder.otherSender_Timestamp.setText(comment.getTimestamp());
                }
                holder.commentString.setText(comment.getCommentString());
            }

            @Override
            public int getItemCount() {
                return list.size();
            }

            public class MyViewHolder extends RecyclerView.ViewHolder {
                TextView otherSender_sender, otherSender_Timestamp, meSender_sender, meSender_Timestamp, commentString;
                LinearLayout otherSender, meSender;

                public MyViewHolder(View itemView) {
                    super(itemView);
                    otherSender_sender = (TextView) itemView.findViewById(R.id.otherSender_Sender);
                    otherSender_Timestamp = (TextView) itemView.findViewById(R.id.otherSender_TimeStamp);
                    otherSender = (LinearLayout) itemView.findViewById(R.id.otherSender);

                    meSender_sender = (TextView) itemView.findViewById(R.id.meSender_Sender);
                    meSender_Timestamp = (TextView) itemView.findViewById(R.id.meSender_TimeStamp);
                    meSender = (LinearLayout) itemView.findViewById(R.id.meSender);

                    commentString = (TextView) itemView.findViewById(R.id.commentString);

                }


            }
        }
