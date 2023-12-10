package fr.aoc.session2023;

import fr.aoc.common.Utils;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
public class Day09 {

    public static void main(String[] args) throws IOException {
        var today = new Day09();
        var input = Utils.readInputSplitOnNewLines("src/main/resources/2023/day09/input").stream()
                .map(line -> Arrays.stream(line.split("\\s+")).map(Long::parseLong).toList())
                .toList();

        var results = input.stream()
                .map(today::extrapolate)
                .toList();
        var sum1 = results.stream().map(value -> value.get(1)).mapToLong(Long::longValue).sum();
        var sum2 = results.stream().map(value -> value.get(0)).mapToLong(Long::longValue).sum();
        log.info("Valeurs extrapolées : {}", results);
        log.info("Somme forward : {}", sum1);
        log.info("Somme backward : {}", sum2);
    }

    private List<Long> extrapolate(List<Long> measures) {
        List<List<Long>> extrapolatedMeasures = new ArrayList<>();
        extrapolatedMeasures.add(new ArrayList<>(measures));

        var workingMeasures = extrapolatedMeasures.get(0);
        while (!isAllZeroes(workingMeasures)) {
            extrapolatedMeasures.add(calculateNextStep(workingMeasures));
            workingMeasures = extrapolatedMeasures.get(extrapolatedMeasures.size() - 1);
        }

        return List.of(calcPreviousValue(extrapolatedMeasures), calcNextValue(extrapolatedMeasures));
    }

    private List<Long> calculateNextStep(List<Long> previousStep) {
        return IntStream.range(1, previousStep.size())
                .mapToObj(index -> previousStep.get(index) - previousStep.get(index - 1))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    private long calcNextValue(List<List<Long>> extrapolatedMeasures) {
        var workingArray = new ArrayList<>(extrapolatedMeasures);
        IntStream.range(0, workingArray.size() - 1).boxed().sorted(Comparator.reverseOrder())
                .forEach(index -> {
                    var step = workingArray.get(index);
                    var nextStep = workingArray.get(index + 1);
                    step.add(step.get(step.size() - 1) + nextStep.get(nextStep.size() - 1));
                });
        return workingArray.get(0).get(workingArray.get(0).size() - 1);
    }

    private long calcPreviousValue(List<List<Long>> extrapolatedMeasures) {
        var workingArray = new ArrayList<>(extrapolatedMeasures);
        IntStream.range(0, workingArray.size() - 1).boxed().sorted(Comparator.reverseOrder())
                .forEach(index -> {
                    var step = workingArray.get(index);
                    var nextStep = workingArray.get(index + 1);
                    step.add(0, step.get(0) - nextStep.get(0));
                });
        return workingArray.get(0).get(0);
    }

    private boolean isAllZeroes(List<Long> measures) {
        return measures.stream().allMatch(data -> data == 0);
    }

}
