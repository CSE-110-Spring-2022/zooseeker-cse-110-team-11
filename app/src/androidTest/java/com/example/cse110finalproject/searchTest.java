package com.example.cse110finalproject;

import android.content.Context;

import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;

import org.junit.Before;
import org.junit.Test;

import java.util.List;

public class searchTest {

    SearchDatabase db;
    SearchPlacesDao dao;
    @Before
    public void resetDatabase() {
        Context context = ApplicationProvider.getApplicationContext();
        db = Room.inMemoryDatabaseBuilder(context, SearchDatabase.class)
                .allowMainThreadQueries()
                .build();
        SearchDatabase.injectTestDatabase(db);

        List<ZooData.VertexInfo> vertex = ZooData.loadVertexToListJSON(context,"sample_node_info.json");
        List<Places> places = Places.convertVertexListToPlaces(vertex);
        dao = db.searchPlacesDao();
        dao.insertAll(places);
    }

    @Test
    public void searchData(){
        String search = "Gorillas";
    }
}
