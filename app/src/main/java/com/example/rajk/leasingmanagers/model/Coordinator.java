package com.example.rajk.leasingmanagers.model;

/**
 * Created by RajK on 15-07-2017.
 */

public class Coordinator {
    private String name,username,password;

    public Coordinator() {
    }

    public Coordinator(String name, String username, String password) {
        this.name = name;
        this.username = username;
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
