package fr.aoc.session2023;

import fr.aoc.common.Utils;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;

import static fr.aoc.common.Utils.NUMBER_PATTERN;

@Slf4j
public class Day03 {

    private static final Pattern SYMBOL_PATTERN = Pattern.compile("[^\\w\\s\\\\.]");
    private static  final Pattern GEAR_PATTERN = Pattern.compile("\\*");

    public static void main(String[] args) throws IOException {
        var today = new Day03();
        var input = Utils.readInputSplitOnNewLines("src/main/resources/2023/day03/input");

        var validNumbers = today.parseValidNumbers(input);
        var sumOfValidNumbers = validNumbers.stream()
                .mapToInt(Integer::intValue)
                .sum();
        log.info("Somme des nombres valides : {}", sumOfValidNumbers);

        var gears = today.parseGear(input);
        var sumOfRatios = gears.stream()
                .filter(integers -> integers.size() == 2)
                .map(integers -> integers.get(0) * integers.get(1))
                .mapToInt(Integer::intValue)
                .sum();
        log.info("Somme des ratio des gears : {}", sumOfRatios);
    }

    private List<List<Integer>> parseGear(List<String> input) {
        var gearsNumbers = new ArrayList<List<Integer>>();

        for (int lineIndex = 0; lineIndex < input.size(); lineIndex++) {
            String line = input.get(lineIndex);
            int finalLineIndex = lineIndex;
            gearsNumbers.addAll(GEAR_PATTERN.matcher(line).results()
                    .map(matchResult -> scanAroundGear(input, finalLineIndex, matchResult.start()))
                    .toList());
        }

        return gearsNumbers;
    }

    private List<Integer> scanAroundGear(List<String> input, int lineIndex, int indexInLine) {
        var numbersAround = new ArrayList<Integer>();
        String line = input.get(lineIndex);

        // On check après *
        scanBeforeOrAfterGear(indexInLine, line.length(), line, matchResult -> matchResult.start() == indexInLine + 1)
                .ifPresent(numbersAround::add);
        // On check avant *
        scanBeforeOrAfterGear(0, indexInLine, line, matchResult -> matchResult.end() == indexInLine)
                .ifPresent(numbersAround::add);

        if (lineIndex > 0) numbersAround.addAll(scanOverOrUnderGear(input.get(lineIndex - 1), indexInLine));
        if (lineIndex < input.size()) numbersAround.addAll(scanOverOrUnderGear(input.get(lineIndex + 1), indexInLine));

        return numbersAround;
    }

    private static Optional<Integer> scanBeforeOrAfterGear(int start, int end, String line, Predicate<MatchResult> filter) {
        return NUMBER_PATTERN.matcher(line).region(start, end).results()
                .filter(filter)
                .map(MatchResult::group)
                .map(Integer::parseInt)
                .findFirst();
    }

    private List<Integer> scanOverOrUnderGear(String line, int indexInLine) {
        return NUMBER_PATTERN.matcher(line).results()
                .filter(matchResult ->
                        (matchResult.start() < indexInLine && matchResult.end() - 1 > indexInLine)
                                || (matchResult.start() == indexInLine || matchResult.start() == indexInLine + 1)
                                || (matchResult.end() - 1 == indexInLine || matchResult.end() == indexInLine))
                .map(MatchResult::group)
                .map(Integer::parseInt)
                .toList();
    }

    private List<Integer> parseValidNumbers(List<String> input) {
        var validNumbers = new ArrayList<Integer>();

        for (int lineIndex = 0; lineIndex < input.size(); lineIndex++) {
            String line = input.get(lineIndex);
            int finalLineIndex = lineIndex;
            validNumbers.addAll(NUMBER_PATTERN.matcher(line).results()
                    .filter(matchResult -> isNumberValid(input, finalLineIndex, matchResult.start(), line, matchResult.group(0).length())).map(matchResult -> Integer.parseInt(matchResult.group(0)))
                    .toList()
            );
        }

        return validNumbers;
    }

    private boolean isNumberValid(List<String> input, int lineIndex, int numberIndexInLine, String line, int length) {
        var outline = "";

        if (lineIndex > 0) outline += input.get(lineIndex - 1).substring(Math.max(0, numberIndexInLine - 1), Math.min(line.length(), numberIndexInLine + length + 1));
        if (lineIndex < input.size() - 2) outline += input.get(lineIndex + 1).substring(Math.max(0, numberIndexInLine - 1), Math.min(line.length(), numberIndexInLine + length + 1));
        if (numberIndexInLine > 0) outline += line.charAt(numberIndexInLine - 1);
        if (numberIndexInLine + length < line.length()) outline += line.charAt(numberIndexInLine + length);

        return SYMBOL_PATTERN.matcher(outline).find();
    }
}
