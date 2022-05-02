package com.example.cse110finalproject;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class SearchViewModel extends AndroidViewModel {
    private LiveData<List<Places>> searchItems;
    private final SearchItemDao searchItemDao;

    public SearchViewModel(@NonNull Application application) {
        super(application);
        Context context = getApplication().getApplicationContext();
        SearchDatabase db = SearchDatabase.getSingleton(context);
        searchItemDao = db.searchItemDao();
    }

    public LiveData<List<Places>> getSearchItems() {
        if (searchItems == null) {
            loadUsers();
        }
        return searchItems;
    }

    private void loadUsers() {
        searchItems = searchItemDao.getAllLive();
    }
}
