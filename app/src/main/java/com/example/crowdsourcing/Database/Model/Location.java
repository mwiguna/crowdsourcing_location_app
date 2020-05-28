package com.example.crowdsourcing.Database.Model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Location {
    @PrimaryKey(autoGenerate = true)
    @NonNull()
    public int id;

    @ColumnInfo(name = "lat")
    public String lat;

    @ColumnInfo(name = "lng")
    public String lng;

    @ColumnInfo(name = "time")
    public String time;

    public Location(String lat, String lng, String time) {
        this.lat = lat;
        this.lng = lng;
        this.time = time;
    }

    public int getId() {
        return id;
    }

    public String getLat() {
        return lat;
    }

    public String getLng() {
        return lng;
    }

    public String getTime() {
        return time;
    }

}