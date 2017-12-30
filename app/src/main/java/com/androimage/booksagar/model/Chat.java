package com.androimage.booksagar.model;

/**
 * Created by subha on 9/24/2016.
 */

public class Chat {
    private String name, msg, id, timestamp;

    public Chat() {

    }

    public Chat(String timestamp, String id, String msg, String name) {
        this.timestamp = timestamp;
        this.id = id;
        this.msg = msg;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
