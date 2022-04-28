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

@Entity(tableName = "search_items")
public class SearchItem {

    @PrimaryKey(autoGenerate = true)
    public long id;

    @NonNull
    public String text;
    public boolean completed;
    public int order;

    SearchItem(@NonNull String text, boolean completed, int order) {
        this.id = id;
        this.text = text;
        this.completed = completed;
        this.order = order;
    }

    @Override
    public String toString() {
        return "com.example.lab5.TodoListItem{" +
                "id=" + id +
                ", text='" + text + '\'' +
                ", completed=" + completed +
                ", order=" + order +
                '}';
    }

    public static List<SearchItem> loadJSON(Context context, String path){
        try{
            InputStream input = context.getAssets().open(path);
            Reader reader = new InputStreamReader(input);
            Gson gson = new Gson();
            Type type = new TypeToken<List<SearchItem>>(){}.getType();
            return gson.fromJson(reader,type);
        }
        catch(IOException e){
            e.printStackTrace();
            return Collections.emptyList();
        }

    }
}
