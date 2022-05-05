package com.example.cse110finalproject;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SearchViewModel extends AndroidViewModel {
    private LiveData<List<Places>> searchItems;
    private LiveData<List<Places>> queryItems;
    private final SearchPlacesDao searchPlacesDao;

    public SearchViewModel(@NonNull Application application) {
        super(application);
        Context context = getApplication().getApplicationContext();
        SearchDatabase db = SearchDatabase.getSingleton(context);
        searchPlacesDao = db.searchPlacesDao();
    }

    public LiveData<List<Places>> getSearchItems() {
        if (searchItems == null) {
            loadSearchAnimals();
        }
        return searchItems;
    }

    private void loadSearchAnimals() {
        searchItems = searchPlacesDao.getSearchItemsLive();
    }

    public LiveData<List<Places>> loadSearchResult(String keyword){
        if(keyword.length()==0){
            queryItems = searchPlacesDao.getSearchItemsLive();
        }
        else {
            queryItems = searchPlacesDao.getSearchResult(keyword + "%");
        }
        return queryItems;
    }
}
