package fr.aoc.session2021;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static fr.aoc.common.Utils.REGEX_NEW_LINE;

@Slf4j
public class Day15 {

    private static final Integer[][] neighbors = {{0, -1}, {-1, 0}, {1, 0}, {0, 1}};
    private final Map<String, Node> nodeMap = new HashMap<>();
    private Node sourceNode;
    private String endNodeName;
    private int origSize;
    private long replicateNumber;
    Supplier<ArrayList<Long>> arrayListSupplier = () -> Stream.generate(() -> 0L).limit(origSize * replicateNumber).collect(Collectors.toCollection(ArrayList::new));

    public static void main(String[] args) {
        Day15 day15 = new Day15();
        day15.runDijkstra();
    }

    public void runDijkstra() {
        replicateNumber = 1;
        long startTime = System.currentTimeMillis();
        buildGraph();
        long buildGraphTime = System.currentTimeMillis();
        calculateShortestPathFromSource(sourceNode);
        long dijkstraTime = System.currentTimeMillis();
        log.info("Part 1 answer : \n{}", nodeMap.get(endNodeName).toString());
        log.info("Time to complete graph {}, dijkstra {}", buildGraphTime - startTime, dijkstraTime - buildGraphTime);

        replicateNumber = 5;
        startTime = System.currentTimeMillis();
        buildGraph();
        buildGraphTime = System.currentTimeMillis();
        calculateShortestPathFromSource(sourceNode);
        dijkstraTime = System.currentTimeMillis();
        log.info("Part 2 answer : \n{}", nodeMap.get(endNodeName).toString());
        log.info("Time to complete graph {}, dijkstra {}", buildGraphTime - startTime, dijkstraTime - buildGraphTime);
    }

    private void buildGraph() {
        ArrayList<ArrayList<Long>> nodesInput = readInput("src/main/resources/2021/day15/input.txt");
        Graph graph = new Graph();
        endNodeName = getNodeName(nodesInput.size() - 1, nodesInput.get(nodesInput.size() - 1).size() - 1);

        for (int lineIndex = 0; lineIndex < nodesInput.size(); lineIndex++) {
            for (int colIndex = 0; colIndex < nodesInput.get(lineIndex).size(); colIndex++) {
                String nodeName = getNodeName(lineIndex, colIndex);
                Node node = getOrCreateNode(nodeName);
                Node neighborNode;

                for (Integer[] neighbor : neighbors) {
                    int line = lineIndex + neighbor[0];
                    int column = colIndex + neighbor[1];
                    try {
                        String neighborName = getNodeName(line, column);
                        long neighborWeight = (nodesInput.get(line).get(column)) % 10;
                        neighborNode = getOrCreateNode(neighborName);
                        node.addDestination(neighborNode, neighborWeight);
                    } catch (IndexOutOfBoundsException ignored) {
                    }
                }

                if ("000000".equals(nodeName)) sourceNode = node;

                graph.addNode(node);
            }
        }
    }

    private ArrayList<ArrayList<Long>> readInput(String filePath) {
        ArrayList<ArrayList<Long>> nodesInputOrig;
        ArrayList<ArrayList<Long>> nodesInput = new ArrayList<>();

        try (FileInputStream fis = new FileInputStream(filePath)) {
            String[] inputStrArray = IOUtils.toString(fis, StandardCharsets.UTF_8).split(REGEX_NEW_LINE);
            nodesInputOrig = Arrays.stream(inputStrArray)
                    .filter(line -> line != null && !line.isEmpty() && !line.trim().isEmpty())
                    .map(line -> Arrays.stream(line.split("")).map(Long::parseLong).collect(Collectors.toCollection(ArrayList::new)))
                    .collect(Collectors.toCollection(ArrayList::new));

            if (replicateNumber <= 1) {
                return nodesInputOrig;
            }

            origSize = nodesInputOrig.size();
            nodesInput = Stream.generate(arrayListSupplier).limit(origSize * replicateNumber).collect(Collectors.toCollection(ArrayList::new));

            for (int lineIndex = 0; lineIndex < origSize * replicateNumber; lineIndex++) {
                for (int colIndex = 0; colIndex < origSize * replicateNumber; colIndex++) {
                    nodesInput.get(lineIndex).set(colIndex, calcIncrement(nodesInputOrig.get(lineIndex % origSize).get(colIndex % origSize), (lineIndex / origSize), (colIndex / origSize)));
                }
            }
            return nodesInput;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return nodesInput;
    }

    private Node getOrCreateNode(String nodeName) {
        return nodeMap.computeIfAbsent(nodeName, Node::new);
    }

    private void calculateShortestPathFromSource(Node source) {
        source.setDistance(0L);

        Set<Node> settledNodes = new HashSet<>();
        Set<Node> unsettledNodes = new HashSet<>();

        unsettledNodes.add(source);

        while (!unsettledNodes.isEmpty()) {
            Node currentNode = getLowestDistanceNode(unsettledNodes);
            unsettledNodes.remove(currentNode);

            for (Map.Entry<Node, Long> adjacencyPair : currentNode.getAdjacentNodes().entrySet()) {
                Node adjacentNode = adjacencyPair.getKey();
                Long edgeweight = adjacencyPair.getValue();
                if (!settledNodes.contains(adjacentNode)) {
                    calculateMinimumDistance(adjacentNode, edgeweight, currentNode);
                    unsettledNodes.add(adjacentNode);
                }
            }

            settledNodes.add(currentNode);
        }
    }

    private Node getLowestDistanceNode(Set<Node> unsettledNodes) {
        Node lowestDistanceNode = null;
        long lowestDistance = Long.MAX_VALUE;

        for (Node node : unsettledNodes) {
            long nodeDistance = node.getDistance();
            if (nodeDistance < lowestDistance) {
                lowestDistance = nodeDistance;
                lowestDistanceNode = node;
            }
        }

        return lowestDistanceNode;
    }

    private void calculateMinimumDistance(Node evaluationNode, Long edgeWeigh, Node sourceNode) {
        Long sourceDistance = sourceNode.getDistance();

        if (sourceDistance + edgeWeigh < evaluationNode.getDistance()) {
            evaluationNode.setDistance(sourceDistance + edgeWeigh);
            LinkedList<Node> shortestPath = new LinkedList<>(sourceNode.getShortestPath());
            shortestPath.add(sourceNode);
            evaluationNode.setShortestPath(shortestPath);
        }
    }

    private String getNodeName(int line, int col) {
        String nodeName = "";
        if (line < 100) nodeName += "0";
        if (line < 10) nodeName += "0";
        nodeName += String.valueOf(line);

        if (col < 100) nodeName += "0";
        if (col < 10) nodeName += "0";
        nodeName += String.valueOf(col);

        return nodeName;
    }

    private long calcIncrement(long base, int lineReplicate, int colReplicate) {
        long result = base + lineReplicate + colReplicate;
        return result > 9 ? result - 9 : result;
    }

    @Getter
    @Setter
    private static class Graph {
        private Set<Node> nodes = new HashSet<>();

        public void addNode(Node node) {
            nodes.add(node);
        }
    }

    @Getter
    @Setter
    private static class Node {
        Map<Node, Long> adjacentNodes = new HashMap<>();
        private String name;
        private List<Node> shortestPath = new LinkedList<>();
        private Long distance = Long.MAX_VALUE;

        public Node(String name) {
            this.name = name;
        }

        public void addDestination(Node destination, long distance) {
            adjacentNodes.put(destination, distance);
        }

        @Override
        public String toString() {
            return String.format("Name {}, distance {}", name, distance);
        }
    }

}
