package com.kingominho.monchridiario.models;

public class AboutItem {

    private String title;
    private String description;
    private String link;

    public AboutItem(String title, String description, String link) {
        this.title = title;
        this.description = description;
        this.link = link;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getLink() {
        return link;
    }
}
