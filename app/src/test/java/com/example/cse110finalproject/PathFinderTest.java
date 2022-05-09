package com.example.cse110finalproject;

import android.content.Context;

import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
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

//        Context context = ApplicationProvider.getApplicationContext();
//        Map<String, VertexInfo> exhibitsMap =
//            ZooData.loadVertexInfoJSON(context,"sample_node_info.json");
//        List<VertexInfo> exhibitsList = new ArrayList<VertexInfo>(exhibitsMap.values());
//        Graph<String, IdentifiedWeightedEdge> graph =  ZooData.loadZooGraphJSON(context, "sample_zoo_graph.json");
//
//        List<Places> placesList = Places.convertVertexListToPlaces(exhibitsList);
//
//        List<Places> wantToVisit = placesList.subList(0,3);
//
//
//        PathCalculator calculator = new PathCalculator(graph, wantToVisit.get(0).id_name, wantToVisit);
//        GraphPath<String, IdentifiedWeightedEdge> smallestPath=calculator.smallestPath();
//
//
//        assertEquals((double) 300, smallestPath.getWeight());
    }
}
