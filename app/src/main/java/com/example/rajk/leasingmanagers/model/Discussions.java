package com.example.rajk.leasingmanagers.model;

/**
 * Created by SoumyaAgarwal on 5/21/2017.
 */

public class Discussions {
    private String id,place_id;

    public Discussions(String id, String place_id)
    {
        this.id = id;
        this.place_id = place_id;
    }

    public Discussions() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPlace_id() {
        return place_id;
    }

    public void setPlace_id(String place_id) {
        this.place_id = place_id;
    }
}
