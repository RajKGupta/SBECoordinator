package com.example.rajk.leasingmanagers.adapter;

import android.content.Context;
import android.drm.DrmStore;
import android.graphics.Color;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.Gravity;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
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
            holder.messageContainer.setBackgroundResource(R.drawable.chatbubble_right);
            holder.parent_layout.setGravity(Gravity.RIGHT);
            holder.parent_layout.setPadding(150,0,0,0);  //(left,top,right,bottom)
            holder.status.setVisibility(View.VISIBLE);
            holder.meSender_Timestampdate.setText(comment.getSendertimestamp().substring(0,11));
            holder.meSender_Timestamptime.setText(comment.getSendertimestamp().substring(12));
            applyStatus(comment, holder);

        } else {
            holder.parent_layout.setGravity(Gravity.LEFT);
            holder.parent_layout.setPadding(0,0,150,0);
            holder.messageContainer.setBackgroundResource(R.drawable.chatbubble_left);
            holder.meSender_Timestampdate.setText(comment.getSendertimestamp().substring(0,11));
            holder.meSender_Timestamptime.setText(comment.getSendertimestamp().substring(12));
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
                if (comment.getSenderUId().equals(session.getUsername())){

                    if (!comment.getMesenderlocal_storage().equals(""))
                    {
                        holder.photo.setImageURI(Uri.parse(comment.getMesenderlocal_storage()));

                        if (comment.getCommentString()==null) {
                            holder.commentString.setVisibility(View.GONE);
                        } else {
                            holder.commentString.setVisibility(View.VISIBLE);
                            holder.commentString.setText(comment.getCommentString());
                        }
                        break;
                    }
                    else
                    {
                        Glide.with(context)
                                .load(Uri.parse(comment.getImgurl()))
                                .placeholder(R.color.black)
                                .crossFade()
                                .centerCrop()
                                .into(holder.photo);

                        if (comment.getCommentString()==null) {
                            holder.commentString.setVisibility(View.GONE);
                        } else {
                            holder.commentString.setVisibility(View.VISIBLE);
                            holder.commentString.setText(comment.getCommentString());
                        }
                        break;
                    }

                }
                else
                {
                    if (!comment.getOthersenderlocal_storage().equals(""))
                    {
                        holder.download_chatimage.setVisibility(View.GONE);
                        holder.photo.setImageURI(Uri.parse(comment.getOthersenderlocal_storage()));

                        if (comment.getCommentString()==null) {
                            holder.commentString.setVisibility(View.GONE);
                        } else {
                            holder.commentString.setVisibility(View.VISIBLE);
                            holder.commentString.setText(comment.getCommentString());
                        }
                        break;
                    }
                    else
                    {
                        holder.download_chatimage.setVisibility(View.VISIBLE);
                        Glide.with(context)
                                .load(Uri.parse(comment.getImgurl()))
                                .placeholder(R.color.black)
                                .crossFade()
                                .centerCrop()
                                .into(holder.photo);

                        if (comment.getCommentString()==null) {
                            holder.commentString.setVisibility(View.GONE);
                        } else {
                            holder.commentString.setVisibility(View.VISIBLE);
                            holder.commentString.setText(comment.getCommentString());
                        }
                        break;
                    }
                }

            case "doc":
                if (!comment.getSenderUId().equals(session.getUsername())){
                    holder.download_chatimage.setVisibility(View.VISIBLE);
                }
                holder.photo.setVisibility(View.VISIBLE);
                Glide.with(context)
                        .load(R.drawable.download_pdf)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .placeholder(R.color.black)
                        .crossFade()
                        .centerCrop()
                        .into(holder.photo);

                if (comment.getCommentString()==null) {
                    holder.commentString.setVisibility(View.GONE);
                } else {
                    holder.commentString.setVisibility(View.VISIBLE);
                    holder.commentString.setText(comment.getCommentString());
                }
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
                        //pending
                        case "0":
                            holder.status.setImageResource(R.mipmap.ic_sent);
                            break;

                        //sent
                        case "1":
                            holder.status.setImageResource(R.mipmap.ic_sent);
                            break;

                        //delivered
                        case "2":
                            holder.status.setImageResource(R.mipmap.ic_delivered);
                            break;

                        //read
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
        TextView meSender_Timestampdate, meSender_Timestamptime, commentString;
        LinearLayout parent_layout,messageContainer;
        ImageView photo, status;
        DatabaseReference dbCommentStatus;
        ValueEventListener dbCommentStatusListener;
        ProgressBar progressBar;
        ImageButton download_chatimage;

        public MyViewHolder(View itemView) {
            super(itemView);
            messageContainer = (LinearLayout)itemView.findViewById(R.id.sender_message_container);
            parent_layout = (LinearLayout) itemView.findViewById(R.id.parent_layout);
            progressBar = (ProgressBar)itemView.findViewById(R.id.progress);
            status = (ImageView) itemView.findViewById(R.id.status);
            download_chatimage = (ImageButton)itemView.findViewById(R.id.download_chatimage);
            meSender_Timestampdate = (TextView) itemView.findViewById(R.id.meSender_TimeStampdate);
            meSender_Timestamptime = (TextView) itemView.findViewById(R.id.meSender_TimeStamptime);

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
        void download_chatimageClicked(int position);

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

        holder.download_chatimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.download_chatimageClicked(position);
            }
        });
    }
}

