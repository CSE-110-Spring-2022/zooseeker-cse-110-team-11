package com.example.cse110finalproject;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.jgrapht.Graph;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PlanFragment extends Fragment {

    public RecyclerView recyclerView;
    PlanViewModel viewModel;
    PlanListAdapter adapter;

    /**
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return Returns the view of the fragment, not currently visible
     * not all components are initialized at this point
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_plan, container, false);
    }

    /**
     * @param view
     * @param savedInstanceState
     *
     * In this method, the view is created and we can access all components contained in fragment
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        View rootView = (ViewGroup) getView();
//        List<SearchItem> searches = SearchItem.loadJSON(this,"demo.json");
//        Log.d("SearchActivity", searches.toString());

        viewModel = new ViewModelProvider(this)
                .get(PlanViewModel.class);

        adapter = new PlanListAdapter();

        //Load in only the planned exhibits
        List<Places> placesList = viewModel.getPlannedPlaces();
        adapter.setSearchItem(placesList);

        recyclerView = rootView.findViewById(R.id.plan_items);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        //Set the cunter that shows the num of planned exhibits
        TextView counter = rootView.findViewById(R.id.num_exhibits_textview);
        counter.setText(String.valueOf(adapter.getItemCount()));
    }
}