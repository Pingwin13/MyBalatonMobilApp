package com.example.mybalaton;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class AttractionModel {
    private String name;
    private String description;
    private int imageResource;

    public AttractionModel() {

    }

    public AttractionModel(String name, String description, int imageResource) {
        this.name = name;
        this.description = description;
        this.imageResource = imageResource;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getImageResource() {
        return imageResource;
    }

    public void setImageResource(int imageResource) {
        this.imageResource = imageResource;
    }
} 