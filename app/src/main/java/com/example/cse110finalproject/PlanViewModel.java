package com.example.cse110finalproject;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.example.cse110finalproject.ZooData.VertexInfo;

public class PlanViewModel extends AndroidViewModel {
    private LiveData<List<Places>> searchItems;
    private final SearchPlacesDao searchPlacesDao;

    public PlanViewModel(@NonNull Application application) {
        super(application);
        Context context = getApplication().getApplicationContext();
        SearchDatabase db = SearchDatabase.getSingleton(context);
        searchPlacesDao = db.searchPlacesDao();
    }

    public LiveData<List<Places>> getSearchItems() {
        if (searchItems == null) {
            //TODO: Load this shit from database
            //loadPlans();
            loadPlans();
        }
        return searchItems;
    }

    //TODO Implement the database
    private void loadPlans() {
        searchItems = searchPlacesDao.getPlannedItemsLive();
    }
}
