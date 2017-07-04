package com.example.rajk.leasingmanagers.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.example.rajk.leasingmanagers.CoordinatorLogin.CoordinatorSession;
import com.example.rajk.leasingmanagers.R;
import com.example.rajk.leasingmanagers.model.ChatMessage;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by RajK on 16-05-2017.
 */

public class chatAdapter extends RecyclerView.Adapter<chatAdapter.MyViewHolder> {
    ArrayList<ChatMessage> list = new ArrayList<>();
    private Context context;
    private CoordinatorSession session;
    String dbTablekey;
    private SparseBooleanArray selectedItems;
    private static int currentSelectedIndex = -1;
    private SparseBooleanArray animationItemsIndex;
    private boolean reverseAllAnimations = false;
    private ChatAdapterListener listener;


    public chatAdapter(ArrayList<ChatMessage> list, Context context, String dbTableKey,ChatAdapterListener listener) {
        this.list = list;
        this.context = context;
        session = new CoordinatorSession(context);
        this.dbTablekey = dbTableKey;
        selectedItems = new SparseBooleanArray();
        animationItemsIndex = new SparseBooleanArray();
        this.listener =  listener;

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_row, parent, false);
        return new MyViewHolder(view);

    }

    @Override
    public void onBindViewHolder(chatAdapter.MyViewHolder holder, int position) {
        ChatMessage comment = list.get(position);
        if (comment.getSenderUId().equals(session.getUsername())) {
            holder.otherSender.setVisibility(View.GONE);
            holder.meSender.setVisibility(View.VISIBLE);
            holder.status.setVisibility(View.VISIBLE);
            holder.meSender_sender.setText(comment.getSenderUId());
            holder.meSender_Timestamp.setText(comment.getSendertimestamp());
            applyStatus(comment, holder);

        } else {
            holder.meSender.setVisibility(View.GONE);
            holder.otherSender.setVisibility(View.VISIBLE);
            holder.otherSender_sender.setText(comment.getSenderUId());
            holder.otherSender_Timestamp.setText(comment.getSendertimestamp());
            holder.status.setVisibility(View.GONE);
        }
        applyClickEvents(holder,position);
        applyProgressBar(holder,comment);
        String type = comment.getType();
        switch (type) {
            case "text":
                holder.commentString.setText(comment.getCommentString());
                holder.photo.setVisibility(View.GONE);
                break;

            case "photo":
                holder.photo.setVisibility(View.VISIBLE);
                Glide.with(context).load(Uri.parse(comment.getImgurl())).into(holder.photo);

                if (comment.getCommentString()==null) {
                    holder.commentString.setVisibility(View.GONE);
                } else {
                    holder.commentString.setVisibility(View.VISIBLE);
                    holder.commentString.setText(comment.getCommentString());
                }
                break;

            case "doc":
                break;
        }
    }

    private void applyStatus(ChatMessage comment, final MyViewHolder holder) {
        holder.dbCommentStatus = FirebaseDatabase.getInstance().getReference().child("Chats").child(dbTablekey).child("ChatMessages").child(comment.getId()).child("status").getRef();
        holder.dbCommentStatusListener = holder.dbCommentStatus.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
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
                            holder.dbCommentStatus.removeEventListener(this);
                            break;

                    }
                }

            }



            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    private void applyProgressBar(final MyViewHolder holder, ChatMessage chatMessage)
    {
        if(!chatMessage.getType().equals("text")&&chatMessage.getSenderUId().equals(session.getUsername()))
        {
            DatabaseReference dbUploadStatus = FirebaseDatabase.getInstance().getReference().child("Chats").child(dbTablekey).child("ChatMessages").child(chatMessage.getId()).child("percentageUploaded").getRef();
            dbUploadStatus.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists())
                    {
                        int percent = dataSnapshot.getValue(int.class);
                        if(percent!=100)
                        {
                            holder.progressBar.setVisibility(View.VISIBLE);
                        }
                        else
                        {
                            holder.progressBar.setVisibility(View.GONE);
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView otherSender_sender, otherSender_Timestamp, meSender_sender, meSender_Timestamp, commentString;
        LinearLayout otherSender, meSender,messageContainer;
        ImageView status, photo;
        DatabaseReference dbCommentStatus;
        ValueEventListener dbCommentStatusListener;
        ProgressBar progressBar;

        public MyViewHolder(View itemView) {
            super(itemView);
            messageContainer = (LinearLayout)itemView.findViewById(R.id.message_container);
            otherSender_sender = (TextView) itemView.findViewById(R.id.otherSender_Sender);
            otherSender_Timestamp = (TextView) itemView.findViewById(R.id.otherSender_TimeStamp);
            otherSender = (LinearLayout) itemView.findViewById(R.id.otherSender);
            progressBar = (ProgressBar)itemView.findViewById(R.id.progress);
            status = (ImageView) itemView.findViewById(R.id.status);

            meSender_sender = (TextView) itemView.findViewById(R.id.meSender_Sender);
            meSender_Timestamp = (TextView) itemView.findViewById(R.id.meSender_TimeStamp);
            meSender = (LinearLayout) itemView.findViewById(R.id.meSender);

            commentString = (TextView) itemView.findViewById(R.id.commentString);

            photo = (ImageView) itemView.findViewById(R.id.photo);

        }
    }

    public void resetAnimationIndex() {
        reverseAllAnimations = false;
        animationItemsIndex.clear();
    }
    public void toggleSelection(int pos) {
        currentSelectedIndex = pos;
        if (selectedItems.get(pos, false)) {
            selectedItems.delete(pos);
            animationItemsIndex.delete(pos);
        } else {
            selectedItems.put(pos, true);
            animationItemsIndex.put(pos, true);
        }
        notifyItemChanged(pos);
    }

    public void clearSelections() {
        reverseAllAnimations = true;
        selectedItems.clear();
        notifyDataSetChanged();
    }

    public int getSelectedItemCount() {
        return selectedItems.size();
    }

    public List<Integer> getSelectedItems() {
        List<Integer> items =
                new ArrayList<>(selectedItems.size());
        for (int i = 0; i < selectedItems.size(); i++) {
            items.add(selectedItems.keyAt(i));
        }
        return items;
    }

    public void removeData(int position) {
        list.remove(position);
        resetCurrentIndex();
    }

    private void resetCurrentIndex() {
        currentSelectedIndex = -1;
    }
    public interface ChatAdapterListener {


        void onMessageRowClicked(int position);

        void onRowLongClicked(int position);
    }
    private void applyRowAnimation(MyViewHolder holder, int position) {
            if ((reverseAllAnimations && animationItemsIndex.get(position, false)) || currentSelectedIndex == position) {
                //FlipAnimator.flipView(mContext, holder.iconBack, holder.iconFront, false);
//TODO
                resetCurrentIndex();
            }

    }
    private void applyClickEvents(MyViewHolder holder, final int position) {

        holder.messageContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onMessageRowClicked(position);
            }
        });

        holder.messageContainer.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                listener.onRowLongClicked(position);
                view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
                return true;
            }
        });
    }


}

