package com.example.cse110finalproject;

import android.content.Context;
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
import android.widget.Button;
import android.widget.EditText;

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


public class DirectionsFragment extends Fragment {

    private Map<String, String> streetIdMap;
    private Map<String, Places> placesIdMap;

    public RecyclerView recyclerView;
    DirectionsViewModel viewModel;
    DirectionsAdapter adapter;
    Places current;
    Places next;
    Places entranceExitPlace;
    private Graph<String, IdentifiedWeightedEdge> graph;
    List<Places> unvisited;
    EditText current_dest;
    EditText next_dest;
    boolean visited_all;
    private List<Exhibit> unvisitedExhbits;
    private Map<String, List<Exhibit>> exhibitGroupsWithChildren;
    private Exhibit currentExhibit;
    private Exhibit nextExhibit;
    private Exhibit entranceExitExhibit;
    private Map<String, Exhibit> exhibitMap;


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

        //Load kist of exhibits from new json
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



        unvisited = plannedPlaces;
        //Convert places to exhibits
        unvisitedExhbits = getIdsListFromPlacesList(plannedPlaces).stream().map(id-> exhibitMap.get(id)).collect(Collectors.toList());
        exhibitGroupsWithChildren = new HashMap<>();

        recyclerView = rootView.findViewById(R.id.directionsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        //Load add locations from the json file using helper code
        Map<String, ZooData.VertexInfo> exhibitsMap =
                ZooData.loadVertexInfoJSON(context, "exhibit_info.json");

        //Just the vertex values, from the map
        //We need this to get loaction names from ids
        List<ZooData.VertexInfo> exhibitsList = new ArrayList<ZooData.VertexInfo>(exhibitsMap.values());

        //We need this in order to get the street names from the edge_ids
        streetIdMap = ZooData.loadEdgeIdToStreetJSON(context, "trail_info.json");
        graph = ZooData.loadZooGraphJSON(context, "zoo_graph.json");

        //Get a list of places from the vertex list
        List<Places> placesList = Places.convertVertexListToPlaces(exhibitsList);

        //Create map of name_id -> place
        placesIdMap = placesList.stream().collect(Collectors.toMap(place->place.id_name, place->place));
        //Set the first current exhibit as the entrance gate
        entranceExitPlace = placesList.stream().filter(places -> places.kind==ZooData.VertexInfo.Kind.GATE).findFirst().get();
        entranceExitExhibit = exhibitList.stream().filter(exhibit -> exhibit.kind==Exhibit.Kind.GATE).findFirst().get();
        currentExhibit = entranceExitExhibit;
        unvisitedExhbits.add(entranceExitExhibit);
        current = entranceExitPlace;
        unvisited.add(entranceExitPlace);
        unvisitedExhbits=groupTogetherExhibits(unvisitedExhbits);
        assert(unvisitedExhbits.stream().noneMatch(exhibit -> exhibit==null));




        //Setup next button
        Button nextbtn = getView().findViewById(R.id.next_button);
        nextbtn.setOnClickListener(view1 -> nextDirections());

        //Setup skip button
        // TODO: Implement skip function
        Button skipbtn = getView().findViewById(R.id.skip_button);
        skipbtn.setOnClickListener(view1 -> skip());

        //Start showing directions
        if(unvisited.size()>1) {
            nextDirections();
        }
    }

    /**
     * @param place list of Places objects
     * @return a list of the places ids
     */
    static List<String> getIdsListFromPlacesList(@NonNull List<Places> place) {
        return place.stream().map(places -> places.id_name).collect(Collectors.toList());
    }

    /**
     * @param exhibits list of Places objects
     * @return a list of the places ids
     */
    static List<String> getIdsListFromExhibits(@NonNull List<Exhibit> exhibits) {
        return exhibits.stream().map(exhibit -> exhibit.id).collect(Collectors.toList());
    }


    List<Exhibit> groupTogetherExhibits(List<Exhibit> exhibits) {

        //Add all exhibits except ones that are part of a group
        //For groups: only add the group
        List<Exhibit> exhibitNeedToVisit = new ArrayList<Exhibit>();
        for(Exhibit exhibit: exhibits) {
            if(exhibit.hasGroup()) {
                String exhibitGroupid = exhibit.groupId;
                //If we already have the group, just add this as a child to keep track of it
                if(exhibitNeedToVisit.contains(exhibit)) {
                    exhibitGroupsWithChildren.get(exhibitGroupid).add(exhibit);
                }
                //Otherwise we add the group to the group map
                //And add the group to the plan
                else {
                    List<Exhibit> childrenList = new ArrayList<Exhibit>();
                    childrenList.add(exhibit);
                    exhibitGroupsWithChildren.put(exhibitGroupid, childrenList);
                    exhibitNeedToVisit.add(exhibitMap.get(exhibitGroupid));
                }
            } else {
                //If the exhibit is not in a group, just add it to the plan
                exhibitNeedToVisit.add(exhibit);
            }
        }

        return exhibitNeedToVisit;

    }

    enum ExhibitOrGroup {

    }


    /**
     * Changes screen to display directions to the next planned exhibit
     * TODO: Add comments/cleanup this stuff
     * TODO: replace Places with Exhibits
     */
    public void nextDirections() {
        // For Debugging
        //TODO: this probably does not put current and next in the right order
        if( visited_all == true){
            current = unvisited.get(0);
            currentExhibit = unvisitedExhbits.get(0);
            next = unvisited.get(1);
            nextExhibit = unvisitedExhbits.get(1);
            Button nextbtn = getView().findViewById(R.id.next_button);
            nextbtn.setEnabled(false);
        }
        unvisited = removePlaceWithId(unvisited, current.id_name);
        unvisitedExhbits = removeExhibitWithId(unvisitedExhbits, currentExhibit.id);


        PathCalculator calculator = new PathCalculator(graph, currentExhibit.id, getIdsListFromExhibits(unvisitedExhbits));
        GraphPath<String, IdentifiedWeightedEdge> path = calculator.smallestPath();
        List<EdgeDispInfo> edgeDispInfoList = convertToDisplay(path);
        adapter.setDiretionsItems(edgeDispInfoList);
        if(visited_all!=true) {
            current = placesIdMap.get(path.getEndVertex());
            currentExhibit = exhibitMap.get(path.getEndVertex());
        }
        printUnvisited();
        setCurrentDestination();
        setNextDestination();

        if(unvisited.size()==1 && visited_all == false) {
            visited_all = true;
            unvisitedExhbits.add(entranceExitExhibit);
            unvisited.add(entranceExitPlace);
        }
    }

    @NonNull
    private List<Places> removePlaceWithId(@NonNull List<Places> unvisited,@NonNull String removeid) {
        return unvisited.stream().filter(places -> !places.id_name.equals(current.id_name)).collect(Collectors.toList());
    }
    private List<Exhibit> removeExhibitWithId(@NonNull List<Exhibit> unvisited,@NonNull String removeid) {
        return unvisited.stream().filter(places -> !places.id.equals(current.id_name)).collect(Collectors.toList());
    }

    public void printUnvisited(){
        for(Places p : unvisited){
            Log.d("Unvisited" , p.getName());
        }
        Log.d("Unvisited",Integer.toString(unvisited.size()));
    }

    /**
     * Skip Buttons Functionality
     */
    public void skip(){
        if(unvisited.size() == 2){
            nextDirections();
        }
        else{
            nextDirections();
            nextDirections();
        }

    }

    /**
     * Sets the current destination
     */
    public void setCurrentDestination(){
        current_dest = (EditText)getView().findViewById(R.id.current_dest);
        current_dest.setText(current.getName());
    }

    /**
     * Gets next destination from the current destination
     */
    public String getNextDestination(){
        String next;
        PathCalculator nextcalculator = new PathCalculator(graph, currentExhibit.id, getIdsListFromExhibits(removeExhibitWithId(unvisitedExhbits, currentExhibit.id)));
        GraphPath<String, IdentifiedWeightedEdge> nextpath = nextcalculator.smallestPath();
        next = placesIdMap.get(nextpath.getEndVertex()).getName();
        return next;
    }

    /**
     * Sets the next destination
     */
    public void setNextDestination(){
        next_dest = (EditText)getView().findViewById(R.id.next_dest);
        next_dest.setText(getNextDestination());
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