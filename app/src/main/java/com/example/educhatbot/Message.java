package com.example.educhatbot;

public class Message {

    private String message;
    private boolean isUser;

    public Message() {
        // required for Firebase
    }

    public Message(String message, boolean isUser) {
        this.message = message;
        this.isUser = isUser;
    }

    public String getMessage() {
        return message;
    }

    public boolean isUser() {
        return isUser;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}