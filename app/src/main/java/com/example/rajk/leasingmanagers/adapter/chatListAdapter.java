package com.example.rajk.leasingmanagers.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.rajk.leasingmanagers.R;
import com.example.rajk.leasingmanagers.helper.CircleTransform;
import com.example.rajk.leasingmanagers.model.ChatListModel;
import com.example.rajk.leasingmanagers.model.ChatMessage;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;

import static com.example.rajk.leasingmanagers.LeasingManagers.DBREF;

/**
 * Created by RajK on 16-05-2017.
 */

public class chatListAdapter extends RecyclerView.Adapter<chatListAdapter.MyViewHolder> {
    ArrayList<ChatListModel> list = new ArrayList<>();
    private Context context;
    private chatListAdapterListener listener;


    public chatListAdapter(ArrayList<ChatListModel> list, Context context, chatListAdapterListener listener) {
        this.context = context;
        this.list = list;
        this.listener = listener;


    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView author, message, timestamp, icon_text,tvunread;
        ImageView imgProfile;
        LinearLayout messageContainer;
        RelativeLayout relunread;


        public MyViewHolder(View itemView) {
            super(itemView);
            relunread = (RelativeLayout)itemView.findViewById(R.id.relunread);
            author = (TextView) itemView.findViewById(R.id.author);
            message = (TextView) itemView.findViewById(R.id.message);
            timestamp = (TextView) itemView.findViewById(R.id.timestamp);
            icon_text = (TextView) itemView.findViewById(R.id.icon_text);
            tvunread = (TextView) itemView.findViewById(R.id.unreadmsgs);
            imgProfile = (ImageView) itemView.findViewById(R.id.icon_profile);
            messageContainer = (LinearLayout) itemView.findViewById(R.id.message_container);
        }

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_list_row, parent, false);
        return new MyViewHolder(view);

    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        ChatListModel topic = list.get(position);
        holder.author.setText(topic.getName());
        applyClickEvents(holder, position);
        applyProfilePicture(holder, topic);
        applyLastMessage(holder, topic);
        findunreadmsgs(holder,topic);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public interface chatListAdapterListener {
        void onChatRowClicked(int position);
    }

    private void applyClickEvents(MyViewHolder holder, final int position) {

        holder.messageContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onChatRowClicked(position);
            }
        });

    }

    private void applyProfilePicture(MyViewHolder holder, ChatListModel message) {
        if (!message.getProfpic().equals("nopicfound")) {
            Glide.with(context).load(message.getProfpic())
                    .thumbnail(0.5f)
                    .crossFade()
                    .transform(new CircleTransform(context))
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(holder.imgProfile);
            holder.imgProfile.setColorFilter(null);
            holder.icon_text.setVisibility(View.GONE);
        } else {
            holder.imgProfile.setImageResource(R.drawable.bg_circle);
            holder.imgProfile.setColorFilter(message.getColor());
            char icontext = message.getName().toUpperCase().charAt(0);
            holder.icon_text.setText(icontext + "");
            holder.icon_text.setVisibility(View.VISIBLE);
        }
    }

    private void applyLastMessage(final MyViewHolder holder, ChatListModel topic) {
        DatabaseReference dbTopicLastComment = DBREF.child("Chats").child(topic.getDbTableKey()).child("ChatMessages").getRef();
        dbTopicLastComment.limitToLast(1).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot.exists()) {
                    ChatMessage chatMessage = dataSnapshot.getValue(ChatMessage.class);
                    if (chatMessage.getCommentString() != null) {
                        if(!chatMessage.getType().equals("text")) {
                            holder.message.setText(chatMessage.getCommentString());

                        }
                        else
                            holder.message.setText(chatMessage.getCommentString());
                    }
                    holder.timestamp.setText(chatMessage.getSendertimestamp());
                }

            }




            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot.exists()) {
                    ChatMessage chatMessage = dataSnapshot.getValue(ChatMessage.class);
                    if (chatMessage.getCommentString() != null) {
                        if(!chatMessage.getType().equals("text")) {
                            holder.message.setText(chatMessage.getCommentString());

                        }
                        else
                        holder.message.setText(chatMessage.getCommentString());
                    }
                    holder.timestamp.setText(chatMessage.getSendertimestamp());
                }

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

    private void findunreadmsgs(final MyViewHolder holder, final ChatListModel topic) {
        String a="nil";
        DatabaseReference dbTopicLastComment = DBREF.child("Chats").child(topic.getDbTableKey()).child("ChatMessages").getRef();
        System.out.println(topic.getDbTableKey()+" unreadmsgs called " + dbTopicLastComment);

        dbTopicLastComment.orderByChild("status").equalTo("2").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){

                    holder.relunread.setVisibility(View.VISIBLE);

                System.out.println(dataSnapshot.getChildrenCount()+" unreadmsgs " + dataSnapshot.getValue());
                holder.tvunread.setText(String.valueOf(dataSnapshot.getChildrenCount()));
                }
                else
                {
                    holder.relunread.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
