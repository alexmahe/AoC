package fr.aoc.session2021;

import org.apache.commons.io.IOUtils;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Day10 {

    private static final String OPENING_CHARS = "([{<";
    private final ArrayList<String> illegalChars = new ArrayList<>();
    private final ArrayList<ArrayList<String>> incompleteLists = new ArrayList<>();

    public static void main(String[] args) {
        Day10 day10 = new Day10();
        List<List<String>> lines = day10.readInput("src/main/resources/2021/day10/input.txt");
        day10.buildCharsList(lines);

        System.out.printf("Score illegal : %s%n", day10.calcIllegalScore(day10.illegalChars));

        ArrayList<ArrayList<String>> completingLists = day10.buildCompletingLists(day10.incompleteLists);
        System.out.printf("Score incomplete : %s%n", day10.calcIncompleteScore(completingLists));
    }

    private List<List<String>> readInput(String filePath) {
        List<List<String>> inputLines = null;

        try (FileInputStream fis = new FileInputStream(filePath)) {
            String inputStr = IOUtils.toString(fis, StandardCharsets.UTF_8);
            inputLines = Arrays.stream(inputStr.split("(\r\n|\r|\n)"))
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
            ArrayList<String> charBuffer = new ArrayList<>();
            int symbolIndex = 0;
            boolean corrupted = false;

            while (!corrupted && symbolIndex < line.size()) {
                String symbol = line.get(symbolIndex);

                if (OPENING_CHARS.contains(symbol)) {
                    charBuffer.add(symbol);
                } else {
                    if (closingCharMatchOpening(charBuffer.get(charBuffer.size() - 1), symbol)) {
                        charBuffer.remove(charBuffer.size() - 1);
                    } else {
                        corrupted = true;
                        illegalChars.add(symbol);
                    }
                }

                symbolIndex++;
            }

            if (!corrupted) incompleteLists.add(new ArrayList<>(charBuffer));
        });
    }

    private ArrayList<ArrayList<String>> buildCompletingLists(ArrayList<ArrayList<String>> incompleteLists) {
        ArrayList<ArrayList<String>> completingLists = new ArrayList<>();

        incompleteLists.forEach(line -> {
            completingLists.add(new ArrayList<>());
            line.forEach(symbol -> completingLists.get(completingLists.size() - 1).add(0, findMatchingClosingChar(symbol)));
        });

        return completingLists;
    }

    private int calcIllegalScore(ArrayList<String> illegalChars) {
        return illegalChars.stream().map(symbol -> {
            return switch (symbol) {
                case ")" -> 3;
                case "}" -> 1197;
                case "]" -> 57;
                case ">" -> 25137;
                default -> 0;
            };
        }).reduce(0, Integer::sum);
    }

    private long calcIncompleteScore(ArrayList<ArrayList<String>> completingLists) {
        List<Long> scoreList = completingLists.stream().map(line -> {
            return line.stream().map(symbol -> {
                return switch (symbol) {
                    case ")" -> 1L;
                    case "}" -> 3L;
                    case "]" -> 2L;
                    case ">" -> 4L;
                    default -> 0L;
                };
            }).reduce(0L, (a, b) -> (5 * a) + b);
        }).sorted().toList();

        return scoreList.get(scoreList.size() / 2);
    }

    private boolean closingCharMatchOpening(String opening, String closing) {
        return switch (closing) {
            case ")" -> "(".equals(opening);
            case "}" -> "{".equals(opening);
            case "]" -> "[".equals(opening);
            case ">" -> "<".equals(opening);
            default -> throw new IllegalArgumentException("Unrecognized char");
        };
    }

    private String findMatchingClosingChar(String opening) {
        return switch (opening) {
            case "(" -> ")";
            case "{" -> "}";
            case "[" -> "]";
            case "<" -> ">";
            default -> throw new IllegalArgumentException("Unrecognized char");
        };
    }

}
