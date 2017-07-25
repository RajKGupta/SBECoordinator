package com.example.rajk.leasingmanagers.tablayout;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.example.rajk.leasingmanagers.CoordinatorLogin.CoordinatorSession;
import com.example.rajk.leasingmanagers.R;
import com.example.rajk.leasingmanagers.helper.MarshmallowPermissions;
import com.example.rajk.leasingmanagers.model.Coordinator;
import com.example.rajk.leasingmanagers.notification.NotificationActivity;

public class Tabs extends AppCompatActivity implements TabLayout.OnTabSelectedListener{

    private TabLayout tab;
    private ViewPager vpager;
    int page;
    CoordinatorSession session;
    private MarshmallowPermissions marshmallowPermissions;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tabs);
        marshmallowPermissions = new MarshmallowPermissions(this);
        if(!marshmallowPermissions.checkPermissionForCamera())
            marshmallowPermissions.requestPermissionForCamera();
        if(!marshmallowPermissions.checkPermissionForExternalStorage())
            marshmallowPermissions.requestPermissionForExternalStorage();
        if(!marshmallowPermissions.checkPermissionForLocations())
            marshmallowPermissions.requestPermissionForLocations();

        session = new CoordinatorSession(getApplicationContext());

        if(getIntent().getExtras()!=null)
            page = getIntent().getIntExtra("page",0);
        else
            page = 0;

        tab = (TabLayout) findViewById(R.id.tabLayout);

        tab.addTab(tab.newTab().setText("Customer"));
        tab.addTab(tab.newTab().setText("Employee"));
        tab.addTab(tab.newTab().setText("Chat"));

        tab.setTabGravity(TabLayout.GRAVITY_FILL);

        vpager = (ViewPager) findViewById(R.id.pager);

        pager adapter = new pager(getSupportFragmentManager(), tab.getTabCount());

        vpager.setAdapter(adapter);

        tab.setOnTabSelectedListener(this);
        vpager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tab));

        vpager.setCurrentItem(page);
        vpager.setOffscreenPageLimit(2);
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        vpager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.tabsmenu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.notif:
                Intent intent = new Intent(getApplicationContext(), NotificationActivity.class);
                startActivity(intent);
                break;
        }
        return true;
    }
}
