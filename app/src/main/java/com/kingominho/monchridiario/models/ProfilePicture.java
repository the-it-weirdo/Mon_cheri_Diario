package com.kingominho.monchridiario.models;

import com.google.firebase.database.Exclude;

public class ProfilePicture {

    private String user_id;
    private String imageUrl;
    private  String imageKey;

    public ProfilePicture()
    {

    }

    public ProfilePicture(String user_id, String imageUrl) {
        this.user_id = user_id;
        this.imageUrl = imageUrl;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    @Exclude
    public String getImageKey() {
        return imageKey;
    }

    @Exclude
    public void setImageKey(String imageKey) {
        this.imageKey = imageKey;
    }
}
