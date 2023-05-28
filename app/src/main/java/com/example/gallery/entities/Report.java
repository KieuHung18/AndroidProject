package com.example.gallery.entities;

public class Report {
    private boolean solved;
    private String name,description,artWorkId,userId;

    public boolean isSolved() {
        return solved;
    }

    public void setSolved(boolean solved) {
        this.solved = solved;
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

    public String getArtWorkId() {
        return artWorkId;
    }

    public void setArtWorkId(String artWorkId) {
        this.artWorkId = artWorkId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
