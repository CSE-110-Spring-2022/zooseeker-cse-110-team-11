package com.example.cse110finalproject;

import android.content.Context;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.jgrapht.Graph;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import com.example.cse110finalproject.ZooData.VertexInfo;


@RunWith(AndroidJUnit4.class)
public class PathFinderTest {
    @Test
    public void testGet() {

        Context context = ApplicationProvider.getApplicationContext();
        Map<String, VertexInfo> exhibitsMap =
            ZooData.loadVertexInfoJSON(context, "exhibit_info.json");
        List<VertexInfo> exhibitsList = new ArrayList<VertexInfo>(exhibitsMap.values());
        Graph<String, IdentifiedWeightedEdge> graph =  ZooData.loadZooGraphJSON(context, "zoo_graph.json");

        List<Places> placesList = Places.convertVertexListToPlaces(exhibitsList);

        List<Places> wantToVisit = placesList.subList(1,3);


        PathCalculator calculator = new PathCalculator(graph, placesList.get(0).id_name, DirectionsFragment.getIdsListFromPlacesList(wantToVisit));
        //GraphPath<String, IdentifiedWeightedEdge> smallestPath=calculator.smallestPath();


        //assertEquals( 300.0, smallestPath.getWeight(),0.0);
    }
}
