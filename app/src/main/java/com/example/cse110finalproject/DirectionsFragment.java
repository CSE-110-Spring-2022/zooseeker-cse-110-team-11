package com.example.cse110finalproject;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import org.apache.commons.lang3.tuple.MutablePair;
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
    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;

    private AlertDialog mockingDialog;

    private TextView directionsSettings;
    private CheckBox briefDirectionsCheck, detailedDirectionsCheck;
    private Button goBack;
    private ImageButton goTo;

    //true is brief directions, false is detailed directions
    boolean directions_settings_type = true;

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
    boolean final_directions;
    private List<Exhibit> unvisitedExhibits;
    private Map<String, List<Exhibit>> exhibitGroupsWithChildren;
    private Exhibit previousExhibit;
    private Exhibit currentExhibit;
    private Exhibit nextExhibit;
    private Exhibit entranceExitExhibit;
    private Map<String, Exhibit> exhibitMap;
    GraphPath<String, IdentifiedWeightedEdge> prevPath;

    MutableLiveData<Pair<Double, Double>>  currCoordinates;
    private GraphPath<String, IdentifiedWeightedEdge> path;
    private EditText latView;
    private EditText lngView;

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

        //Load list of exhibits from new json
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
        unvisitedExhibits = getIdsListFromPlacesList(plannedPlaces).stream().map(id-> exhibitMap.get(id)).collect(Collectors.toList());
        exhibitGroupsWithChildren = new HashMap<>();

        recyclerView = rootView.findViewById(R.id.directionsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        //Load add locations from the json file using helper code
        Map<String, ZooData.VertexInfo> exhibitsMap =
                ZooData.loadVertexInfoJSON(context, "exhibit_info.json");

        //Just the vertex values, from the map
        //We need this to get location names from ids
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
        unvisitedExhibits.add(entranceExitExhibit);
        current = entranceExitPlace;
        unvisited.add(entranceExitPlace);
        unvisitedExhibits =groupTogetherExhibits(unvisitedExhibits, exhibitMap, exhibitGroupsWithChildren);
        assert(unvisitedExhibits.stream().noneMatch(exhibit -> exhibit==null));


        //setup direction tab buttons
        {
            // Setup next button
            Button nextbtn = getView().findViewById(R.id.next_button);
            nextbtn.setOnClickListener(view1 -> nextDirections());

            // Setup skip button
            Button skipbtn = getView().findViewById(R.id.skip_button);
            skipbtn.setOnClickListener(view1 -> skip());

            // Setup previous button
            Button prebtn = getView().findViewById(R.id.previous_button);
            prebtn.setOnClickListener(view1 -> previousDirections());

            // Setup direction settings button
            goTo = getView().findViewById(R.id.directions_settings_btn);
            goTo.setOnClickListener(view1 -> createNewSettingsDialog());
        }


        // Start showing directions
        if(unvisited.size()>1) {
            unvisitedExhibits = removeExhibitWithId(unvisitedExhibits, currentExhibit.id);
            nextDirections();
        }

        {
            //Setup Mocking Event
            latView = getView().findViewById(R.id.latTextView);
            lngView = getView().findViewById(R.id.lngTextView);
            latView.setOnFocusChangeListener((viewVar, hasFoucus) -> {
                if(hasFoucus) {
                    recyclerView.setVisibility(View.INVISIBLE);
                } else {
                    recyclerView.setVisibility(View.VISIBLE);
                }
            });
            lngView.setOnFocusChangeListener((viewVar, hasFoucus) -> {
                if(hasFoucus) {
                    recyclerView.setVisibility(View.INVISIBLE);
                } else {
                    recyclerView.setVisibility(View.VISIBLE);
                }
            });
            Button mockButton = getView().findViewById(R.id.mock_button);
            mockButton.setOnClickListener(view1 -> {
                onMockCoordinates(latView, lngView);
            }
            );
        }


        //Set current location incase we haven't setup live feed
        currCoordinates = new MutableLiveData<Pair<Double, Double>>();
        currCoordinates.setValue(new Pair<>(32.73459618734685, -117.14936));
        currCoordinates.observe(getViewLifecycleOwner(), doublePair -> {
            onCoordinatesChanged(doublePair, path);
        });
    }

    /**
     * When coordinates are mocked, update the current coordinates
     * @param latView
     * @param lngView
     */
    void onMockCoordinates(EditText latView, EditText lngView) {
        double lat = Double.parseDouble(latView.getText().toString());
        double lng = Double.parseDouble(lngView.getText().toString());
        currCoordinates.setValue(Pair.create(lat,lng));
    }


    void onCoordinatesChanged(Pair<Double, Double> coordinates, GraphPath<String, IdentifiedWeightedEdge> path) {
        double lat = coordinates.first;
        double lng = coordinates.second;

        //Check if we are close to an exhibit not in the current path and not closer to a planned one
        path.getVertexList();

        //Get the exhibits that are not on the current path which you should be near
        List<Exhibit> exhibitsOnPath = path.getVertexList().stream().map(id -> exhibitMap.get(id)).collect(Collectors.toList());
        List<Exhibit> exhibitsNotOnPath = exhibitMap.values().stream().filter(exhibit -> !exhibitsOnPath.contains(exhibit)).collect(Collectors.toList());

        //Check if you are near an exhibit not on the current path
        for(Exhibit exhibit: exhibitsNotOnPath) {
            if(exhibit.isCloseTo(coordinates)) {
                //Make new plans with "exhibit" as current exhibit
                //Don't remove current from unvisited
                replanFromNewCurrent(exhibit);
                break;
            }
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


    static List<Exhibit> groupTogetherExhibits(List<Exhibit> exhibits, Map<String, Exhibit> exhibitMap,Map<String, List<Exhibit>> exhibitGroupsWithChildren)  {

        //Add all exhibits except ones that are part of a group
        //For groups: only add the group
        List<Exhibit> exhibitNeedToVisit = new ArrayList<Exhibit>();
        for(Exhibit exhibit: exhibits) {
            if(exhibit.hasGroup()) {
                String exhibitGroupid = exhibit.groupId;
                //If we already have the group, just add this as a child to keep track of it
                if(exhibitNeedToVisit.contains(exhibitMap.get(exhibitGroupid))) {
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

    void replanFromNewCurrent(Exhibit newCurrent) {

        currentExhibit = newCurrent;
        boolean exhibitWasPlanned=false;

        //Remove The new location if it was part of the plan
        if(unvisitedExhibits.contains(newCurrent)) {
            exhibitWasPlanned=true;
            unvisitedExhibits = removeExhibitWithId(unvisitedExhibits, newCurrent.id);
        }
        path = getPath();
        List<EdgeDispInfo> edgeDispInfoList = convertToDisplay(path, exhibitMap, streetIdMap);
        adapter.setDiretionsItems(edgeDispInfoList);


        // Current Destination where user is headed
        if(exhibitWasPlanned) previousExhibit = currentExhibit;

        currentExhibit = exhibitMap.get(path.getEndVertex());

        // Display Current and Next Destination
        setNextCurrent();
        enablePrevious(true);

    }

    /**
     * Changes screen to display directions to the next planned exhibit
     */
    public void nextDirections() {
        if(final_directions){

        }
        else{
            // Remove Current Destination from unvisited list
            unvisitedExhibits = removeExhibitWithId(unvisitedExhibits, currentExhibit.id);
            if(visited_all){
                unvisitedExhibits = removeExhibitWithId(unvisitedExhibits, currentExhibit.id);
                unvisitedExhibits.add(entranceExitExhibit);

                path = getPath();
                List<EdgeDispInfo> edgeDispInfoList;
                if(!directions_settings_type){
                    edgeDispInfoList = convertToDetailedDisplay(path, exhibitMap, streetIdMap);

                }else{
                    edgeDispInfoList = convertToBriefDisplay(path, exhibitMap, streetIdMap);
                }
                previousExhibit = currentExhibit;
                currentExhibit = exhibitMap.get(path.getEndVertex());
                adapter.setDirectionsItems(edgeDispInfoList);
                final_directions = true;
            }
            else{
                // Calculate the next closest exhibit
                path = getPath();
                prevPath = path;
                List<EdgeDispInfo> edgeDispInfoList;
                if(!directions_settings_type){
                    edgeDispInfoList = convertToDetailedDisplay(path, exhibitMap, streetIdMap);

                }else{
                    edgeDispInfoList = convertToBriefDisplay(path, exhibitMap, streetIdMap);
                }
                adapter.setDirectionsItems(edgeDispInfoList);

                // Current Destination where user is headed
                previousExhibit = currentExhibit;
                currentExhibit = exhibitMap.get(path.getEndVertex());

            }

            // Display Current and Next Destination
            setNextCurrent();
            enablePrevious(true);


            if(unvisitedExhibits.size() == 1 && visited_all == false ) {
                visited_all = true;
                next_dest = (EditText) getView().findViewById(R.id.next_dest);
                if(currentExhibit.name.equals("Entrance and Exit Gate")){
                    next_dest.setText(" ");
                    enableNextSkip(false);
                }
                else{
                    next_dest.setText("Entrance and Exit Gate");
                }

            }


        }

    }
    public GraphPath<String, IdentifiedWeightedEdge> getPath(){
        PathCalculator calculator = new PathCalculator(graph, currentExhibit.id, getIdsListFromExhibits(unvisitedExhibits));
        path = calculator.smallestPath();
        return path;
    }

    public void previousDirections(){
        currentExhibit = previousExhibit;

        path = getPath();
        nextExhibit = exhibitMap.get(path.getEndVertex());
        setNextCurrent();
        enablePrevious(false);
        enableNextSkip(true);
        final_directions = false;
        visited_all = false;

    }

    @NonNull
    private List<Places> removePlaceWithId(@NonNull List<Places> unvisited,@NonNull String removeid) {
        return unvisited.stream().filter(places -> !places.id_name.equals(current.id_name)).collect(Collectors.toList());
    }
    private List<Exhibit> removeExhibitWithId(@NonNull List<Exhibit> unvisited,@NonNull String removeid) {
        return unvisited.stream().filter(places -> !places.id.equals(currentExhibit.id)).collect(Collectors.toList());
    }

    public void printUnvisited(){
        for(Exhibit e : unvisitedExhibits){
            Log.d("Unvisited" , e.name);
        }
        Log.d("Unvisited",Integer.toString(unvisitedExhibits.size()));
    }

    /**
     * Skip Buttons Functionality
     */
    public void skip(){
        if(unvisitedExhibits.size() == 2){
            nextDirections();
        }
        else{
            nextDirections();
            nextDirections();
        }

    }

    public void setNextCurrent(){
        setCurrentDestination();
        setNextDestination();
    }

    /**
     * Sets the current destination
     */
    public void setCurrentDestination(){
        current_dest = (EditText)getView().findViewById(R.id.current_dest);
        current_dest.setText(currentExhibit.name);
        if(currentExhibit.name.equals("Entrance and Exit Gate") && visited_all ){
            enableNextSkip(false);
        }
    }

    /**
     * Active/Inactives the next and skip button
     * @param choice whether buttons are enabled
     */
    public void enableNextSkip(boolean choice){
        Button nextbtn = getView().findViewById(R.id.next_button);
        nextbtn.setEnabled(choice);
        Button skipbtn = getView().findViewById(R.id.skip_button);
        skipbtn.setEnabled(choice);
    }

    /**
     * Active/Inactives the previous button
     * @param choice whether buttons are enabled
     */
    public void enablePrevious(boolean choice){
        Button prebtn = getView().findViewById(R.id.previous_button);
        prebtn.setEnabled(choice);
    }

    /**
     * Gets next destination from the current destination
     */
    public String getNextDestination(){
        String next;
        try{
            PathCalculator nextcalculator = new PathCalculator(graph, currentExhibit.id, getIdsListFromExhibits(removeExhibitWithId(unvisitedExhibits, currentExhibit.id)));
            GraphPath<String, IdentifiedWeightedEdge> nextpath = nextcalculator.smallestPath();
            next = placesIdMap.get(nextpath.getEndVertex()).name;
            return next;
        }
        catch(Exception e){
            return null;
        }
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
    public static List<EdgeDispInfo> convertToDetailedDisplay(GraphPath<String,IdentifiedWeightedEdge> path, Map<String, Exhibit> exhibitMap, Map<String, String> streetIdMap) {
        List<EdgeDispInfo> edgeDispInfos = new ArrayList<>();

        String current = path.getStartVertex();

        for(IdentifiedWeightedEdge edge: path.getEdgeList()) {
            if(!edge.getSourceStr().equals(current)) {
                edgeDispInfos.add(new EdgeDispInfo(
                        exhibitMap.get(edge.getTargetStr()).name,
                        exhibitMap.get(edge.getSourceStr()).name,
                        streetIdMap.get(edge.getId()),
                        String.valueOf(edge.getWeight())));
                current = edge.getSourceStr();
            } else {
                edgeDispInfos.add(new EdgeDispInfo(
                        exhibitMap.get(edge.getSourceStr()).name,
                        exhibitMap.get(edge.getTargetStr()).name,
                        streetIdMap.get(edge.getId()),
                        String.valueOf(edge.getWeight())));
                current = edge.getTargetStr();
            }

        }

        return edgeDispInfos;
    }

    public static List<EdgeDispInfo> convertToBriefDisplay(GraphPath<String,IdentifiedWeightedEdge> path, Map<String, Exhibit> exhibitMap, Map<String, String> streetIdMap) {
        List<EdgeDispInfo> edgeDispInfos = new ArrayList<>();

        String current = path.getStartVertex();

        List<IdentifiedWeightedEdge> pathEdges = path.getEdgeList();

        for(int i=0; i<pathEdges.size();i++){
            if(!pathEdges.get(i).getSourceStr().equals(current)) {
                if(i>0 && streetIdMap.get(pathEdges.get(i-1).getId()).equals(streetIdMap.get(pathEdges.get(i).getId()))){
                    EdgeDispInfo last = edgeDispInfos.remove(edgeDispInfos.size()-1);
                    edgeDispInfos.add(new EdgeDispInfo(
                                    last.start,
                                    exhibitMap.get(pathEdges.get(i).getSourceStr()).name,
                                    streetIdMap.get(pathEdges.get(i).getId()),
                                    String.valueOf(Double.valueOf(last.distance) + pathEdges.get(i).getWeight())
                            )
                    );
                }
                else{
                    edgeDispInfos.add(new EdgeDispInfo(
                                    exhibitMap.get(pathEdges.get(i).getTargetStr()).name,
                                    exhibitMap.get(pathEdges.get(i).getSourceStr()).name,
                                    streetIdMap.get(pathEdges.get(i).getId()),
                                    String.valueOf(pathEdges.get(i).getWeight())
                            )
                    );
                }
                current = pathEdges.get(i).getSourceStr();
            }
            else {
                if(i>0 && streetIdMap.get(pathEdges.get(i-1).getId()).equals(streetIdMap.get(pathEdges.get(i).getId()))){
                    EdgeDispInfo last = edgeDispInfos.remove(edgeDispInfos.size()-1);
                    edgeDispInfos.add(new EdgeDispInfo(
                                    last.start,
                                    exhibitMap.get(pathEdges.get(i).getTargetStr()).name,
                                    streetIdMap.get(pathEdges.get(i).getId()),
                                    String.valueOf(Double.valueOf(last.distance) + pathEdges.get(i).getWeight())
                            )
                    );
                }
                else{
                    edgeDispInfos.add(new EdgeDispInfo(
                                    exhibitMap.get(pathEdges.get(i).getSourceStr()).name,
                                    exhibitMap.get(pathEdges.get(i).getTargetStr()).name,
                                    streetIdMap.get(pathEdges.get(i).getId()),
                                    String.valueOf(pathEdges.get(i).getWeight())
                            )
                    );
                }

                current = pathEdges.get(i).getTargetStr();
            }
        }

        return edgeDispInfos;

    }

    public void createNewSettingsDialog(){
        dialogBuilder = new AlertDialog.Builder(getContext());

        final View settingsPopupView = getLayoutInflater().inflate(R.layout.settings_popup,null);
        briefDirectionsCheck = settingsPopupView.findViewById(R.id.briefDirectionsCheck);
        detailedDirectionsCheck = settingsPopupView.findViewById(R.id.detailedDirectionsCheck);
        if(directions_settings_type){
            briefDirectionsCheck.setChecked(true);
        }else{
            detailedDirectionsCheck.setChecked(true);
        }
        goBack = settingsPopupView.findViewById(R.id.goBackBtn);

        dialogBuilder.setView(settingsPopupView);
        dialog = dialogBuilder.create();
        dialog.show();

        briefDirectionsCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                detailedDirectionsCheck.setChecked(false);
                directions_settings_type = true;
            }
        });

        detailedDirectionsCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                briefDirectionsCheck.setChecked(false);
                directions_settings_type = false;
            }
        });

        goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<EdgeDispInfo> edgeDispInfoList;

                if(!directions_settings_type){
                    edgeDispInfoList = convertToDetailedDisplay(prevPath, exhibitMap, streetIdMap);

                }else{
                    edgeDispInfoList = convertToBriefDisplay(prevPath, exhibitMap, streetIdMap);
                }
                adapter.setDirectionsItems(edgeDispInfoList);
                dialog.dismiss();
            }
        });
    }

}