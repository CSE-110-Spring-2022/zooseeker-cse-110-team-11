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
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

@Entity(tableName = "search_places")
public class Places {

    @PrimaryKey(autoGenerate = true)
    public long id;

    @NonNull
    public String id_name;
    public String kind;
    public boolean checked;
    public String name;



    Places(@NonNull String id_name, ZooData.VertexInfo.Kind kind, boolean checked, String name) {
        this.id_name = id_name;
        this.kind = kind.toString();
        this.checked = checked;
        this.name = name;
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





}
