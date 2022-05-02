package com.example.cse110finalproject;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

public class SearchFragment extends Fragment {

    public RecyclerView recyclerView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView =
                (ViewGroup) inflater.inflate(R.layout.fragment_search, container, false);
//        List<SearchItem> searches = SearchItem.loadJSON(this,"demo.json");
//        Log.d("SearchActivity", searches.toString());

        SearchViewModel viewModel = new ViewModelProvider(this)
                .get(SearchViewModel.class);

        AnimalListAdapter adapter = new AnimalListAdapter();
        viewModel.getSearchItems().observe(getViewLifecycleOwner(), adapter::setSearchItem);

        recyclerView = rootView.findViewById(R.id.animal_items);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        List<ZooData.VertexInfo> vertices = ZooData.loadVertexToListJSON(getContext(), "sample_node_info.json");
        adapter.setSearchItem(Places.convertVertexListToPlaces(vertices));
        // Inflate the layout for this fragment
        return rootView;
    }
}