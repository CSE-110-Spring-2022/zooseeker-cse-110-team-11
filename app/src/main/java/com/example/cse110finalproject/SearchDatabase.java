package com.example.cse110finalproject;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.util.List;
import java.util.concurrent.Executors;

@Database(entities = {Places.class}, version = 1)
public abstract class SearchDatabase extends RoomDatabase {
    public static SearchDatabase singleton = null;

    public abstract SearchPlacesDao searchPlacesDao();

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
                            List<ZooData.VertexInfo> vertices = ZooData.loadVertexToListJSON(context, "sample_node_info.json");
                            List<Places> places = Places
                                    .convertVertexListToPlaces(vertices);
                            getSingleton(context).searchPlacesDao().insertAll(places);
                        });
                    }
                })
                .build();
    }
    @VisibleForTesting
    public static void injectTestDatabase(SearchDatabase testDatabase) {
        if (singleton != null) {
            singleton.close();
        }
        singleton = testDatabase;
    }
    @VisibleForTesting
    public static void releaseSingleton() {
        if (singleton != null) {
            synchronized (singleton) {
                singleton.close();
            }
        }
    }
}
