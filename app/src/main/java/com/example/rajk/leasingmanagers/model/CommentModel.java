package com.example.rajk.leasingmanagers.model;

/**
 * Created by RajK on 16-05-2017.
 */

public class Comment {
    private String comment_string,id,discussion_id;

    public String getComment_string() {
        return comment_string;
    }

    public void setComment_string(String comment_string) {
        this.comment_string = comment_string;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDiscussion_id() {
        return discussion_id;
    }

    public void setDiscussion_id(String discussion_id) {
        this.discussion_id = discussion_id;
    }

    public Comment() {

    }
}
