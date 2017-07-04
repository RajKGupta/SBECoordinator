package com.example.rajk.leasingmanagers.model;

/**
 * Created by RajK on 20-06-2017.
 */

public class ChatListModel {
    private String name,profpic,userkey,dbTableKey;
            private int color=-1;

    public ChatListModel() {
    }

    public String getDbTableKey() {
        return dbTableKey;
    }

    public void setDbTableKey(String dbTableKey) {
        this.dbTableKey = dbTableKey;
    }

    public ChatListModel(String name, String profpic, String userkey, String dbTableKey, int color) {
        this.name = name;
        this.profpic = profpic;
        this.userkey = userkey;
        this.dbTableKey = dbTableKey;
        this.color = color;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProfpic() {
        return profpic;
    }

    public void setProfpic(String profpic) {
        this.profpic = profpic;
    }

    public String getUserkey() {
        return userkey;
    }

    public void setUserkey(String userkey) {
        this.userkey = userkey;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }
}
