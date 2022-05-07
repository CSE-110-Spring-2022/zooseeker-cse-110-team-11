package com.example.cse110finalproject;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import java.util.List;

public class PlanViewModel extends AndroidViewModel {
    private List<Places> searchItems;
    private final SearchPlacesDao searchPlacesDao;

    public PlanViewModel(@NonNull Application application) {
        super(application);
        Context context = getApplication().getApplicationContext();
        SearchDatabase db = SearchDatabase.getSingleton(context);
        searchPlacesDao = db.searchPlacesDao();
    }

    public List<Places> getSearchItems() {
        if (searchItems == null) {
            //TODO: Load this shit from database
            //loadPlans();
            loadPlans();
        }
        return searchItems;
    }

    //TODO Implement the database
    private void loadPlans() {
        searchItems = searchPlacesDao.getPlannedPlaces();
    }
}
