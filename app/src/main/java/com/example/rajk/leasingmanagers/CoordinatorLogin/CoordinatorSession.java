package com.example.rajk.leasingmanagers.CoordinatorLogin;

import android.content.Context;
import android.content.SharedPreferences;

public class CoordinatorSession {
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

    public void create_oldusersession(String place_get,String place_ids,String username_get)
    {
        editor.putString(is_loggedin,"true");
        editor.putString(username,username_get);
        editor.putString("designation","coordinator");
        editor.commit();
    }

    public String isolduser()
    {
        return pref.getString(is_loggedin,"");
    }

    public String getUsername()
    {
        return pref.getString(username,"");
    }

    public void clearoldusersession()
    {
        editor.clear();
        editor.commit();
    }
}
