package fr.aoc.session2023;

import fr.aoc.common.Utils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
public class Day10 {

    private int[][] neighbors = {{1, 0}, {0, -1}, {-1, 0}, {0, 1}};

    public static void main(String[] args) throws IOException {
        var today = new Day10();
        var input = Utils.readInputSplitOnNewLines("src/main/resources/2023/day10/input_test").stream()
                .map(line -> Arrays.stream(line.split("")).toList())
                .toList();

        var startingLine = input.stream().filter(line -> line.contains("S")).findFirst().get();
        var startingY = input.indexOf(startingLine);
        var startingX = startingLine.indexOf("S");

        var loop = today.traversePipes(input, startingX, startingY);
        var length = loop.size();
        log.info("Taille de la chaine : {}", length);
        log.info("Answer : {}", length / 2);

        var counter = 0;
        for (int y = 0; y < input.size(); y++) {
            for (int x = 0; x < input.get(y).size(); x++) {
                if (today.isEnclosed(new Node(x, y, NodeType.getNodeTypeFromSymbol(input.get(y).get(x))), loop)) counter++;
            }
        }

        log.info("counter : {}", counter);
    }

    private HashSet<Node> traversePipes(List<List<String>> map, int startingX, int startingY) {
        var visitedNodeSet = new HashSet<>(Set.of(new Node(startingX, startingY, NodeType.S)));
        Node currentNode = new Node(-1, -1, NodeType.G);
        List<Integer> nextNodeDir = Collections.emptyList();
        for (int[] neighbor : neighbors) {
            try {
                var neighborNodeType = NodeType.getNodeTypeFromSymbol(map.get(startingY + neighbor[1]).get(startingX + neighbor[0]));
                if (neighborNodeType == NodeType.G) continue;
                if (neighborNodeType.getAdjacentNodes().stream().anyMatch(coordChange -> coordChange.get(0) == -neighbor[0] && coordChange.get(1) == -neighbor[1])) {
                    currentNode = new Node(startingX + neighbor[0], startingY + neighbor[1], neighborNodeType);
                    nextNodeDir = neighborNodeType.getAdjacentNodes().stream()
                            .filter(coordChange -> (coordChange.get(0) == 0 || coordChange.get(0) != -neighbor[0])
                                    && (coordChange.get(1) == 0 || coordChange.get(1) != --neighbor[1]))
                            .findFirst().get();
                    break;
                }
            } catch (IndexOutOfBoundsException ignored) {}
        }

        while (currentNode.nodeType != NodeType.S) {
            try {
                visitedNodeSet.add(currentNode);
                var nextNodeType = NodeType.getNodeTypeFromSymbol(map.get(currentNode.y() + nextNodeDir.get(1)).get(currentNode.x() + nextNodeDir.get(0)));
                if (nextNodeType == NodeType.G || nextNodeType == NodeType.S) break;
                List<Integer> nextNodeDirTemp = nextNodeDir;
                currentNode = new Node(currentNode.x() + nextNodeDir.get(0), currentNode.y() + nextNodeDir.get(1), nextNodeType);
                nextNodeDir = nextNodeType.getAdjacentNodes().stream()
                        .filter(coordChange -> (coordChange.get(0) == 0 || coordChange.get(0) != -nextNodeDirTemp.get(0))
                                && (coordChange.get(1) == 0 || coordChange.get(1) != -nextNodeDirTemp.get(1)))
                        .findFirst().get();
            } catch (IndexOutOfBoundsException ignored) {}
        }

        return visitedNodeSet;
    }

    private boolean isEnclosed(Node nodeStudied, Set<Node> loop) {
        if (loop.contains(nodeStudied)) return false;

        var nodesOverX = loop.stream().filter(node -> node.x() > nodeStudied.x() && node.y() == nodeStudied.y()).toList();
        var nodesUnderX = loop.stream().filter(node -> node.x() < nodeStudied.x() && node.y() == nodeStudied.y()).toList();
        var nodesOverY = loop.stream().filter(node -> node.x() == nodeStudied.x() && node.y() > nodeStudied.y()).toList();
        var nodesUnderY = loop.stream().filter(node -> node.x() == nodeStudied.x() && node.y() < nodeStudied.y()).toList();
        if (nodesOverX.isEmpty() || nodesOverY.isEmpty() || nodesUnderX.isEmpty() || nodesUnderY.isEmpty()) return false;

        var wallOnTheWay = false;
        wallOnTheWay = wallOnTheWay || nodesOverX.stream()
                .map(node -> node.nodeType().getSymbol())
                .anyMatch(symbol -> NodeType.NS.getSymbol().equals(symbol));
        wallOnTheWay = wallOnTheWay || nodesUnderX.stream()
                .map(node -> node.nodeType().getSymbol())
                .anyMatch(symbol -> NodeType.NS.getSymbol().equals(symbol));
        wallOnTheWay = wallOnTheWay || nodesOverY.stream()
                .map(node -> node.nodeType().getSymbol())
                .anyMatch(symbol -> NodeType.EW.getSymbol().equals(symbol));
        wallOnTheWay = wallOnTheWay || nodesUnderY.stream()
                .map(node -> node.nodeType().getSymbol())
                .anyMatch(symbol -> NodeType.EW.getSymbol().equals(symbol));
        return wallOnTheWay;
    }

    private record Node(int x, int y, NodeType nodeType) {}

    @Getter
    @AllArgsConstructor
    private enum NodeType {
        NS("|", List.of(List.of(0, 1), List.of(0, -1))),
        EW("-", List.of(List.of(-1, 0), List.of(1, 0))),
        NE("L", List.of(List.of(0, -1), List.of(1, 0))),
        NW("J", List.of(List.of(0, -1), List.of(-1, 0))),
        SW("7", List.of(List.of(0, 1), List.of(-1, 0))),
        SE("F", List.of(List.of(0, 1), List.of(1, 0))),
        G(".", null),
        O("O", null),
        I("I", null),
        S("S", null);

        private String symbol;
        private List<List<Integer>> adjacentNodes;

        public static NodeType getNodeTypeFromSymbol(String symbol) {
            return Arrays.stream(NodeType.values())
                    .filter(nodeType -> nodeType.getSymbol().equalsIgnoreCase(symbol))
                    .findFirst().orElseThrow(UnsupportedOperationException::new);
        }
    }
}
