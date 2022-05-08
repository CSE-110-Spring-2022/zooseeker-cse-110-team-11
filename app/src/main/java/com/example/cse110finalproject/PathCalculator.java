package com.example.cse110finalproject;

import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class PathCalculator {
    Graph<String, IdentifiedWeightedEdge> graph;
    String curr;
    List<Places> places;
    List<String> nVisited;

    PathCalculator(Graph<String, IdentifiedWeightedEdge> graph, String curr, List<Places> places) {
        this.graph = graph;
        this.curr = curr;
        this.places = places;
        nVisited = new ArrayList<String>();
        for(Places place : places) {
            String name = place.id_name;
            nVisited.add(name);
        }
    }

    public List<GraphPath<String, IdentifiedWeightedEdge>> calculateAllPaths() {
        List<GraphPath<String, IdentifiedWeightedEdge>> answer = new ArrayList<>();

        for(String dest : nVisited){
            GraphPath<String, IdentifiedWeightedEdge> shortest = DijkstraShortestPath.findPathBetween(graph ,curr, dest);
            answer.add(shortest);
        }

        return answer;
    }

    public GraphPath<String, IdentifiedWeightedEdge> smallestPath(){
        GraphPath<String, IdentifiedWeightedEdge> smallestPathResult;
        List<GraphPath<String, IdentifiedWeightedEdge>> allPaths = calculateAllPaths();
        allPaths.stream().collect(Collectors.toMap((graph) -> graph.getWeight(), graph->graph));
        smallestPathResult = allPaths.stream().min((graph1, graph2) -> {
            if(graph1.getWeight() < graph2.getWeight()) {
                return -1;
            } else if(graph1.getWeight() > graph2.getWeight()) {
                return 1;
            } else {
                return 0;
            }
        }).get();

        return smallestPathResult;
    }
}
