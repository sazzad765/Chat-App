package com.team15.chatapp.Model;

public class User {

    private String id;
    private String username;
    private String imageURL;
    private String status;
    private String userType;
    private String search;

    public User(String id, String username, String imageURL, String status ,String userType, String search) {
        this.id = id;
        this.username = username;
        this.imageURL = imageURL;
        this.status = status;
        this.userType = userType;
        this.search = search;
    }

    public User() {

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }
}
