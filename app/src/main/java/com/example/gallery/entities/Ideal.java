package com.example.gallery.entities;

import com.example.gallery.AddIdealActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

public class Ideal implements Serializable {
    private String id,description,name,userId,thumbnail;
    private int size;
    private boolean publish;
    @Override
    public String toString() {
        JSONObject ideal = new JSONObject();
        try {
            ideal.put("id", id);
            ideal.put("name", name);
            ideal.put("publish", publish);
            ideal.put("description", description);
            ideal.put("thumbnail",thumbnail);
            return ideal.toString();
        } catch (JSONException e) {
            e.printStackTrace();
            return "";
        }
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isPublish() {
        return publish;
    }

    public void setPublish(boolean publish) {
        this.publish = publish;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }
}
