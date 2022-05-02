package com.example.cse110finalproject;

import static org.junit.Assert.assertNotEquals;

import android.content.Context;

import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

@RunWith(AndroidJUnit4.class)
public class SearchDatabaseTest {
    private SearchPlacesDao dao;
    private SearchDatabase db;

    @Before
    public void createDb() {
        Context context = ApplicationProvider.getApplicationContext();
        db = Room.inMemoryDatabaseBuilder(context, SearchDatabase.class)
                .allowMainThreadQueries()
                .build();
        dao = db.searchPlacesDao();
    }

    @After
    public void closeDb() throws IOException {
        db.close();
    }

    @Test
    public void testInsert() {
        Places place1 = new Places("Pizza time", ZooData.VertexInfo.Kind.EXHIBIT, false, "Pizza");
        Places place2 = new Places("Photos of Spider-Man", ZooData.VertexInfo.Kind.EXHIBIT, false, "Spider-Man");

        long id1 = dao.insert(place1);
        long id2 = dao.insert(place2);

        assertNotEquals(id1, id2);
    }



}