package com.example.cse110finalproject;

import androidx.room.PrimaryKey;

public class EdgeDispInfo {
    @PrimaryKey(autoGenerate = true)
    public long id;

    public String start;
    public String end;
    public String streetName;
    public String distance;

    public EdgeDispInfo(String start, String end, String streetName, String distance) {
        this.start = start;
        this.end = end;
        this.streetName = streetName;
        this.distance = distance;
    }
}
