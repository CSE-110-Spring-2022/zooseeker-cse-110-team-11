package com.example.cse110finalproject;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

@Entity(tableName = "search_places")
public class Places {

    @PrimaryKey(autoGenerate = true)
    public long id;

    @NonNull
    public String id_name;
    public ZooData.VertexInfo.Kind kind;
    public boolean checked;
    public String name;
    public String tags;



    Places(@NonNull String id_name, ZooData.VertexInfo.Kind kind, boolean checked, String name, String tags) {
        this.id_name = id_name;
        this.kind = kind;
        this.checked = checked;
        this.name = name;
        this.tags = tags;
    }

    @Override
    public String toString() {
        return "Places{" +
                "id=" + id +
                ", id_name='" + id_name + '\'' +
                ", kind=" + kind +
                ", checked=" + checked +
                ", name='" + name + '\'' +
                '}';
    }

    public static List<Places> convertVertexListToPlaces(List<ZooData.VertexInfo> vertexList) {
        List<Places> placesList;
        placesList = vertexList.stream().map((ZooData.VertexInfo vertex)->{
            return new Places(vertex.id, vertex.kind, false, vertex.name, getTags(vertex.tags));
        }).collect(Collectors.toList());
        return placesList;
    }

    public String getName(){
        return this.name;
    }

    public static String getTags(List<String> tagList) {
        String tags = "";
        for(String tag : tagList) {
            tags += tag;
        }

        return tags;
    }
}