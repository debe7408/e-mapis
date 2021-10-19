package com.vu.emapis;

import com.google.gson.annotations.SerializedName;

public class Post {

    @SerializedName("task")
    private String text;

    private String due;
    private int userId;
    private boolean done;

    public Post(String text, String due, boolean done) {
        this.text = text;
        this.due = due;
        this.done = done;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getDue() {
        return due;
    }

    public void setDue(String due) {
        this.due = due;
    }
}
