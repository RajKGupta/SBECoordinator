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

import com.example.rajk.leasingmanagers.CoordinatorLogin.CoordinatorSession;
import com.example.rajk.leasingmanagers.R;
import com.example.rajk.leasingmanagers.model.ChatListModel;
import com.example.rajk.leasingmanagers.model.ChatMessage;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;

import static com.example.rajk.leasingmanagers.LeasingManagers.DBREF;
import static com.example.rajk.leasingmanagers.LeasingManagers.simpleDateFormatWithMonthName;

public class chatListAdapter extends RecyclerView.Adapter<chatListAdapter.MyViewHolder> {
    ArrayList<ChatListModel> list = new ArrayList<>();
    private Context context;
    private HashMap<DatabaseReference, ChildEventListener> hashMapCHE;
    private HashMap<DatabaseReference, ValueEventListener> hashMapVLE;
    private CoordinatorSession coordinatorSession;
    private String mykey;
    public ArrayList<ChatListModel> filterlist;

    public chatListAdapter(ArrayList<ChatListModel> list, Context context) {
        this.context = context;
        this.list = list;
        hashMapCHE = new HashMap<>();
        hashMapVLE = new HashMap<>();
        coordinatorSession = new CoordinatorSession(context);
        mykey = coordinatorSession.getUsername();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView author, message, timestamp, icon_text, tvunread;
        ImageView imgProfile, onlineStatus;
        LinearLayout messageContainer;
        RelativeLayout relunread;

        public MyViewHolder(View itemView) {
            super(itemView);
            relunread = (RelativeLayout) itemView.findViewById(R.id.relunread);
            author = (TextView) itemView.findViewById(R.id.author);
            message = (TextView) itemView.findViewById(R.id.message);
            timestamp = (TextView) itemView.findViewById(R.id.timestamp);
            icon_text = (TextView) itemView.findViewById(R.id.icon_text);
            tvunread = (TextView) itemView.findViewById(R.id.unreadmsgs);
            imgProfile = (ImageView) itemView.findViewById(R.id.icon_profile);
            messageContainer = (LinearLayout) itemView.findViewById(R.id.message_container);
            onlineStatus = (ImageView) itemView.findViewById(R.id.onlineStatus);

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
        applyProfilePicture(holder, topic);
        applyLastMessage(holder, topic);
        applyOnlineStatus(holder, topic);
        findunreadmsgs(holder, topic);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    private void applyProfilePicture(MyViewHolder holder, ChatListModel message) {

        holder.imgProfile.setImageResource(R.drawable.bg_circle);
        holder.imgProfile.setColorFilter(message.getColor());
        char icontext = message.getName().toUpperCase().charAt(0);
        holder.icon_text.setText(icontext + "");
        holder.icon_text.setVisibility(View.VISIBLE);

    }

    private void applyLastMessage(final MyViewHolder holder, ChatListModel topic) {
        DatabaseReference dbTopicLastComment = DBREF.child("Chats").child(topic.getDbTableKey()).child("ChatMessages").getRef();
        ChildEventListener childEventListener = dbTopicLastComment.limitToLast(1).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot.exists()) {
                    ChatMessage chatMessage = dataSnapshot.getValue(ChatMessage.class);
                    if (chatMessage.getType().equals("text")) {
                        holder.message.setText(chatMessage.getCommentString());
                    } else if (chatMessage.getType().equals("doc")) {
                        holder.message.setText("Sent a Document");
                    } else if (chatMessage.getType().equals("photo")) {
                        holder.message.setText("Sent an Image");
                    }

                    String timestamp = simpleDateFormatWithMonthName.format(Calendar.getInstance().getTime());
                    String senderTimestamp = chatMessage.getSendertimestamp().substring(0, 11);
                    if (timestamp.equals(senderTimestamp))
                        senderTimestamp = chatMessage.getSendertimestamp().substring(12).trim();

                    holder.timestamp.setText(senderTimestamp);
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
        hashMapCHE.put(dbTopicLastComment, childEventListener);

    }

    private void applyOnlineStatus(final MyViewHolder holder, ChatListModel chatListModel) {

        DatabaseReference dbOnlineStatus = DBREF.child("Users").child("Usersessions").child(chatListModel.getUserkey()).child("online").getRef();
        ValueEventListener valueEventListener = dbOnlineStatus.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    boolean online = dataSnapshot.getValue(Boolean.class);
                    if (online == true) {
                        holder.onlineStatus.setVisibility(View.VISIBLE);
                    } else {
                        holder.onlineStatus.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        hashMapVLE.put(dbOnlineStatus, valueEventListener);
    }


    private void findunreadmsgs(final MyViewHolder holder, final ChatListModel topic) {
        String a = "nil";
        DatabaseReference dbTopicLastComment = DBREF.child("Chats").child(topic.getDbTableKey()).child("ChatMessages").getRef();
        System.out.println(topic.getDbTableKey() + " unreadmsgs called " + dbTopicLastComment);
        final Integer[] countunreadmessages = {0};
        ValueEventListener valueEventListener = dbTopicLastComment.orderByChild("status").equalTo("2").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists())
                {
                    holder.relunread.setVisibility(View.VISIBLE);
                    ChatMessage chatMessage = new ChatMessage();
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        chatMessage = ds.getValue(ChatMessage.class);
                        if (chatMessage.getReceiverUId().equals(mykey))
                            countunreadmessages[0]++;
                    }
                    if (countunreadmessages[0] != 0) {
                        holder.relunread.setVisibility(View.VISIBLE);
                        holder.tvunread.setText(String.valueOf(countunreadmessages[0]));
                    }
                    else {
                        holder.relunread.setVisibility(View.GONE);
                     }
                } else {
                    holder.relunread.setVisibility(View.GONE);
                    countunreadmessages[0] = 0;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        hashMapVLE.put(dbTopicLastComment, valueEventListener);
    }

    public void removeListeners() {
        Iterator<HashMap.Entry<DatabaseReference, ChildEventListener>> iterator = hashMapCHE.entrySet().iterator();
        while (iterator.hasNext()) {
            HashMap.Entry<DatabaseReference, ChildEventListener> entry = (HashMap.Entry<DatabaseReference, ChildEventListener>) iterator.next();
            if (entry.getValue() != null)
                entry.getKey().removeEventListener(entry.getValue());
        }
        Iterator<HashMap.Entry<DatabaseReference, ValueEventListener>> iterator2 = hashMapVLE.entrySet().iterator();
        while (iterator2.hasNext()) {
            HashMap.Entry<DatabaseReference, ValueEventListener> entry = (HashMap.Entry<DatabaseReference, ValueEventListener>) iterator2.next();
            if (entry.getValue() != null) entry.getKey().removeEventListener(entry.getValue());
        }

    }
}
