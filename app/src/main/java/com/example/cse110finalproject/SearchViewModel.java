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

public class SearchViewModel extends AndroidViewModel {
    private LiveData<List<Places>> searchItems;
    private final SearchPlacesDao searchPlacesDao;

    public SearchViewModel(@NonNull Application application) {
        super(application);
        Context context = getApplication().getApplicationContext();
        SearchDatabase db = SearchDatabase.getSingleton(context);
        searchPlacesDao = db.searchPlacesDao();
    }

    public LiveData<List<Places>> getSearchItems() {
        if (searchItems == null) {
            //loadUsers();
            ZooData.VertexInfo[] example_array = {MainActivity.exhibitsList.get(1)};
            List<ZooData.VertexInfo> example_arrlst = new ArrayList<ZooData.VertexInfo>(Arrays.asList(example_array));
            List<Places> places = Places.convertVertexListToPlaces(example_arrlst);
            searchItems = new MutableLiveData<List<Places>>(places);
        }
        return searchItems;
    }

//    private void loadUsers() {
//        searchItems = PlanViewModel.getSearchItems();
//    }
}
