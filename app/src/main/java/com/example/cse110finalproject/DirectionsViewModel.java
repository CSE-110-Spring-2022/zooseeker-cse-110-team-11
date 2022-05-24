package com.example.cse110finalproject;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;
import androidx.lifecycle.AndroidViewModel;

import java.util.List;

public class DirectionsViewModel extends AndroidViewModel {
    @VisibleForTesting
    public final SearchPlacesDao searchPlacesDao;
    private List<Places> searchItems;
    @VisibleForTesting
    final SearchDatabase db;


    public DirectionsViewModel(@NonNull Application application) {
        super(application);
        Context context = getApplication().getApplicationContext();
        db = SearchDatabase.getSingleton(context);
        searchPlacesDao = db.searchPlacesDao();
    }

    public List<Places> getPlannedPlaces() {
        if (searchItems == null) {
            loadPlans();
        }
        return searchItems;
    }

    private void loadPlans() {
        searchItems = searchPlacesDao.getPlannedPlaces();
    }
}
