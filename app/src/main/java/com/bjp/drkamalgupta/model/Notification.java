package com.bjp.drkamalgupta.model;

import org.json.JSONObject;

public class Notification {
    String title, message, image_url, timestamp;
    JSONObject payload;
    
    public Notification(){
    
    }
    
    public Notification (String title, String message, String image_url, String timestamp, JSONObject payload) {
        this.title = title;
        this.message = message;
        this.image_url = image_url;
        this.timestamp = timestamp;
        this.payload = payload;
    }
    
    public String getTitle () {
        return title;
    }
    
    public void setTitle (String title) {
        this.title = title;
    }
    
    public String getMessage () {
        return message;
    }
    
    public void setMessage (String message) {
        this.message = message;
    }
    
    public String getImage_url () {
        return image_url;
    }
    
    public void setImage_url (String image_url) {
        this.image_url = image_url;
    }
    
    public String getTimestamp () {
        return timestamp;
    }
    
    public void setTimestamp (String timestamp) {
        this.timestamp = timestamp;
    }
    
    public JSONObject getPayload () {
        return payload;
    }
    
    public void setPayload (JSONObject payload) {
        this.payload = payload;
    }
}
