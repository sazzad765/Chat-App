package com.team15.chatapp.Model;

public class Chat {

    private String sender;
    private String receiver;
    private String message;
    private String type;
    private boolean isSeen;

    public Chat(String sender, String receiver, String message, String type, boolean isSeen){
        this.sender = sender;
        this.receiver = receiver;
        this.message = message;
        this.type = type;
        this.isSeen = isSeen;
    }

    public Chat() {
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isSeen() {
        return isSeen;
    }

    public void setSeen(boolean seen) {
        isSeen = seen;
    }
}
