package com.example.educhatbot;

import com.google.gson.annotations.SerializedName;

public class FreeChatResponse {

    @SerializedName("message")
    private String message;

    public String getMessage() {
        return message;
    }
}