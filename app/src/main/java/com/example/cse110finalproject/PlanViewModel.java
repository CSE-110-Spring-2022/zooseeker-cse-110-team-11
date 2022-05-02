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
    private LiveData<List<VertexInfo>> searchItems;
    private final SearchPlacesDao searchPlacesDao;

    public PlanViewModel(@NonNull Application application) {
        super(application);
        Context context = getApplication().getApplicationContext();
        SearchDatabase db = SearchDatabase.getSingleton(context);
        searchPlacesDao = db.searchItemDao();
    }

    public LiveData<List<VertexInfo>> getSearchItems() {
        if (searchItems == null) {
            //TODO database
            //loadUsers();


            VertexInfo[] example_array = {MainActivity.exhibitsList.get(1)};
            List<VertexInfo> example_arrlst = new ArrayList<VertexInfo>(Arrays.asList(example_array));
            searchItems = new MutableLiveData<List<VertexInfo>>(example_arrlst);
        }
        return searchItems;
    }

    //TODO Implement the database
    //private void loadUsers() {
    //    searchItems = searchItemDao.getAllLive();
    //}
}
