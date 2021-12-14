package fr.aoc.session2021;

import org.apache.commons.io.IOUtils;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

public class Day14 {

    Map<String, String> matchingMap = new HashMap<>();
    Map<String, AtomicLong> moleculeMap = new HashMap<>();
    private String[] startingMolecule;

    public static void main(String[] args) {
        Day14 day14 = new Day14();
        day14.readInput("src/main/resources/2021/day14/input.txt");
        System.out.printf("starting map : %n%s%n%n", day14.moleculeMap);
        Map<String, AtomicLong> answerMap = new HashMap<>(day14.moleculeMap);

        day14.calcNSteps(answerMap, 10, 1);
        day14.calcNSteps(answerMap, 40, 2);
    }

    private void readInput(String filePath) {
        try (FileInputStream fis = new FileInputStream(filePath)) {
            String[] inputStrArray = IOUtils.toString(fis, StandardCharsets.UTF_8).split("(\r\n|\r|\n)");
            Arrays.stream(inputStrArray).filter(line -> line != null && !line.isEmpty() && !line.trim().isEmpty())
                    .filter(line -> line.contains("->"))
                    .map(line -> line.split(" -> "))
                    .forEach(line -> {
                        matchingMap.put(line[0], line[1]);
                        moleculeMap.put(line[0], new AtomicLong(0));
                    });
            startingMolecule = inputStrArray[0].split("");
            System.out.printf("Starting molecule : %s%n%n", Arrays.stream(startingMolecule).toList());
            for (int index = 0; index < startingMolecule.length - 1; index++) {
                String molecule = startingMolecule[index] + startingMolecule[index + 1];
                moleculeMap.get(molecule).getAndIncrement();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void calcNSteps(Map<String, AtomicLong> answerMap, int nbStep, int part) {
        long mostCommon;
        long leastCommon;
        Map<String, AtomicLong> elementsSummary;

        for (int step = 0; step < nbStep; step++) {
            answerMap = executeStep(answerMap);
        }

        elementsSummary = countElements(answerMap);
        System.out.printf("Total elements : %s%n", elementsSummary);
        mostCommon = elementsSummary.values().stream().map(AtomicLong::get).mapToLong(x -> x).max().orElse(0L);
        leastCommon = elementsSummary.values().stream().map(AtomicLong::get).mapToLong(x -> x).min().orElse(0L);
        System.out.printf("Most common %s and least common %s quantities%n", mostCommon, leastCommon);
        System.out.printf("Answer part %s, difference = %s%n%n", part, mostCommon - leastCommon);
    }

    private Map<String, AtomicLong> executeStep(Map<String, AtomicLong> moleculeMap) {
        Map<String, AtomicLong> newMoleculeMap = new HashMap<>();

        moleculeMap.forEach((molecule, quantity) -> {
            String[] elements = molecule.split("");
            String[] addedMolecules = {elements[0] + matchingMap.get(molecule), matchingMap.get(molecule) + elements[1]};
            long delta = moleculeMap.get(molecule).get();
            for (String newMolecule : addedMolecules) {
                if (newMoleculeMap.containsKey(newMolecule)) {
                    newMoleculeMap.get(newMolecule).addAndGet(delta);
                } else {
                    newMoleculeMap.put(newMolecule, new AtomicLong(delta));
                }
            }
        });

        return newMoleculeMap;
    }

    private Map<String, AtomicLong> countElements(Map<String, AtomicLong> moleculeMap) {
        Map<String, AtomicLong> elementsMap = new HashMap<>();

        moleculeMap.forEach((molecule, quantity) -> {
            String element = molecule.split("")[0];
            if (elementsMap.containsKey(element)) {
                elementsMap.get(element).addAndGet(quantity.get());
            } else {
                elementsMap.put(element, quantity);
            }
        });

        elementsMap.get(startingMolecule[startingMolecule.length - 1]).incrementAndGet();

        return elementsMap;
    }

}
