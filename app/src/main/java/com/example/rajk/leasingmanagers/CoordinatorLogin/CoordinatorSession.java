package com.example.rajk.leasingmanagers.CoordinatorLogin;

import android.content.Context;
import android.content.SharedPreferences;

public class CoordinatorSession
{
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context _context;
    int mode=0;
    String prefname="SESSION";
    private String is_loggedin = "is_loggedin";
    private String username = "username";

    public CoordinatorSession(Context context)
    {
        this._context=context;
        pref = _context.getSharedPreferences(prefname,mode);
        editor = pref.edit();
    }

    public void create_oldusersession(String username_get,String name, String contact, String address)
    {
        editor.putBoolean(is_loggedin,true);
        editor.putString(username,username_get);
        editor.putString("designation","coordinator");
        editor.putString("name",name);
        editor.putString("contact",contact);
        editor.putString("address",address);
        editor.commit();
    }

    public void edit_oldusersession(String name, String contact, String address)
    {
        editor.putBoolean(is_loggedin,true);
        editor.putString("name",name);
        editor.putString("contact",contact);
        editor.putString("address",address);
        editor.commit();
    }

    public Boolean isolduser()
    {
        return pref.getBoolean(is_loggedin,false);
    }

    public String getUsername()
    {
        return pref.getString(username,"");
    }

    public String getName(){return pref.getString("name","");}

    public String getContact (){return pref.getString("contact","");}

    public String getAddress(){return pref.getString("address","");}

    public void clearoldusersession()
    {
        editor.clear();
        editor.commit();
    }
}
