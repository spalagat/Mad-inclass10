package com.example.in_class_10;

import java.io.Serializable;

public class User implements Serializable {
    String text,user_id,id;

    public User() {
    }

    public User(String text, String user_id, String id) {
        this.text = text;
        this.user_id = user_id;
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "User{" +
                "text='" + text + '\'' +
                ", user_id='" + user_id + '\'' +
                ", id='" + id + '\'' +
                '}';
    }
}
