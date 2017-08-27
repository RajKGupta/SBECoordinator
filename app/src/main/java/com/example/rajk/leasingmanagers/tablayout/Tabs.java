package com.example.rajk.leasingmanagers.tablayout;

import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Parcelable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.widget.FrameLayout;
import com.example.rajk.leasingmanagers.CoordinatorLogin.CoordinatorSession;
import com.example.rajk.leasingmanagers.CoordinatorLogin.CoordinatorSignUp;
import com.example.rajk.leasingmanagers.CoordinatorLogin.coordinatorLogin;
import com.example.rajk.leasingmanagers.R;
import com.example.rajk.leasingmanagers.Splash;
import com.example.rajk.leasingmanagers.drawer;
import com.example.rajk.leasingmanagers.employee.Emp_add;
import com.example.rajk.leasingmanagers.helper.MarshmallowPermissions;
import com.example.rajk.leasingmanagers.model.Coordinator;

import static com.example.rajk.leasingmanagers.LeasingManagers.AppName;

public class Tabs extends drawer implements TabLayout.OnTabSelectedListener{

    private TabLayout tab;
    private ViewPager vpager;
    int page;
    CoordinatorSession session;
    private MarshmallowPermissions marshmallowPermissions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FrameLayout frame = (FrameLayout)findViewById(R.id.frame);
        getLayoutInflater().inflate(R.layout.activity_tabs, frame);

        marshmallowPermissions = new MarshmallowPermissions(this);
        if(!marshmallowPermissions.checkPermissionForCamera())
            marshmallowPermissions.requestPermissionForCamera();
        if(!marshmallowPermissions.checkPermissionForExternalStorage())
            marshmallowPermissions.requestPermissionForExternalStorage();
        if(!marshmallowPermissions.checkPermissionForLocations())
            marshmallowPermissions.requestPermissionForLocations();

        session = new CoordinatorSession(getApplicationContext());
        if(session.get_ShortCutInstalled()==false)
        {
            createShortCut();
        }
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
    public void onBackPressed() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to exit?")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, int id) {
                        Tabs.super.onBackPressed();
                    }


                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();

    }
    public void createShortCut(){
        Intent shortcutintent = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
        shortcutintent.putExtra("duplicate", false);
        shortcutintent.putExtra(Intent.EXTRA_SHORTCUT_NAME, getString(R.string.app_name));
        Parcelable icon = Intent.ShortcutIconResource.fromContext(getApplicationContext(), R.mipmap.ic_launcher);
        shortcutintent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, icon);
        shortcutintent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, new Intent(getApplicationContext(), coordinatorLogin.class));
        sendBroadcast(shortcutintent);
        session.set_ShortCutInstalled();

    }

}
