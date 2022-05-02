package com.example.cse110finalproject;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.util.List;
import java.util.concurrent.Executors;

@Database(entities = {Places.class}, version = 1)
public abstract class SearchDatabase extends RoomDatabase {
    private static SearchDatabase singleton = null;

    public abstract SearchPlacesDao searchItemDao();

    public synchronized static SearchDatabase getSingleton(Context context) {
        if(singleton == null) {
            singleton = SearchDatabase.makeDatabase(context);
        }
        return singleton;
    }

    private static SearchDatabase makeDatabase(Context context) {
        return Room.databaseBuilder(context, SearchDatabase.class, "search_app.db")
                .allowMainThreadQueries()
                .addCallback(new Callback() {
                    @Override
                    public void onCreate(@NonNull SupportSQLiteDatabase db) {
                        super.onCreate(db);
                        Executors.newSingleThreadScheduledExecutor().execute(() -> {
                            List<Places> searches = Places
                                    .loadJSON(context, "demo.json");
                            getSingleton(context).searchItemDao().insertAll(searches);
                        });
                    }
                })
                .build();
    }
}
