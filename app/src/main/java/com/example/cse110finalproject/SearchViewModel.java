package com.example.cse110finalproject;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import java.util.List;

public class SearchViewModel extends AndroidViewModel {
    private List<Places> searchItems;
    private List<Places> queryItems;
    private final SearchPlacesDao searchPlacesDao;

    public SearchViewModel(@NonNull Application application) {
        super(application);
        Context context = getApplication().getApplicationContext();
        SearchDatabase db = SearchDatabase.getSingleton(context);
        searchPlacesDao = db.searchPlacesDao();
    }

    public List<Places> getSearchItems() {
        if (searchItems == null) {
            loadAllAnimals();
        }
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
