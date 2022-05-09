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
    private List<Places> unvisited;
    private List<List<IdentifiedWeightedEdge>> optimalPath;
    private int currentStep;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_directions, container, false);
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        View rootView = (ViewGroup) getView();
        Context context = getContext();

        viewModel = new ViewModelProvider(this)
                .get(DirectionsViewModel.class);

        adapter = new DirectionsAdapter();
        List<Places> plannedPlaces = viewModel.getAllItems();
        unvisited = plannedPlaces;

        recyclerView = rootView.findViewById(R.id.directionsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        Map<String, ZooData.VertexInfo> exhibitsMap =
                ZooData.loadVertexInfoJSON(context,"sample_node_info.json");
        List<ZooData.VertexInfo> exhibitsList = new ArrayList<ZooData.VertexInfo>(exhibitsMap.values());
        streetIdMap = ZooData.loadEdgeIdToStreetJSON(context, "sample_edge_info.json");
        graph = ZooData.loadZooGraphJSON(context, "sample_zoo_graph.json");

        List<Places> placesList = Places.convertVertexListToPlaces(exhibitsList);
        placesIdMap = placesList.stream().collect(Collectors.toMap(place->place.id_name, place->place));
        //Set the first current exhibit as the entrance gate
        entranceExitPlace = placesList.stream().filter(places -> places.kind==ZooData.VertexInfo.Kind.GATE).findFirst().get();
        current = entranceExitPlace;
        unvisited.add(entranceExitPlace);

        PathCalculator calculator = new PathCalculator(graph, entranceExitPlace.id_name, unvisited, entranceExitPlace);
        optimalPath = calculator.getOptimalPath();
        currentStep = 0;


        Button nextbtn = getView().findViewById(R.id.next_button);
        nextbtn.setOnClickListener(view1 -> nextDirections());

        nextDirections();
    }

    public void nextDirections() {
        if(currentStep==optimalPath.size()-1) {
            Button nextbtn = getView().findViewById(R.id.next_button);
            nextbtn.setClickable(false);
        }
        currentStep++;
        List<IdentifiedWeightedEdge> currEdges = optimalPath.get(currentStep);
        List<EdgeDispInfo> edgeDispInfoList = currEdges.stream().map(edge -> {
            return new EdgeDispInfo(
                    placesIdMap.get(edge.getSourceStr()).name,
                    placesIdMap.get(edge.getTargetStr()).name,
                    streetIdMap.get(edge.getId()),
                    String.valueOf(edge.getWeight()));
        }).collect(Collectors.toList());
        adapter.setDiretionsItems(edgeDispInfoList);
    }

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