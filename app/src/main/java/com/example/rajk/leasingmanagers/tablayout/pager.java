package com.example.rajk.leasingmanagers.tablayout;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.example.rajk.leasingmanagers.MainViews.TaskHome;
import com.example.rajk.leasingmanagers.customer.Cust_Tab;
import com.example.rajk.leasingmanagers.employee.Emp_Tab;

public class pager extends FragmentStatePagerAdapter
{
    int tabCount;

    public pager(FragmentManager fm, int tabCount)
    {
        super(fm);
        this.tabCount=tabCount;
    }

    @Override
    public Fragment getItem(int position)
    {
        switch (position)
        {
            case 0:
                Cust_Tab a = new Cust_Tab();
                return a;
            case 1:
                TaskHome b = new TaskHome();
                return b;
            case 2:
                Emp_Tab c = new Emp_Tab();
                return c;
            default:
                return null;
        }
    }

    @Override
    public int getCount()
    {
        return tabCount;
    }

}