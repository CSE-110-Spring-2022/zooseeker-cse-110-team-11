package com.example.cse110finalproject;

import android.app.Activity;
import android.content.Context;
import android.widget.Button;
import android.widget.EditText;

import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Lifecycle;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import static org.junit.Assert.*;


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
    public void testSearchResult(){

        String search = "Gorillas";


    }
}
