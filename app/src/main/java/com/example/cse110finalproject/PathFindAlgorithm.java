package com.example.cse110finalproject;

import org.jgrapht.Graph;

import java.util.List;

public interface PathFindAlgorithm {
    //This identified weight edges are all we need to find the optimal path
    List<List<IdentifiedWeightedEdge>> getPath(Graph<String, IdentifiedWeightedEdge> graph);
}
