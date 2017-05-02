package com.jason.chat;

/**
 * Created by Jason on 1/6/2017.
 */

public class Message {
    public String text;
    public String senderId;

    public Message(){
        text = "";
        senderId = "";
    }

    public Message(String text, String id) {
        this.text = text;
        this.senderId = id;
    }

}
