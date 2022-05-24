package com.example.cse110finalproject;

import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultUndirectedWeightedGraph;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PathCalculator {
    Graph<String, IdentifiedWeightedEdge> graph;
    String curr;
    List<Places> places;
    List<String> needVisit;

    PathCalculator(Graph<String, IdentifiedWeightedEdge> graph, String curr, List<Places> places) {
        this.graph = graph;
        this.curr = curr;
        this.places = places;
        needVisit = new ArrayList<String>();
        for(Places place : places) {
            String name = place.id_name;
            needVisit.add(name);
        }
    }

    /**
     * Finds all possible optimal paths from current location to other desired locations
     * @return list of all possible paths
     */
    public List<GraphPath<String, IdentifiedWeightedEdge>> calculateAllPaths() {
        List<GraphPath<String, IdentifiedWeightedEdge>> answer = new ArrayList<>();

        //adds all possible paths from curr to everything that needs to be visited
        for(String dest : needVisit){
            GraphPath<String, IdentifiedWeightedEdge> shortest = DijkstraShortestPath.findPathBetween(graph ,curr, dest);
            answer.add(shortest);
        }

        return answer;
    }

    /**
     * Filters through all possibe paths to find the most optimal path
     * @return
     */
    public GraphPath<String, IdentifiedWeightedEdge> smallestPath(){
        GraphPath<String, IdentifiedWeightedEdge> smallestPathResult;
        List<GraphPath<String, IdentifiedWeightedEdge>> allPaths = calculateAllPaths();

        //Get path with min length
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


//    GraphPath<String, IdentifiedWeightedEdge> normalizedPath(GraphPath<String, IdentifiedWeightedEdge> path) {
//        List<IdentifiedWeightedEdge> weightedEdges = path.getEdgeList();
//        List<IdentifiedWeightedEdge> normalizedEdges = path.getEdgeList();
//        Graph<String, IdentifiedWeightedEdge> newPath;
//        newPath = new DefaultUndirectedWeightedGraph<String, IdentifiedWeightedEdge>(
//                IdentifiedWeightedEdge.class);
//
//
//
//
//        String currSource = path.getStartVertex();
//        for (int i = 0; i < weightedEdges.size(); i++) {
//            if(!weightedEdges.get(i).equals(currSource)) {
//
//                newPath.addEdge()
//
//            }
//
//        }
//    }

//    @Override
//    public List<List<IdentifiedWeightedEdge>> getPath(Graph<String, IdentifiedWeightedEdge> graph) {
//        GraphPath<String, IdentifiedWeightedEdge> smallestPathResult;
//
//        smallestPathResult = smallestPath();
//
//        //We need to make sure the order is correct
//
//
//
//
//    }
}
