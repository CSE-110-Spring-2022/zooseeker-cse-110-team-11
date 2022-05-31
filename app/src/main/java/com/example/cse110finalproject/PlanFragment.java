package com.example.cse110finalproject;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.jgrapht.Graph;
import org.jgrapht.GraphPath;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PlanFragment extends Fragment {

    public RecyclerView recyclerView;
    PlanViewModel viewModel;
    PlanListAdapter adapter;
    private Runnable onAllClearClicked;
    private TextView counter;
    private Map<String, Exhibit> exhibitMap;
    private List<Exhibit> unvisitedExhbits;
    private Map<String, String> streetIdMap;
    private Graph<String, IdentifiedWeightedEdge> graph;
    private Map<String, List<Exhibit>> exhibitGroupsWithChildren;

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
        adapter.setDeletePlannedPlace(viewModel::deletePlaces);

        Button clearAll = rootView.findViewById(R.id.all_clr_bttn);
        clearAll.setOnClickListener(view1 -> {
            viewModel.deleteAllPlaces();
            adapter.notifyDataSetChanged();
        });

        //Load in only the planned exhibits
        List<Places> placesList = viewModel.getPlannedPlaces();

        //Load list of exhibits from new json
        Context context = getContext();
        Reader exhibitsReader = null;
        Reader trailsReader = null;
        try {
            exhibitsReader = new InputStreamReader(context.getAssets().open("exhibit_info.json"));
            trailsReader = new InputStreamReader(context.getAssets().open("trail_info.json"));
        } catch (IOException e) {
            throw new RuntimeException("Unable to load data for prepopulation!");
        }

        List<Exhibit> exhibitList = Exhibit.fromJson(exhibitsReader);
        exhibitMap = exhibitList.stream().collect(Collectors.toMap(exhibit -> exhibit.id, exhibit -> exhibit));
        //Convert places to exhibits
        unvisitedExhbits = DirectionsFragment.getIdsListFromPlacesList(placesList).stream().map(id-> exhibitMap.get(id)).collect(Collectors.toList());

        //We need this in order to get the street names from the edge_ids
        streetIdMap = ZooData.loadEdgeIdToStreetJSON(context, "trail_info.json");
        graph = ZooData.loadZooGraphJSON(context, "zoo_graph.json");

        List<String> unvisited;
        List<GraphPath<String, IdentifiedWeightedEdge>> fullPath = new ArrayList<>();
        String current = "entrance_exit_gate";
        exhibitGroupsWithChildren =new HashMap<>();
        unvisitedExhbits=DirectionsFragment.groupTogetherExhibits(unvisitedExhbits, exhibitMap, exhibitGroupsWithChildren);
        unvisited=DirectionsFragment.getIdsListFromExhibits(unvisitedExhbits);
        while(unvisited.size() > 0) {
            PathCalculator calculator = new PathCalculator(graph, current, unvisited);
            GraphPath<String, IdentifiedWeightedEdge> sp = calculator.smallestPath();
            fullPath.add(sp);
            unvisited.remove(sp.getEndVertex());
            current = sp.getEndVertex();
        }
        unvisited.add("entrance_exit_gate");
        PathCalculator calculator = new PathCalculator(graph, current, unvisited);
        GraphPath<String, IdentifiedWeightedEdge> sp = calculator.smallestPath();
        fullPath.add(sp);
        unvisited.remove(sp.getEndVertex());




        Map<String, String> exhibitToStreet = new HashMap<>();
        Map<Exhibit, Integer> exhibitToDistanceMap = new HashMap<>();

        //Gets the map of exhibit keys to their street string for value to the key
        exhibitToStreet = getStreetFromExhibit(fullPath, placesList, streetIdMap);
        //Gets the map of exhibit keys to their distance from the entrance along the path

        Map<String, Places> placesMap = placesList.stream().collect(Collectors.toMap(places -> places.id_name, places -> places));
        exhibitToDistanceMap = getDistanceFromExhibit(fullPath, exhibitMap);

        List<PlacesWithDistance> placesdispList = convertMapToExhibandDist(exhibitToDistanceMap);



        recyclerView = rootView.findViewById(R.id.plan_items);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        //Get Live Size from the viewmodel
        LiveData<Integer> liveSize = viewModel.placesCount;
        //Everytime the number of planned exhibits changes, we update the textview
        liveSize.observe(this.getViewLifecycleOwner(), num -> setSizeText(num));


        //Set the counter that shows the num of planned exhibits
        counter = rootView.findViewById(R.id.num_exhibits_textview);
        counter.setText(String.valueOf(adapter.getItemCount()));


        adapter.setSearchItem(placesdispList);

    }

    static List<PlacesWithDistance> convertMapToExhibandDist(Map<Exhibit, Integer> convertMap) {
        return convertMap.entrySet().stream().map(stringExhibitEntry -> {
            Exhibit key = stringExhibitEntry.getKey();
            int value = stringExhibitEntry.getValue();
            return new PlacesWithDistance(
                    key,
                    value
            );
        }).collect(Collectors.toList());

    }

    void setSizeText(Integer num) {
        counter.setText(String.valueOf(num));
    }

    public Map<String, String> getStreetFromExhibit(List<GraphPath<String, IdentifiedWeightedEdge>> fullPath, List<Places> planned, Map<String, String> streetIdMap){
        Map<String, String> bank = new HashMap<>();
        for(int i = 0; i < fullPath.size(); i++) {
            if(planned.contains(fullPath.get(i).getEndVertex())) {
                List<IdentifiedWeightedEdge> edges = fullPath.get(i).getEdgeList();
                IdentifiedWeightedEdge lastEdge = edges.get(edges.size()-1);
                bank.put(fullPath.get(i).getEndVertex(), streetIdMap.get(lastEdge.getId()));
            }
        }
        return bank;
    }

    public Map<Exhibit, Integer> getDistanceFromExhibit(List<GraphPath<String, IdentifiedWeightedEdge>> fullPath, Map<String, Exhibit> exhibitMap) {
        Map<Exhibit, Integer> bank = new HashMap<>();
        int total = 0;
        for(int i = 0; i < fullPath.size(); i++) {
            total += fullPath.get(i).getWeight();
            bank.put(exhibitMap.get(fullPath.get(i).getEndVertex()), total);
        }

        return bank;
    }
}