package com.example.cse110finalproject;

import static org.junit.Assert.assertEquals;
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
        Places place1 = new Places("Pizza time", ZooData.VertexInfo.Kind.EXHIBIT, false, "Pizza", "mammalsrat");
        Places place2 = new Places("Photos of Spider-Man", ZooData.VertexInfo.Kind.EXHIBIT, false, "Spider-Man", "spiderkiller");

        long id1 = dao.insert(place1);
        long id2 = dao.insert(place2);

        assertNotEquals(id1, id2);
    }

    @Test
    public void testGet() {
        Places place1 = new Places("Pizza time", ZooData.VertexInfo.Kind.EXHIBIT, false, "Pizza","mammals");
        long id = dao.insert(place1);

        Places item = dao.get(id);
        assertEquals(id, item.id);
        assertEquals(place1.name, item.name);
        assertEquals(place1.checked, item.checked);
        assertEquals(place1.kind, item.kind);
        assertEquals(place1.tags, item.tags);
    }





}