package com.example.rajk.leasingmanagers.notification;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.rajk.leasingmanagers.CoordinatorLogin.CoordinatorSession;
import com.example.rajk.leasingmanagers.MainViews.TaskDetail;
import com.example.rajk.leasingmanagers.R;
import com.example.rajk.leasingmanagers.adapter.notification_adapter;
import com.example.rajk.leasingmanagers.model.Notif;
import com.example.rajk.leasingmanagers.tablayout.Tabs;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.List;

import static com.example.rajk.leasingmanagers.LeasingManagers.DBREF;

public class NotificationActivity extends AppCompatActivity {

    RecyclerView recview;
    notification_adapter adapter;
    List<Notif> list = new ArrayList<>();
    Notif notif  = new Notif();
    String Username;
    CoordinatorSession session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        session = new CoordinatorSession(getApplicationContext());
        Username = session.getUsername();

        recview = (RecyclerView) findViewById(R.id.notification_list);
        recview.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recview.setItemAnimator(new DefaultItemAnimator());
        recview.addItemDecoration(new DividerItemDecoration(getApplicationContext(), LinearLayoutManager.VERTICAL));

        adapter = new notification_adapter(list, getApplicationContext());
        recview.setAdapter(adapter);

        preparelist();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, Tabs.class);
        startActivity(intent);
        finish();
    }

    private void preparelist()
    {
        final DatabaseReference db = DBREF.child("Notification").child(Username).getRef();
        db.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot.exists()) {
                    notif = dataSnapshot.getValue(Notif.class);
                    list.add(notif);
                    adapter.notifyDataSetChanged();
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

}
