package com.example.cse110finalproject;

import android.content.Context;
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
import android.widget.Button;

import org.jgrapht.Graph;
import org.jgrapht.GraphPath;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


public class DirectionsFragment extends Fragment {

    private Map<String, String> streetIdMap;
    private Map<String, Places> placesIdMap;

    public RecyclerView recyclerView;
    DirectionsViewModel viewModel;
    DirectionsAdapter adapter;
    Places current;
    Places entranceExitPlace;
    private Graph<String, IdentifiedWeightedEdge> graph;
    List<Places> unvisited;


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
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_directions, container, false);
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
        Context context = getContext();

        viewModel = new ViewModelProvider(this)
                .get(DirectionsViewModel.class);

        adapter = new DirectionsAdapter();
        List<Places> plannedPlaces = viewModel.getPlannedPlaces();
        unvisited = plannedPlaces;

        recyclerView = rootView.findViewById(R.id.directionsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        //Load add locations from the json file using helper code
        Map<String, ZooData.VertexInfo> exhibitsMap =
                ZooData.loadVertexInfoJSON(context,"sample_node_info.json");

        //Just the vertex values, from the map
        //We need this to get loaction names from ids
        List<ZooData.VertexInfo> exhibitsList = new ArrayList<ZooData.VertexInfo>(exhibitsMap.values());

        //We need this in order to get the street names from the edge_ids
        streetIdMap = ZooData.loadEdgeIdToStreetJSON(context, "sample_edge_info.json");
        graph = ZooData.loadZooGraphJSON(context, "sample_zoo_graph.json");

        //Get a list of places from the vertex list
        List<Places> placesList = Places.convertVertexListToPlaces(exhibitsList);

        //Create map of name_id -> place
        placesIdMap = placesList.stream().collect(Collectors.toMap(place->place.id_name, place->place));
        //Set the first current exhibit as the entrance gate
        entranceExitPlace = placesList.stream().filter(places -> places.kind==ZooData.VertexInfo.Kind.GATE).findFirst().get();
        current = entranceExitPlace;
        unvisited.add(entranceExitPlace);


        //Setup next button
        Button nextbtn = getView().findViewById(R.id.next_button);
        nextbtn.setOnClickListener(view1 -> nextDirections());

        //Start showing directions
        if(unvisited.size()>1) {
            nextDirections();
        }
    }

    /**
     * Changes screen to display directions to the next planned exhibit
     */
    public void nextDirections() {
        if(unvisited.size()==1) {
            unvisited.add(entranceExitPlace);
            Button nextbtn = getView().findViewById(R.id.next_button);
            nextbtn.setClickable(false);
        }
        unvisited = unvisited.stream().filter(places -> !places.id_name.equals(current.id_name)).collect(Collectors.toList());
        PathCalculator calculator = new PathCalculator(graph, current.id_name, unvisited);
        GraphPath<String, IdentifiedWeightedEdge> path = calculator.smallestPath();
        List<EdgeDispInfo> edgeDispInfoList = convertToDisplay(path);
        current = placesIdMap.get(path.getEndVertex());
        adapter.setDiretionsItems(edgeDispInfoList);
    }

    /**
     * TODO: Fix this to keep proper track of source & destinations
     * @param path
     * @return
     */
    public List<EdgeDispInfo> convertToDisplay(GraphPath<String,IdentifiedWeightedEdge> path) {
        return path.getEdgeList().stream().map(edge -> {
            return new EdgeDispInfo(
                    placesIdMap.get(edge.getSourceStr()).name,
                    placesIdMap.get(edge.getTargetStr()).name,
                    streetIdMap.get(edge.getId()),
                    String.valueOf(edge.getWeight()));
        }).collect(Collectors.toList());


    }

}