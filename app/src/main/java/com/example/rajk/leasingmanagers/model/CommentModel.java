package com.example.rajk.leasingmanagers.model;

/**
 * Created by RajK on 16-05-2017.
 */

public class CommentModel {
    private String commentString,sender,timestamp;

    public String getCommentString() {
        return commentString;
    }

    public void setCommentString(String commentString) {
        this.commentString = commentString;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public CommentModel() {

    }

    public CommentModel(String commentString, String sender, String timestamp) {
        this.commentString = commentString;
        this.sender = sender;
        this.timestamp = timestamp;
    }
}
