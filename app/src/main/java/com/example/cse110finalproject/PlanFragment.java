package com.example.cse110finalproject;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PlanFragment extends Fragment {

    public RecyclerView recyclerView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_plan, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        View rootView = (ViewGroup) getView();
//        List<SearchItem> searches = SearchItem.loadJSON(this,"demo.json");
//        Log.d("SearchActivity", searches.toString());

        PlanViewModel viewModel = new ViewModelProvider(this)
                .get(PlanViewModel.class);

        PlanListAdapter adapter = new PlanListAdapter();
        if(getViewLifecycleOwner()==null)
            Log.d("confustion", "what the fuck");
        adapter.setSearchItem(viewModel.getSearchItems());

        recyclerView = rootView.findViewById(R.id.plan_items);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

    }
}