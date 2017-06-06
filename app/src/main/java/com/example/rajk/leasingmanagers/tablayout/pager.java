package com.example.rajk.leasingmanagers.tablayout;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class pager extends FragmentStatePagerAdapter {
    int tabCount;

    public pager(FragmentManager fm, int tabCount)
    {
        super(fm);
        this.tabCount=tabCount;
    }

    @Override
    public Fragment getItem(int position) {

        //Fragment frag;
        switch (position) {
            case 0:
            case 1:
            case 2:
            default:
                return null;
        }
    }

    @Override
    public int getCount()
    {
        return tabCount;
    }

    /*@Override
    public CharSequence getPageTitle(int position) {
        //this is where you set the titles
        switch(position) {
            case 0:
                return "a1";
            case 1:
                return "a2";
            case 2:
                return "a3";
        }
        return null;
    }*/

}