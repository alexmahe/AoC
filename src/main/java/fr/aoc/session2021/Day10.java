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

import static fr.aoc.common.Utils.REGEX_NEW_LINE;

@Slf4j
public class Day10 {

    private static final String OPENING_CHARS = "([{<";
    private static final Map<String, String> MATCHING_CHARS = Map.of("(", ")", "[", "]", "{", "}", "<", ">");
    private static final Map<String, Integer> SCORE_PART_1 = Map.of(")", 3, "]", 57, "}", 1197, ">", 25137);
    private static final Map<String, Long> SCORE_PART_2 = Map.of(")", 1L, "]", 2L, "}", 3L, ">", 4L);
    private final ArrayList<String> illegalChars = new ArrayList<>();
    private final ArrayList<ArrayList<String>> incompleteLists = new ArrayList<>();

    public static void main(String[] args) {
        Day10 day10 = new Day10();
        List<List<String>> lines = day10.readInput("src/main/resources/2021/day10/input.txt");
        day10.buildCharsList(lines);

        log.info("Score illegal : {}", day10.calcIllegalScore(day10.illegalChars));

        ArrayList<ArrayList<String>> completingLists = day10.buildCompletingLists(day10.incompleteLists);
        log.info("Score incomplete : {}", day10.calcIncompleteScore(completingLists));
    }

    private List<List<String>> readInput(String filePath) {
        List<List<String>> inputLines = null;

        try (FileInputStream fis = new FileInputStream(filePath)) {
            String inputStr = IOUtils.toString(fis, StandardCharsets.UTF_8);
            inputLines = Arrays.stream(inputStr.split(REGEX_NEW_LINE))
                    .filter(line -> line != null && !line.isEmpty() && !line.trim().isEmpty())
                    .map(line -> Arrays.stream(line.split("")).toList())
                    .toList();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return inputLines;
    }

    private void buildCharsList(List<List<String>> inputLines) {
        inputLines.forEach(line -> {
            ArrayList<String> symbolBuffer = new ArrayList<>();
            int symbolIndex = 0;
            boolean corrupted = false;

            while (!corrupted && symbolIndex < line.size()) {
                String symbol = line.get(symbolIndex);

                if (OPENING_CHARS.contains(symbol)) {
                    symbolBuffer.add(symbol);
                } else {
                    if (symbol.equals(MATCHING_CHARS.get(symbolBuffer.get(symbolBuffer.size() - 1)))) {
                        symbolBuffer.remove(symbolBuffer.size() - 1);
                    } else {
                        corrupted = true;
                        illegalChars.add(symbol);
                    }
                }

                symbolIndex++;
            }

            if (!corrupted) incompleteLists.add(new ArrayList<>(symbolBuffer));
        });
    }

    private ArrayList<ArrayList<String>> buildCompletingLists(ArrayList<ArrayList<String>> incompleteLists) {
        ArrayList<ArrayList<String>> completingLists = new ArrayList<>();

        incompleteLists.forEach(line -> {
            completingLists.add(new ArrayList<>());
            line.forEach(symbol -> completingLists.get(completingLists.size() - 1).add(0, MATCHING_CHARS.get(symbol)));
        });

        return completingLists;
    }

    private int calcIllegalScore(ArrayList<String> illegalChars) {
        return illegalChars.stream().map(SCORE_PART_1::get).reduce(0, Integer::sum);
    }

    private long calcIncompleteScore(ArrayList<ArrayList<String>> completingLists) {
        List<Long> scoreList = completingLists.stream().map(line -> {
            return line.stream().map(SCORE_PART_2::get).reduce(0L, (a, b) -> (5 * a) + b);
        }).sorted().toList();

        return scoreList.get(scoreList.size() / 2);
    }
    
}
