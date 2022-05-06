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
    long insert(Places places);

    @Insert
    List<Long> insertAll(List<Places> places);

    @Query("SELECT * FROM `search_places` WHERE `id`=:id")
    Places get(long id);

    @Query("SELECT * FROM `search_places` WHERE `kind`='exhibit'")
    List<Places> getAll();

    @Query("SELECT * FROM `search_places` WHERE `kind`='EXHIBIT' ORDER BY `id_name` ASC")
    LiveData<List<Places>> getSearchItemsLive();

    @Query("SELECT * FROM `search_places` WHERE `checked`=1")
    LiveData<List<Places>> getPlannedItemsLive();

    @Query("SELECT * FROM `search_places` WHERE `name` LIKE '%' + :keyword + '%' " )
    LiveData<List<Places>> getSearchResult(String keyword);

    @Update
    int update(Places places);

    @Delete
    int delete(Places places);
}
