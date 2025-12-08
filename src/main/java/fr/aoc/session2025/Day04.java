package fr.aoc.session2025;

import fr.aoc.common.Utils;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static fr.aoc.common.Utils.RESOURCE;

@Slf4j
public class Day04 {

    private static final Integer[][] NEIGHBOURS = {{-1, -1}, {-1, 0}, {-1, 1}, {0, -1}, {0, 1}, {1, -1}, {1, 0}, {1, 1}};

    public static void main(String[] args) throws IOException {
        var today = new Day04();
        var maxNeighbours = 4;
        var input = new ArrayList<>(Utils.readInputSplitOnNewLines(RESOURCE.formatted("2025", "day04", "input")).stream()
                .map(line -> new ArrayList<>(Arrays.asList(line.split(""))))
                .toList());

        // Part 01
        var accessibleRolls = today.getAccessibleRolls(input, maxNeighbours);
        log.info("Il y a {} rolls accessibles", accessibleRolls.size());

        // Part 02
        var allRemovableRolls = today.countAllRemovableRolls(input, maxNeighbours);
        log.info("On peut enlever {} rolls en tout", allRemovableRolls);
    }

    private int countAllRemovableRolls(ArrayList<ArrayList<String>> map, int maxNeighbours) {
        var counter = 0;
        var accessibleRolls = new ArrayList<List<Integer>>();
        var allPossibleRollsRemoved = false;

        while (!allPossibleRollsRemoved) {
            accessibleRolls = getAccessibleRolls(map, maxNeighbours);
            counter += accessibleRolls.size();
            accessibleRolls.forEach(accessibleRoll -> map.get(accessibleRoll.get(0)).set(accessibleRoll.get(1), "x"));
            allPossibleRollsRemoved = accessibleRolls.isEmpty();
        }

        return counter;
    }

    private ArrayList<List<Integer>> getAccessibleRolls(ArrayList<ArrayList<String>> map, int maxNeighbours) {
        var accessibleCoords = new ArrayList<List<Integer>>();
        for (int x = 0; x < map.size(); x++) {
            for (int y = 0; y < map.size(); y++) {
                if ("@".equals(map.get(x).get(y)) && hasLessThanNeighbours(map, x, y, maxNeighbours)) {
                    accessibleCoords.add(List.of(x, y));
                }
            }
        }

        return accessibleCoords;
    }

    private boolean hasLessThanNeighbours(ArrayList<ArrayList<String>> map, int row, int col, int maxNeighbours) {
        var count = Arrays.stream(NEIGHBOURS)
                .map(coordModifier -> {
                    var neighbour = ".";
                    try {
                        neighbour = map.get(row + coordModifier[0]).get(col + coordModifier[1]);
                    } catch (IndexOutOfBoundsException ignored) {}
                    return neighbour;
                }).filter("@"::equals)
                .count();

        return count < maxNeighbours;
    }



}
