package com.example.rajk.leasingmanagers.tablayout;

import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.rajk.leasingmanagers.R;

public class Tabs extends AppCompatActivity implements TabLayout.OnTabSelectedListener{

    private TabLayout tab;
    private ViewPager vpager;
    String page;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tabs);

        if(getIntent().getExtras()!=null)
            page = getIntent().getStringExtra("page");
        else
            page = "0";

        tab = (TabLayout) findViewById(R.id.tabLayout);

        tab.addTab(tab.newTab().setText("Customer"));
        tab.addTab(tab.newTab().setText("Task"));
        tab.addTab(tab.newTab().setText("Employee"));
        tab.setTabGravity(TabLayout.GRAVITY_FILL);

        vpager = (ViewPager) findViewById(R.id.pager);

        pager adapter = new pager(getSupportFragmentManager(), tab.getTabCount());

        vpager.setAdapter(adapter);

        tab.setOnTabSelectedListener(this);
        vpager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tab));

        vpager.setCurrentItem(Integer.parseInt(page));
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
}
