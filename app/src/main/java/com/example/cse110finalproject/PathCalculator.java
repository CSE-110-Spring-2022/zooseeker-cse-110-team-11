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
    Places entranceNode;
    private List<List<List<IdentifiedWeightedEdge>>> allPaths;

    PathCalculator(Graph<String, IdentifiedWeightedEdge> graph, String curr, List<Places> places) {
        this.graph = graph;
        this.curr = curr;
        this.places = places;
        nVisited = new ArrayList<String>();
        allPaths = new ArrayList<>();
        for(Places place : places) {
            String name = place.id_name;
            nVisited.add(name);
        }
    }

    PathCalculator(Graph<String, IdentifiedWeightedEdge> graph, String curr, List<Places> places, Places entranceNode) {
        this.graph = graph;
        this.curr = curr;
        this.places = places;
        this.entranceNode=entranceNode;
        nVisited = new ArrayList<String>();
        allPaths = new ArrayList<>();
        for(Places place : places) {
            String name = place.id_name;
            nVisited.add(name);
        }
    }
    public void calculateAllPaths(String currentNode, List<String> needToVisit, List<List<IdentifiedWeightedEdge>> edgesSoFar) {

        if(needToVisit.isEmpty()) {
            GraphPath<String, IdentifiedWeightedEdge> shortest = DijkstraShortestPath.findPathBetween(graph, currentNode, entranceNode.id_name);
            List<IdentifiedWeightedEdge> edge = shortest.getEdgeList();
            List<List<IdentifiedWeightedEdge>> edgesSoFarCopy = edgesSoFar.stream().collect(Collectors.toList());
            edgesSoFarCopy.add(edge);
            allPaths.add(edgesSoFarCopy);
        }

        //Remove current from list
        needToVisit = needToVisit.stream().filter(id_name -> !id_name.equals(currentNode)).collect(Collectors.toList());
        for (String dest : needToVisit) {
            GraphPath<String, IdentifiedWeightedEdge> shortest = DijkstraShortestPath.findPathBetween(graph, currentNode, dest);
            List<IdentifiedWeightedEdge> edge = shortest.getEdgeList();
            List<List<IdentifiedWeightedEdge>> edgesSoFarCopy = edgesSoFar.stream().collect(Collectors.toList());
            edgesSoFarCopy.add(edge);
            List<String> needToVisitRemoved = needToVisit.stream().filter(idname->!idname.equals(dest)).collect(Collectors.toList());
            calculateAllPaths(dest, needToVisitRemoved, edgesSoFarCopy);
        }
    }

    public List<List<IdentifiedWeightedEdge>> getOptimalPath() {
        List<String> needToVisit = places.stream().map(place->place.id_name).collect(Collectors.toList());
        List<List<IdentifiedWeightedEdge>> visitedSoFar = new ArrayList<>();
        calculateAllPaths(entranceNode.id_name, needToVisit, visitedSoFar);
        List<List<IdentifiedWeightedEdge>> smallestPath = allPaths.stream().min((listOfEdges1, listOfEdges2)-> {
            double edgesOneTotalWeight = listOfEdges1.stream().mapToDouble(edgelist->{
                return edgelist.stream().mapToDouble(edge->edge.getWeight()).sum();
            }).sum();
            double edgesTwoTotalWeight = listOfEdges2.stream().mapToDouble(edgelist->{
                return edgelist.stream().mapToDouble(edge->edge.getWeight()).sum();
            }).sum();
            if(edgesOneTotalWeight<edgesTwoTotalWeight) {
                return -1;
            } else if(edgesOneTotalWeight>edgesTwoTotalWeight) {
                return 1;
            } else {
                return 0;
            }
        }).get();
        return smallestPath;
    }

//    public GraphPath<String, IdentifiedWeightedEdge> smallestPath(){
//        GraphPath<String, IdentifiedWeightedEdge> smallestPathResult;
//        List<GraphPath<String, IdentifiedWeightedEdge>> allPaths = calculateAllPaths(entranceNode.);
//        allPaths.stream().collect(Collectors.toMap((graph) -> graph.getWeight(), graph->graph));
//        smallestPathResult = allPaths.stream().min((graph1, graph2) -> {
//            if(graph1.getWeight() < graph2.getWeight()) {
//                return -1;
//            } else if(graph1.getWeight() > graph2.getWeight()) {
//                return 1;
//            } else {
//                return 0;
//            }
//        }).get();
//
//        return smallestPathResult;
//    }
}
