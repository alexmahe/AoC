package fr.aoc.session2023;

import fr.aoc.common.Utils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

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
        var enclosedNodes = new ArrayList<Node>();
        for (int y = 0; y < input.size(); y++) {
            for (int x = 0; x < input.get(y).size(); x++) {
                Node nodeStudied = new Node(x, y, NodeType.getNodeTypeFromSymbol(input.get(y).get(x)));
                if (today.isEnclosed(nodeStudied, loop)) {
                    counter++;
                    enclosedNodes.add(nodeStudied);
                }
            }
        }

        log.info("Enclosed nodes : {}", enclosedNodes);
        log.info("Enclosed nodes counter : {}", counter);
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
                                    && (coordChange.get(1) == 0 || coordChange.get(1) != -neighbor[1]))
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

        var nodesOverX = loop.stream()
                .filter(node -> node.x() > nodeStudied.x() && node.y() == nodeStudied.y())
                .sorted(Comparator.comparingInt(Node::x)).toList();
        var nodesUnderX = loop.stream()
                .filter(node -> node.x() < nodeStudied.x() && node.y() == nodeStudied.y())
                .sorted(Comparator.comparingInt(value -> -value.x()))
                .toList();
        var nodesOverY = loop.stream()
                .filter(node -> node.x() == nodeStudied.x() && node.y() > nodeStudied.y())
                .sorted(Comparator.comparingInt(Node::y)).toList();
        var nodesUnderY = loop.stream()
                .filter(node -> node.x() == nodeStudied.x() && node.y() < nodeStudied.y())
                .sorted(Comparator.comparingInt(value -> -value.y()))
                .toList();
        var nodesList = List.of(nodesOverX, nodesUnderX, nodesOverY, nodesUnderY);

        if (nodesList.stream().anyMatch(List::isEmpty)) return false;

        var firstContinuousWallOverX = IntStream.range(0, nodesOverX.size()).boxed()
                .filter(index -> nodesOverX.get(0).x() + index == nodesOverX.get(index).x())
                .toList();
        var firstContinuousWallUnderX = IntStream.range(0, nodesUnderX.size()).boxed()
                .filter(index -> nodesUnderX.get(0).x() - index == nodesUnderX.get(index).x())
                .toList();
        var firstContinuousWallOverY = IntStream.range(0, nodesOverY.size()).boxed()
                .filter(index -> nodesOverY.get(0).y() + index == nodesOverY.get(index).y())
                .toList();
        var firstContinuousWallUnderY = IntStream.range(0, nodesUnderY.size()).boxed()
                .filter(index -> nodesUnderY.get(0).y() - index == nodesUnderY.get(index).y())
                .toList();
        var firstWallList = List.of(firstContinuousWallOverX, firstContinuousWallUnderX, firstContinuousWallOverY, firstContinuousWallUnderY);

        if (!firstWallList.stream().allMatch(list -> list.size() % 2 == 0)) return false;
        return !firstWallList.stream().allMatch(list -> list.size() % 2 == 0);
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
