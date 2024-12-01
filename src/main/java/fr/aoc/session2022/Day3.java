package fr.aoc.session2022;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static fr.aoc.common.Utils.REGEX_NEW_LINE;

@Slf4j
public class Day3 {

    private final String alphabet = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

    public static void main(String[] args) throws IOException {
        Day3 day3 = new Day3();

        String input = day3.readInput("src/main/resources/2022/day3/input.txt");
        int answer1 = day3.processMisplacedItems(input);
        int answer2 = day3.processBadges(input);

        log.info("Score (answer 1) : {}", answer1);
        log.info("Score (answer 2) : {}", answer2);
    }

    private String readInput(String filepath) throws IOException {
        try (FileInputStream fis = new FileInputStream(filepath)) {
            return IOUtils.toString(fis, StandardCharsets.UTF_8);
        }
    }

    private int processMisplacedItems(String input) {
        return Arrays.stream(input.split(REGEX_NEW_LINE))
                .mapToInt(this::calcMisplacedItemValue)
                .sum();
    }

    private int calcMisplacedItemValue(String rucksack) {
        String compartmentA = rucksack.substring(0, rucksack.length() / 2);
        String compartmentB = rucksack.substring(rucksack.length() / 2);

        return findCommonCharValueInStrings(Arrays.asList(compartmentA, compartmentB)).orElseThrow(RuntimeException::new);
    }

    private int processBadges(String input) {
        List<String> stringsList = Arrays.stream(input.split(REGEX_NEW_LINE)).toList();
        int sum = 0;

        for (int listIndex = 0; listIndex < stringsList.size() - 2; listIndex += 3) {
            sum += findCommonCharValueInStrings(Arrays.asList(stringsList.get(listIndex), stringsList.get(listIndex + 1), stringsList.get(listIndex + 2))).orElseThrow(RuntimeException::new);
        }

        return sum;
    }

    private Optional<Integer> findCommonCharValueInStrings(List<String> stringsToCheck) {
        List<Set<String>> setsOfChar = stringsToCheck.stream()
                .map(string -> Arrays.stream(string.split(""))
                        .collect(Collectors.toSet()))
                .toList();

        return setsOfChar.get(0).stream()
                .filter(commonChar -> isCharPresentInAllSets(commonChar, setsOfChar))
                .map(commonChar -> alphabet.indexOf(commonChar) + 1)
                .findFirst();
    }

    private boolean isCharPresentInAllSets(String commonChar, List<Set<String>> charSetsList) {
        long matchingSets = charSetsList.stream()
                .filter(charSet -> charSet.contains(commonChar))
                .count();

        return matchingSets == charSetsList.size();
    }
}
