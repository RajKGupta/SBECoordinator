package com.example.rajk.leasingmanagers;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rajk.leasingmanagers.CoordinatorLogin.CoordinatorSession;
import com.example.rajk.leasingmanagers.MyProfile.ContactCoordinator;
import com.example.rajk.leasingmanagers.MyProfile.MyProfile;
import com.example.rajk.leasingmanagers.MyProfile.phonebook;
import com.example.rajk.leasingmanagers.customer.Cust_details;
import com.example.rajk.leasingmanagers.model.Coordinator;
import com.example.rajk.leasingmanagers.notification.NotificationActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import static com.example.rajk.leasingmanagers.LeasingManagers.CustomerAppLink;
import static com.example.rajk.leasingmanagers.LeasingManagers.DBREF;

public class drawer extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    CoordinatorSession session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawer1);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        session = new CoordinatorSession(getApplicationContext());

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View header = navigationView.getHeaderView(0);

        TextView nav_name = (TextView) header.findViewById(R.id.nav_name);
        nav_name.setText(session.getName());
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer != null) {
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawers();
            } else {
                super.onBackPressed();
            }
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch (id) {
            case R.id.first:
                Intent intent2 = new Intent(getApplicationContext(), MyProfile.class);
                startActivity(intent2);
                finish();
                break;
            case R.id.second:
                Intent intent = new Intent(getApplicationContext(), ContactCoordinator.class);
                startActivity(intent);
                finish();
                break;
            case R.id.third:
                Intent intent1 = new Intent(getApplicationContext(), phonebook.class);
                startActivity(intent1);
                finish();
                break;
            case R.id.fourth:
                //TODO About the firm
                break;
            case R.id.fifth:
                final Intent smsIntent = new Intent(Intent.ACTION_SEND);
                DBREF.child("cal").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        CustomerAppLink = dataSnapshot.getValue(String.class);
                        String content = "Regards from Tanu Enterprises. Download the Tanu Enterprises App from "+CustomerAppLink;
                        smsIntent.setData(Uri.parse("smsto:"));
                        smsIntent.putExtra("address"  ,"");
                        smsIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Download the Tanu Enterprises App");
                        smsIntent.putExtra(android.content.Intent.EXTRA_TEXT,content );
                        smsIntent.setType("text/plain");
                        smsIntent.putExtra("sms_body"  , content);

                        try {
                            startActivity(Intent.createChooser(smsIntent,"Share Customer app via "));
                        }
                        catch (android.content.ActivityNotFoundException ex) {
                            Toast.makeText(drawer.this,
                                    "Your phone does not support this option. Contact manufacturer for details.", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawers();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.tabsmenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.notif:
                Intent intent = new Intent(getApplicationContext(), NotificationActivity.class);
                startActivity(intent);
                finish();
                break;
        }
        return true;
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

    }
}