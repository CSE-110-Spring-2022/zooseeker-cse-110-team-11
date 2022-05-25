package com.example.cse110finalproject;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

public class PlanViewModel extends AndroidViewModel {
    @VisibleForTesting
    public final SearchPlacesDao searchPlacesDao;
    private List<Places> plannedPlacesList;
    @VisibleForTesting
    final SearchDatabase db;
    public MutableLiveData<Integer> placesCount = new MutableLiveData<>();


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
        placesCount.setValue(plannedPlacesList.size());
        return plannedPlacesList;
    }

    //A method that would remove the planned item from the plan tab
    public void deletePlaces(Places places) {
        places.checked = false;
        plannedPlacesList.remove(places);
        searchPlacesDao.update(places);
        placesCount.setValue(plannedPlacesList.size());
    }

    public void deleteAllPlaces() {
        for(Places places : plannedPlacesList) {
            places.checked = false;
            searchPlacesDao.update(places);
        }
        plannedPlacesList.clear();
        placesCount.setValue(plannedPlacesList.size());
    }

    private void loadPlans() {
        plannedPlacesList = searchPlacesDao.getPlannedPlaces();
    }
}
