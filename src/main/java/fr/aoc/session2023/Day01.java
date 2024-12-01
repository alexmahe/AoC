package fr.aoc.session2023;

import fr.aoc.common.Utils;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.regex.Pattern;

@Slf4j
public class Day01 {

    private final Map<String, String> conversionMap = Map.of(
            "one", "1",
            "two", "2",
            "three", "3",
            "four", "4",
            "five", "5",
            "six", "6",
            "seven","7",
            "eight", "8",
            "nine", "9"
            );

    public static void main(String[] args) throws IOException {
        var today = new Day01();
        var input = Utils.readInputSplitOnNewLines("src/main/resources/2023/day01/input");
        var calibrationValue = today.computeCalibrationValue(input);

        var parsedSpelledOut = input.stream()
                .map(today::computeCalibrationValueWithSpelledOutDigit)
                .mapToInt(Integer::intValue)
                .sum();

        log.info("Calibration value : {}", calibrationValue);
        log.info("Parsed calibration value : {}", parsedSpelledOut);
    }

    private int computeCalibrationValue(List<String> input) {
        return input.stream()
                .map(s -> s.replaceAll("\\D", ""))
                .filter(Predicate.not(String::isEmpty))
                .map(s -> String.valueOf(s.charAt(0)) + s.charAt(s.length() - 1))
                .mapToInt(Integer::parseInt)
                .sum();
    }

    private int computeCalibrationValueWithSpelledOutDigit(String line) {
        var regex = Pattern.compile("(?=([0-9]|" + String.join("|", conversionMap.keySet()) + "))");
        List<String> matches = regex.matcher(line).results().map(matchResult -> matchResult.group(1)).toList();

        var digitList = matches.stream()
                .map(s -> conversionMap.getOrDefault(s, s))
                .toList();
        return Integer.parseInt(digitList.get(0) + digitList.get(digitList.size() - 1));
    }

}
