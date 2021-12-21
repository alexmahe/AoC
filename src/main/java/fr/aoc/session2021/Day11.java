package fr.aoc.session2021;

import org.apache.commons.io.IOUtils;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class Day11 {

    private int flashCounter = 0;

    public static void main(String[] args) {
        Day11 day11 = new Day11();
        ArrayList<ArrayList<AtomicInteger>> energyMapPart1 = day11.readInput("src/main/resources/2021/day11/input.txt");
        ArrayList<ArrayList<AtomicInteger>> energyMapPart2 = day11.readInput("src/main/resources/2021/day11/input.txt");
        System.out.printf("original energy map : %n%s%n%n", day11.formattingArrayForLog(energyMapPart1));

        for (int step = 0; step < 100; step++) {
            day11.incrementStep(energyMapPart1);
        }
        System.out.printf("Part 1 answer : %s%n", day11.flashCounter);

        boolean allFlashed = false;
        int stepCounter = 0;
        while (!allFlashed) {
            day11.incrementStep(energyMapPart2);
            int numberOfFlash = energyMapPart2.stream()
                    .map(line -> line.stream()
                            .filter(point -> point.get() == 0)
                            .map(point -> 1)
                            .reduce(0, Integer::sum))
                    .reduce(0, Integer::sum);
            allFlashed = numberOfFlash == 100;
            stepCounter++;
        }
        System.out.printf("Part 2 answer : %s%n", stepCounter);
    }

    private ArrayList<ArrayList<AtomicInteger>> readInput(String filePath) {
        ArrayList<ArrayList<AtomicInteger>> inputenergyMap = null;

        try (FileInputStream fis = new FileInputStream(filePath)) {
            String inputStr = IOUtils.toString(fis, StandardCharsets.UTF_8);
            inputenergyMap = Arrays.stream(inputStr.split("(\r\n|\r|\n)")).filter(line -> line != null && !line.isEmpty() && !line.trim().isEmpty()).map(line -> Arrays.stream(line.split("")).map(number -> new AtomicInteger(Integer.parseInt(number))).collect(Collectors.toCollection(ArrayList::new))).collect(Collectors.toCollection(ArrayList::new));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return inputenergyMap;
    }

    private void incrementStep(ArrayList<ArrayList<AtomicInteger>> energyMap) {
        for (int lineIndex = 0; lineIndex < energyMap.size(); lineIndex++) {
            for (int colIndex = 0; colIndex < energyMap.get(lineIndex).size(); colIndex++) {
                if (energyMap.get(lineIndex).get(colIndex).incrementAndGet() == 10) {
                    propagate(lineIndex, colIndex, energyMap);
                }
            }
        }

        energyMap.forEach(line -> line.forEach(point -> {
            if (point.get() > 9) {
                point.set(0);
                flashCounter += 1;
            }
        }));
    }

    private void propagate(int line, int col, ArrayList<ArrayList<AtomicInteger>> energyMap) {
        for (int x = line - 1; x <= line + 1; x++) {
            for (int y = col - 1; y <= col + 1; y++) {
                try {
                    if (energyMap.get(x).get(y).incrementAndGet() == 10) {
                        propagate(x, y, energyMap);
                    }
                } catch (IndexOutOfBoundsException ignored) {
                }
            }
        }
    }

    private String formattingArrayForLog(ArrayList<ArrayList<AtomicInteger>> array) {
        return array.stream().map(boardLine -> boardLine.stream().map(String::valueOf).collect(Collectors.joining(" "))).collect(Collectors.joining("\n"));
    }
}
