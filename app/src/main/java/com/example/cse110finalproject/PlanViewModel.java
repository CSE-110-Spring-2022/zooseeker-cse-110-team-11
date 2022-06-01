package com.example.cse110finalproject;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

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

public class PlanViewModel extends AndroidViewModel {
    @VisibleForTesting
    public final SearchPlacesDao searchPlacesDao;


    private List<PlacesWithDistance> placesdispList;
    private Map<String, Exhibit> exhibitMap;
    private List<Exhibit> unvisitedExhbits;
    private Map<String, String> streetIdMap;
    private Graph<String, IdentifiedWeightedEdge> graph;
    private Map<String, List<Exhibit>> exhibitGroupsWithChildren;

    public void setPlannedPlacesList(List<Places> plannedPlacesList) {
        this.plannedPlacesList = plannedPlacesList;
        processPlacesIntoDisplayList(getApplication().getApplicationContext());
        placesCount.setValue(plannedPlacesList.size());
    }

    public List<PlacesWithDistance> getPlacesdispList() {
        return placesdispList;
    }

    private List<Places> plannedPlacesList;
    @VisibleForTesting
    final SearchDatabase db;
    public MutableLiveData<Integer> placesCount = new MutableLiveData<>();


    public PlanViewModel(@NonNull Application application) {
        super(application);
        Context context = getApplication().getApplicationContext();
        db = SearchDatabase.getSingleton(context);
        searchPlacesDao = db.searchPlacesDao();
        //Load list of exhibits from new json
        Reader exhibitsReader = null;
        Reader trailsReader = null;
        try {
            exhibitsReader = new InputStreamReader(context.getAssets().open("exhibit_info.json"));
            trailsReader = new InputStreamReader(context.getAssets().open("trail_info.json"));
        } catch (IOException e) {
            throw new RuntimeException("Unable to load data for prepopulation!");
        }

        List<Places> placesList = getPlannedPlaces();
        List<Exhibit> exhibitList = Exhibit.fromJson(exhibitsReader);
        exhibitMap = exhibitList.stream().collect(Collectors.toMap(exhibit -> exhibit.id, exhibit -> exhibit));
        //Convert places to exhibits
        unvisitedExhbits = DirectionsFragment.getIdsListFromPlacesList(placesList).stream().map(id-> exhibitMap.get(id)).collect(Collectors.toList());

        //We need this in order to get the street names from the edge_ids
        streetIdMap = ZooData.loadEdgeIdToStreetJSON(context, "trail_info.json");
        graph = ZooData.loadZooGraphJSON(context, "zoo_graph.json");



    }

    public void processPlacesIntoDisplayList(Context context) {
        List<Places> placesList = getPlannedPlaces();


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
//        unvisited.add("entrance_exit_gate");
//        PathCalculator calculator = new PathCalculator(graph, current, unvisited);
//        GraphPath<String, IdentifiedWeightedEdge> sp = calculator.smallestPath();
//        fullPath.add(sp);
//        unvisited.remove(sp.getEndVertex());


        Map<String, String> exhibitToStreet = new HashMap<>();
        Map<Exhibit, Integer> exhibitToDistanceMap = new HashMap<>();

        //Gets the map of exhibit keys to their street string for value to the key
        exhibitToStreet = getStreetFromExhibit(fullPath, placesList, streetIdMap);
        //Gets the map of exhibit keys to their distance from the entrance along the path

        Map<String, Places> placesMap = placesList.stream().collect(Collectors.toMap(places -> places.id_name, places -> places));
        exhibitToDistanceMap = getDistanceFromExhibit(fullPath, exhibitMap);

        placesdispList = convertMapToExhibandDistAndStreet(exhibitToDistanceMap, exhibitToStreet);


        List<PlacesWithDistance> newPlacesdispList = new ArrayList<>();

        //Add all sub exhibits as their own entries and remove groups
        for(PlacesWithDistance placesWithDistance: placesdispList) {
            if(exhibitGroupsWithChildren.containsKey(placesWithDistance.id_name)) {
                placesWithDistance.placesInGroup=exhibitGroupsWithChildren.get(placesWithDistance.id_name);
            } else {
                placesWithDistance.placesInGroup=null;
            }
            if(placesWithDistance.placesInGroup!=null&&!placesWithDistance.placesInGroup.isEmpty()) {
                //Remove the group
                newPlacesdispList.remove(placesWithDistance);

                //Add all sub exhibits as their own entries
                for(Exhibit place :placesWithDistance.placesInGroup) {
                    newPlacesdispList.add(new PlacesWithDistance(place, placesWithDistance.distanceFromEntrance, placesWithDistance.streetName));
                }
            } else {
                newPlacesdispList.add(placesWithDistance);
            }
        }
        placesdispList=newPlacesdispList;


        placesdispList.sort((placesAndDist1, placesAndDist2) -> {
            int distance1 = placesAndDist1.distanceFromEntrance;
            int distance2 = placesAndDist2.distanceFromEntrance;
            if(distance1 < distance2) {
                return -1;
            } else if(distance1 > distance2) {
                return 1;
            } else {
                return 0;
            }
        });
    }


    public List<Places> getPlannedPlaces() {
        if (plannedPlacesList == null) {
            loadPlans();
        }
        placesCount.setValue(plannedPlacesList.size());
        return plannedPlacesList;
    }

    //A method that would remove the planned item from the plan tab
    public void deletePlaces(PlacesWithDistance places) {
        placesdispList.remove(places);
        Places removePlace = plannedPlacesList.stream().filter(places1 -> places1.id_name.equals(places.id_name)).findFirst().get();

        //Check for unplanned entrance
        if(plannedPlacesList.contains(removePlace)) {
            removePlace.checked=false;
            plannedPlacesList.remove(removePlace);
            searchPlacesDao.update(removePlace);
        }

        //Whenever setValue is called, all observers are alerted to keep this updated
        placesCount.setValue(plannedPlacesList.size());
    }

    public void deleteAllPlaces() {
        for(Places places : plannedPlacesList) {
            places.checked = false;
            searchPlacesDao.update(places);
        }
        plannedPlacesList.clear();
        placesdispList.clear();
        placesCount.setValue(plannedPlacesList.size());
    }


    private void loadPlans() {
        plannedPlacesList = searchPlacesDao.getPlannedPlaces();
    }

    public static Map<String, String> getStreetFromExhibit(List<GraphPath<String, IdentifiedWeightedEdge>> fullPath, List<Places> planned, Map<String, String> streetIdMap){
        Map<String, String> bank = new HashMap<>();
        for(int i = 0; i < fullPath.size(); i++) {
            if(planned.contains(fullPath.get(i).getEndVertex())) {
            }
            List<IdentifiedWeightedEdge> edges = fullPath.get(i).getEdgeList();
            IdentifiedWeightedEdge lastEdge = edges.get(edges.size()-1);
            bank.put(fullPath.get(i).getEndVertex(), streetIdMap.get(lastEdge.getId()));
        }
        return bank;
    }


    static List<PlacesWithDistance> convertMapToExhibandDistAndStreet(Map<Exhibit, Integer> convertMap, Map<String, String> streetIdMap) {
        return convertMap.entrySet().stream().map(stringExhibitEntry -> {
            Exhibit key = stringExhibitEntry.getKey();
            int value = stringExhibitEntry.getValue();
            PlacesWithDistance place = new PlacesWithDistance(
                    key,
                    value
            );
            place.setStreetName(streetIdMap.get(key.id));
            return place;
        }).collect(Collectors.toList());

    }


    public static Map<Exhibit, Integer> getDistanceFromExhibit(List<GraphPath<String, IdentifiedWeightedEdge>> fullPath, Map<String, Exhibit> exhibitMap) {
        Map<Exhibit, Integer> bank = new HashMap<>();
        int total = 0;
        for(int i = 0; i < fullPath.size(); i++) {
            total += fullPath.get(i).getWeight();
            bank.put(exhibitMap.get(fullPath.get(i).getEndVertex()), total);
        }

        return bank;
    }
}
