package com.example.cse110finalproject;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface SearchPlacesDao {
    @Insert
    long insert(Places searchListItem);

    @Query("SELECT * FROM `search_places` WHERE `id`=:id")
    Places get(long id);

    @Query("SELECT * FROM `search_places` WHERE `kind`=")
    List<Places> getAll();

    @Update
    int update(Places places);

    @Delete
    int delete(Places places);

    @Insert
    List<Long> insertAll(List<Places> places);

    @Query("SELECT * FROM `search_places` ORDER BY `order`")
    LiveData<List<Places>> getAllLive();
}
