package com.example.cse110finalproject;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import java.util.List;

public class SearchFragment extends Fragment {
    public RecyclerView recyclerView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView =
                (ViewGroup) inflater.inflate(R.layout.fragment_search, container, false);

        List<ZooData.VertexInfo> vertices = ZooData.loadVertexToListJSON(getContext(), "sample_node_info.json");
//        List<Places> places = Places
//                .convertVertexListToPlaces(vertices);
//        Log.d("SearchActivity", places.toString());

        SearchViewModel viewModel = new ViewModelProvider(this)
                .get(SearchViewModel.class);

        AnimalListAdapter adapter = new AnimalListAdapter();
        viewModel.getSearchItems().observe(getViewLifecycleOwner(), adapter::setSearchItem);

        recyclerView = rootView.findViewById(R.id.animal_items);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        adapter.setSearchItem(Places.convertVertexListToPlaces(vertices));

//        RelativeLayout rootView = (RelativeLayout) inflater.inflate(R.layout.fragment_search, container, false);
        Button buttonSearch = (Button) rootView.findViewById(R.id.add_search_btn);
        buttonSearch.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                EditText text = (EditText)getView().findViewById(R.id.add_search_text);
                String content = text.getText().toString();
                List<Places> searchResult = viewModel.loadSearchResult(content);
            }
        });

        // Inflate the layout for this fragment
        return rootView;


    }


}