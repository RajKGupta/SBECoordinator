package com.example.rajk.leasingmanagers.model;

/**
 * Created by RajK on 16-05-2017.
 */

public class User {
    private String first_name,last_name;
    private int place_id;

    public User() {
    }

    public String getFirst_name() {

        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public int getPlace_id() {
        return place_id;
    }

    public void setPlace_id(int place_id) {
        this.place_id = place_id;
    }
}
