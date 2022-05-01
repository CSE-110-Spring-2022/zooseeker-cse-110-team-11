package com.example.cse110finalproject;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
        ViewGroup rootView =
                (ViewGroup) inflater.inflate(R.layout.fragment_plan, container, false);
//        List<SearchItem> searches = SearchItem.loadJSON(this,"demo.json");
//        Log.d("SearchActivity", searches.toString());

        PlanViewModel viewModel = new ViewModelProvider(this)
                .get(PlanViewModel.class);

        PlanListAdapter adapter = new PlanListAdapter();
        viewModel.getSearchItems().observe(getViewLifecycleOwner(), adapter::setSearchItem);

        recyclerView = rootView.findViewById(R.id.plan_items);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        ZooData.VertexInfo[] example_array = {MainActivity.exhibitsList.get(1)};
        List<ZooData.VertexInfo> example_arrlst = new ArrayList<ZooData.VertexInfo>(Arrays.asList(example_array));
        adapter.setSearchItem(example_arrlst);
        // Inflate the layout for this fragment
        return rootView;
    }
}