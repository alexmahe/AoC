package fr.aoc.session2022;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import javax.management.InstanceNotFoundException;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static fr.aoc.common.Utils.REGEX_NEW_LINE;

@Slf4j
public class Day12 {

    private static final String ALPHABET = "SabcdefghijklmnopqrstuvwxyzE";
    private static final Integer[][] neighbors = {{0, -1}, {-1, 0}, {1, 0}, {0, 1}};
    private static final Graph graph = new Graph();

    public static void main(String[] args) throws IOException, InstanceNotFoundException {
        Day12 day12 = new Day12();
        var nodeElevationInput = day12.readInput("src/main/resources/2022/day12/input.txt");

        long start = System.currentTimeMillis();
        day12.buildGraph(nodeElevationInput);
        long graphBuilt = System.currentTimeMillis();

        Node sourceNode = graph.getNodes().stream().filter(node -> "S".equals(node.getElevation())).findFirst().get();
        Node endNode = graph.getNodes().stream().filter(node -> "E".equals(node.getElevation())).findFirst().get();

        calcShortestPathFromSource(sourceNode);
        long pathFound = System.currentTimeMillis();

        log.info("Answer 1 : {}", endNode);
        log.info("Time graph : {}\nTime path : {}", graphBuilt - start, pathFound - graphBuilt);

        var shortestPaths = new ArrayList<>(Arrays.asList(endNode.getDistance()));
        graph.getNodes().stream()
                .filter(node -> "a".equals(node.getElevation()))
                .forEach(node -> {
                    try {
                        calcShortestPathFromSource(node);
                    } catch (InstanceNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                    shortestPaths.add(endNode.getDistance());
                });
        log.info("Answer 2 : {}", shortestPaths.stream().mapToLong(Long::longValue).min().getAsLong());
    }

    public List<List<String>> readInput(String filepath) throws IOException {
        try (FileInputStream fis = new FileInputStream(filepath)) {
            return Arrays.stream(IOUtils.toString(fis, StandardCharsets.UTF_8).split(REGEX_NEW_LINE))
                    .map(line -> Arrays.stream(line.split("")).toList())
                    .toList();
        }
    }

    public void buildGraph(List<List<String>> nodeElevationInput) {
        for (int lineIndex = 0; lineIndex < nodeElevationInput.size(); lineIndex++) {
            for (int colIndex = 0; colIndex < nodeElevationInput.get(lineIndex).size(); colIndex++) {
                String elevation = nodeElevationInput.get(lineIndex).get(colIndex);
                Node node = getOrCreateNode(getNodeName(lineIndex, colIndex), elevation);
                Node neighborNode;

                for (Integer[] neighborCoord : neighbors) {
                    int line = lineIndex + neighborCoord[0];
                    int col = colIndex + neighborCoord[1];

                    try {
                        String neighborElevation = nodeElevationInput.get(line).get(col);
                        neighborNode = getOrCreateNode(getNodeName(line, col), neighborElevation);

                        if (calcNodeElevationDiff(elevation, neighborElevation) <= 1) {
                            node.addDestination(neighborNode.getName(), 1);
                        }
                    } catch (IndexOutOfBoundsException ignored) {}
                }
            }
        }
    }

    public static void calcShortestPathFromSource(Node source) throws InstanceNotFoundException {
        source.setDistance(0L);

        Set<String> settledNode = new HashSet<>();
        Set<String> unsettledNode = new HashSet<>();

        unsettledNode.add(source.name);

        while (!unsettledNode.isEmpty()) {
            Node currentNode = getLowestDistanceNode(unsettledNode);
            unsettledNode.remove(currentNode.getName());

            for (Map.Entry<String, Long> adjacencyPair: currentNode.getAdjacentNodes().entrySet()) {
                Node adjacentNode = findNodeByName(adjacencyPair.getKey()).orElseThrow(InstanceNotFoundException::new);
                Long edgeweight = adjacencyPair.getValue();

                if (!settledNode.contains(adjacentNode.getName())) {
                    calcMinDistance(adjacentNode, edgeweight, currentNode);
                    unsettledNode.add(adjacentNode.getName());
                }
            }

            settledNode.add(currentNode.getName());
        }
    }

    private static void calcMinDistance(Node evaluationNode, Long edgweight, Node sourceNode) {
        Long sourceDistance = sourceNode.getDistance();
        if (sourceDistance + edgweight < evaluationNode.getDistance()) {
            evaluationNode.setDistance(sourceDistance + edgweight);
            LinkedList<Node> shortestPath = new LinkedList<>(sourceNode.getShortestPath());
            shortestPath.add(sourceNode);
            evaluationNode.setShortestPath(shortestPath);
        }
    }

    private static Node getLowestDistanceNode(Set<String> nodeSet) throws InstanceNotFoundException {
        return nodeSet.stream()
                .map(nodeName -> findNodeByName(nodeName).get())
                .min(Comparator.comparing(Node::getDistance))
                .orElseThrow(InstanceNotFoundException::new);
    }

    public int calcNodeElevationDiff(String elevationCurrentNode, String elevationDestinationNode) {
        return ALPHABET.indexOf(elevationDestinationNode) - ALPHABET.indexOf(elevationCurrentNode);
    }

    public static Node getOrCreateNode(String name, String elevation) {
        Optional<Node> node = findNodeByName(name);

        if (node.isPresent()) return node.get();

        Node newNode = new Node(name, elevation);
        graph.addNode(newNode);
        return newNode;
    }

    public static Optional<Node> findNodeByName(String name) {
        return graph.getNodes().stream().filter(existingNode -> existingNode.getName().equals(name)).findFirst();
    }

    public String getNodeName(int line, int col) {
        String name = String.valueOf(col);
        name = StringUtils.leftPad(name, 3, "0");
        name = line + name;
        return StringUtils.leftPad(name, 6, "0");
    }


    @Data
    private static class Graph {
        private Set<Node> nodes = new HashSet<>();
        public void addNode(Node node) { nodes.add(node); }
    }

    @Data
    private static class Node {
        Map<String, Long> adjacentNodes = new HashMap<>();
        private String elevation;
        private List<Node> shortestPath = new LinkedList<>();
        private Long distance = Long.MAX_VALUE;
        private String name;

        public Node(String name, String elevation) {
            this.name = name;
            this.elevation = elevation;
        }

        public void addDestination(String destinationName, long distance) {
            adjacentNodes.put(destinationName, distance);
        }

        @Override
        public String toString() {
            return String.format("Elevation {}, distance {}", elevation, distance);
        }
    }
}
