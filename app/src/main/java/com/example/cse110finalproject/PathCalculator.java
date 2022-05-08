package com.example.cse110finalproject;

import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;

import java.util.ArrayList;
import java.util.List;

public class PathCalculator {
    Graph<String, IdentifiedWeightedEdge> graph;
    String curr;
    ArrayList<Places> places;
    List<String> nVisited;

    PathCalculator(Graph<String, IdentifiedWeightedEdge> graph, String curr, ArrayList<Places> places) {
        this.graph = graph;
        this.curr = curr;
        this.places = places;
        for(Places place : places) {
            String name = place.getName();
            nVisited.add(name);
        }
    }

    public List<GraphPath<String, IdentifiedWeightedEdge>> calculatePath() {
        List<GraphPath<String, IdentifiedWeightedEdge>> answer = new ArrayList<>();

        nVisited.remove(curr);

        while(nVisited.size() > 1) {
            GraphPath<String, IdentifiedWeightedEdge> minimumP = null;
            double minDist = Double.MAX_VALUE;
            for(String dest : nVisited){
                GraphPath<String, IdentifiedWeightedEdge> shortest = DijkstraShortestPath.findPathBetween(graph ,curr, dest);
                if(minDist > shortest.getWeight()){
                    minDist = shortest.getWeight();
                    minimumP = shortest;
                }
            }
            answer.add(minimumP);
        }

        return answer;
    }
}
