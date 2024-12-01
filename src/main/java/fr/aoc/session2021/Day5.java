package fr.aoc.session2021;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static fr.aoc.common.Utils.REGEX_NEW_LINE;

@Slf4j
public class Day5 {

    private List<List<List<Integer>>> listOfCoordinates;
    private List<List<List<Integer>>> filteredListOfCoordinates;
    private ArrayList<ArrayList<AtomicInteger>> topology;
    private ArrayList<ArrayList<AtomicInteger>> topologyFiltered;
    private Map<String, Integer> maxCoordinates;
    Supplier<ArrayList<AtomicInteger>> arrayListSupplier = () -> Stream.generate(AtomicInteger::new).limit(maxCoordinates.get("x") + 1).collect(Collectors.toCollection(ArrayList::new));
    private Map<String, Integer> maxCoordinatesFiltered;
    Supplier<ArrayList<AtomicInteger>> arrayListSupplierFiltered = () -> Stream.generate(AtomicInteger::new).limit(maxCoordinatesFiltered.get("x") + 1).collect(Collectors.toCollection(ArrayList::new));

    public static void main(String[] args) {
        // GIVEN
        Day5 day5 = new Day5();
        day5.readInput("src/main/resources/2021/day5/input.txt");
        day5.fillTopology(day5.filteredListOfCoordinates, day5.topologyFiltered);
        day5.fillTopology(day5.listOfCoordinates, day5.topology);

        // Partie 1
        log.info("Overlapping points filtered : {}", day5.calcOverlapping(day5.topologyFiltered));

        // Partie 2
        log.info("Overlapping points : {}", day5.calcOverlapping(day5.topology));
    }

    private void readInput(String filePath) {
        try (FileInputStream fis = new FileInputStream(filePath)) {
            String inputStr = IOUtils.toString(fis, StandardCharsets.UTF_8);
            listOfCoordinates = Arrays.stream(inputStr.split(REGEX_NEW_LINE))
                    .map(line -> Arrays.stream(line.split("\\s->\\s"))
                            .map(point -> Arrays.stream(point.split(","))
                                    .map(Integer::parseInt)
                                    .toList())
                            .toList())
                    .toList();
            filteredListOfCoordinates = listOfCoordinates.stream()
                    .filter(line -> Objects.equals(line.get(0).get(0), line.get(1).get(0)) || Objects.equals(line.get(0).get(1), line.get(1).get(1)))
                    .toList();

            maxCoordinates = calcMaxCoordinates(listOfCoordinates);
            maxCoordinatesFiltered = calcMaxCoordinates(filteredListOfCoordinates);

            topology = Stream.generate(arrayListSupplier).limit(maxCoordinates.get("y") + 1).collect(Collectors.toCollection(ArrayList::new));
            topologyFiltered = Stream.generate(arrayListSupplierFiltered).limit(maxCoordinatesFiltered.get("y") + 1).collect(Collectors.toCollection(ArrayList::new));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void fillTopology(List<List<List<Integer>>> listOfCoordinates, ArrayList<ArrayList<AtomicInteger>> topology) {
        listOfCoordinates.forEach(lineCoordinates -> {
            int first, second;
            if (Objects.equals(lineCoordinates.get(0).get(0), lineCoordinates.get(1).get(0))) {
                first = lineCoordinates.get(0).get(1);
                second = lineCoordinates.get(1).get(1);
                fillVertical(Math.min(first, second), Math.max(first, second), lineCoordinates.get(0).get(0), topology);
            } else if (Objects.equals(lineCoordinates.get(0).get(1), lineCoordinates.get(1).get(1))) {
                first = lineCoordinates.get(0).get(0);
                second = lineCoordinates.get(1).get(0);
                fillHorizontal(Math.min(first, second), Math.max(first, second), lineCoordinates.get(0).get(1), topology);
            } else if (Math.abs(lineCoordinates.get(0).get(0) - lineCoordinates.get(1).get(0)) == Math.abs(lineCoordinates.get(0).get(1) - lineCoordinates.get(1).get(1))) {
                fillDiagonal(lineCoordinates, topology);
            }
        });
    }

    private int calcOverlapping(ArrayList<ArrayList<AtomicInteger>> topology) {
        return topology.stream()
                .map(line -> line.stream().filter(number -> number.get() > 1).map(number -> 1).reduce(0, Integer::sum))
                .reduce(0, Integer::sum);
    }

    private Map<String, Integer> calcMaxCoordinates(List<List<List<Integer>>> listOfCoordinates) {
        int maxX = 0;
        int maxY = 0;

        for (var line : listOfCoordinates) {
            for (var point : line) {
                if (point.get(0) > maxX) maxX = point.get(0);
                if (point.get(1) > maxY) maxY = point.get(1);
            }
        }

        return Map.of("x", maxX, "y", maxY);
    }

    private void fillVertical(int lineStart, int lineEnd, int colNum, ArrayList<ArrayList<AtomicInteger>> topology) {
        for (int index = lineStart; index <= lineEnd; index++) {
            topology.get(index).get(colNum).getAndIncrement();
        }
    }

    private void fillHorizontal(int colStart, int colEnd, int lineNum, ArrayList<ArrayList<AtomicInteger>> topology) {
        for (int index = colStart; index <= colEnd; index++) {
            topology.get(lineNum).get(index).getAndIncrement();
        }
    }

    private void fillDiagonal(List<List<Integer>> diagonal, ArrayList<ArrayList<AtomicInteger>> topology) {
        int lineStart = diagonal.get(0).get(1), lineGoal = diagonal.get(1).get(1);
        int colStart = diagonal.get(0).get(0), colGoal = diagonal.get(1).get(0);
        for (int lineNum = lineStart, colNum = colStart;
             (lineStart < lineGoal && lineGoal >= lineNum) || (lineStart > lineGoal && lineGoal <= lineNum);
             lineNum = incrementIndex(lineNum, lineStart, lineGoal), colNum = incrementIndex(colNum, colStart, colGoal)) {
            topology.get(lineNum).get(colNum).getAndIncrement();
        }
    }

    private int incrementIndex(int index, int start, int goal) {
        int result = index;
        if (index > goal || (index == goal && start > goal)) {
            result--;
        } else if (index < goal || (index == goal && goal > start)) {
            result++;
        }
        return result;
    }

    private String formattingArrayForLog(ArrayList<ArrayList<AtomicInteger>> array) {
        return array.stream()
                .map(boardLine -> boardLine.stream().map(String::valueOf).collect(Collectors.joining(" ")))
                .collect(Collectors.joining("\n"));
    }
}
