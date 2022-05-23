package com.example.cse110finalproject;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;
import androidx.lifecycle.AndroidViewModel;

import java.util.List;

public class SearchViewModel extends AndroidViewModel {
    private List<Places> searchItems;
    private List<Places> queryItems;
    final SearchPlacesDao searchPlacesDao;
    @VisibleForTesting
    final SearchDatabase db;

    public SearchViewModel(@NonNull Application application) {
        super(application);
        Context context = getApplication().getApplicationContext();
        db = SearchDatabase.getSingleton(context);
        searchPlacesDao = db.searchPlacesDao();
    }

    public List<Places> getAllPlaces() {
        loadAllAnimals();
        return searchItems;
    }

    private void loadAllAnimals() {
        searchItems = searchPlacesDao.getAllPlaces();
    }

    public List<Places> loadSearchResult(String keyword){
        if(keyword.length()==0){
            searchItems = searchPlacesDao.getAllPlaces();
        }
        else {
            searchItems = searchPlacesDao.getSearchResult(keyword + "%");
        }
        return searchItems;
    }

    public void updateCheckbox(Places places) {
        places.checked = !places.checked;
        searchPlacesDao.update(places);
    }


}
