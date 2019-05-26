package com.nsromapa.hasay;

public class MessagesObjects {
    private String time;
    private String from;
    private String message;

    public MessagesObjects(String time, String from, String message) {
        this.time = time;
        this.from = from;
        this.message = message;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
