package fr.aoc.session2021;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static fr.aoc.common.Utils.REGEX_NEW_LINE;

@Slf4j
public class Day12 {

    private static List<List<String>> connections;
    private ArrayList<ArrayList<String>> validPathsPart1 = new ArrayList<>();
    private ArrayList<ArrayList<String>> validPathsPart2 = new ArrayList<>();

    public static void main(String[] args) {
        Day12 day12 = new Day12();
        connections = day12.readInput("src/main/resources/2021/day12/input.txt");

        day12.exploreCaves(new ArrayList<>(Arrays.asList("start")), false);
        log.info("Part 1 answer, number of paths : {}", day12.validPathsPart1.size());

        day12.exploreCaves(new ArrayList<>(Arrays.asList("start")), true);
        log.info("Part 2 answer, number of paths : {}", day12.validPathsPart2.size());
    }

    private List<List<String>> readInput(String filePath) {
        List<List<String>> inputLines = null;

        try (FileInputStream fis = new FileInputStream(filePath)) {
            String inputStr = IOUtils.toString(fis, StandardCharsets.UTF_8);
            inputLines = Arrays.stream(inputStr.split(REGEX_NEW_LINE))
                    .filter(line -> line != null && !line.isEmpty() && !line.trim().isEmpty())
                    .map(line -> Arrays.stream(line.split("-")).toList())
                    .toList();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return inputLines;
    }

    private void exploreCaves(ArrayList<String> path, boolean isPart2) {
        String lastStep = path.get(path.size() - 1);
        if ("end".equals(lastStep)) {
            if (isPart2) {
                validPathsPart2.add(path);
            } else {
                validPathsPart1.add(path);
            }
        } else {
            connections.stream()
                    .filter(connection -> connection.get(0).equals(lastStep) || connection.get(1).equals(lastStep))
                    .forEach(connection -> {
                        String nextStep = connection.get(0).equals(lastStep) ? connection.get(1) : connection.get(0);
                        if (isValidNextStep(path, nextStep, isPart2)) {
                            ArrayList<String> newPath = new ArrayList<>(path);
                            newPath.add(nextStep);
                            exploreCaves(newPath, isPart2);
                        }
                    });
        }
    }

    private boolean isValidNextStep(ArrayList<String> path, String nextStep, boolean part2) {
        if ("start".equals(nextStep)) return false;
        if (!part2) {
            return nextStep.equals(nextStep.toUpperCase()) || !path.contains(nextStep);
        } else {
            return nextStep.equals(nextStep.toUpperCase())
                    || !path.contains(nextStep)
                    || isFirstSmallCaveVisitedTwice(path);
        }
    }

    private boolean isFirstSmallCaveVisitedTwice(ArrayList<String> path) {
        AtomicBoolean firstSmallCaveVisitedTwice = new AtomicBoolean(true);
        path.stream()
                .filter(cave -> !"start".equals(cave) && !cave.equals(cave.toUpperCase()))
                .forEach(cave -> {
                    if (path.indexOf(cave) != path.lastIndexOf(cave)) {
                        firstSmallCaveVisitedTwice.set(false);
                    }
                });
        return  firstSmallCaveVisitedTwice.get();
    }

}
