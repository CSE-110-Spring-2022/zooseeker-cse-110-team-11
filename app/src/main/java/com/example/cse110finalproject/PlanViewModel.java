package com.example.cse110finalproject;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;
import androidx.lifecycle.AndroidViewModel;

import java.util.List;

public class PlanViewModel extends AndroidViewModel {
    @VisibleForTesting
    public final SearchPlacesDao searchPlacesDao;
    private List<Places> plannedPlacesList;
    @VisibleForTesting
    final SearchDatabase db;


    public PlanViewModel(@NonNull Application application) {
        super(application);
        Context context = getApplication().getApplicationContext();
        db = SearchDatabase.getSingleton(context);
        searchPlacesDao = db.searchPlacesDao();
    }

    public List<Places> getPlannedPlaces() {
        if (plannedPlacesList == null) {
            loadPlans();
        }
        return plannedPlacesList;
    }

    private void loadPlans() {
        plannedPlacesList = searchPlacesDao.getPlannedPlaces();
    }
}
