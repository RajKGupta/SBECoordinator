package com.example.rajk.leasingmanagers;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class session {
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context _context;
    int mode=0;
    String prefname="SESSION";
    private String is_olduser = "false";
    private String place = "";
    private String place_id ="";

    public session(Context context)
    {
        this._context=context;
        pref = _context.getSharedPreferences(prefname,mode);
        editor = pref.edit();
    }

    public void create_oldusersession(String place_get,String username)
    {
        editor.putString(is_olduser,"true");
        editor.putString(place,place_get);
        editor.putString(place_id,NewUser.hashMap.get(place_get));
        editor.putString("username",username);
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
