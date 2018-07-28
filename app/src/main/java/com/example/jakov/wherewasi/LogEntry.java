package com.example.jakov.wherewasi;

import android.graphics.Bitmap;

public class LogEntry {
    private String Timestamp;
    private String name;
    private String longitude;
    private String latitude;
    private Bitmap image;
    private String path;
    private String description;

    public LogEntry(String timestamp, String name, String longitude, String latitude, Bitmap img, String path, String description  ) {
        this.image = img;
        Timestamp = timestamp;
        this.name = name;
        this.longitude = longitude;
        this.latitude = latitude;
        this.path = path;
        this.description = description;
    }

    public String getTimestamp() {
        return Timestamp;
    }

    public String getName() {
        return name;
    }

    public String getLongitude() {
        return longitude;
    }

    public String getLatitude() {
        return latitude;
    }


    public void setTimestamp(String timestamp) {
        Timestamp = timestamp;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}