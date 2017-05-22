package com.example.rajk.leasingmanagers;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import java.util.HashMap;

public class session {
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context _context;
    int mode=0;

    String prefname="SESSION";
    private String is_olduser = "false";
    private String place = "";

    public session(Context context)
    {
        this._context=context;
        pref = _context.getSharedPreferences(prefname,mode);
        editor = pref.edit();
    }

    public void create_oldusersession(String place_get)
    {
        editor.putString(is_olduser,"true");
        editor.putString(place,place_get);
        editor.commit();
    }

    public String isolduser()
    {
        return pref.getString(is_olduser,"");
    }

    public String place()
    {
        return pref.getString(place,"");
    }

    public void clearoldusersession()
    {
        editor.clear();
        editor.commit();
    }
}
