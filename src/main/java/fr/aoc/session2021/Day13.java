package fr.aoc.session2021;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static fr.aoc.common.Utils.REGEX_NEW_LINE;

@Slf4j
public class Day13 {

    ArrayList<ArrayList<String>> map;
    List<List<String>> instructions;

    public static void main(String[] args) {
        Day13 day13 = new Day13();
        day13.readInput("src/main/resources/2021/day13/input.txt");

        ArrayList<ArrayList<String>> folded = day13.fold(day13.instructions.get(0), day13.map);
        int answerP1 = folded.stream()
                .map(line -> line.stream()
                        .filter("#"::equals)
                        .map(symbol -> 1)
                        .reduce(0, Integer::sum))
                .reduce(0, Integer::sum);
        log.info("Answer part 1 : {}", answerP1);

        for (int index = 1; index < day13.instructions.size(); index++) {
            folded = day13.fold(day13.instructions.get(index), folded);
        }
        log.info("final fold : \n{}", day13.formattingArrayForLog(folded));
    }

    private void readInput(String filePath) {
        List<List<Integer>> inputLines;

        try (FileInputStream fis = new FileInputStream(filePath)) {
            String inputStr = IOUtils.toString(fis, StandardCharsets.UTF_8);
            inputLines = Arrays.stream(inputStr.split(REGEX_NEW_LINE))
                    .filter(line -> line != null && !line.isEmpty() && !line.trim().isEmpty())
                    .filter(line -> !line.startsWith("fold"))
                    .map(line -> Arrays.stream(line.split(",")).map(Integer::parseInt).toList())
                    .toList();
            instructions = Arrays.stream(inputStr.split(REGEX_NEW_LINE))
                    .filter(line -> line.startsWith("fold"))
                    .map(line -> Arrays.stream(line.split("=")).toList())
                    .toList();

            int maxLine = inputLines.stream().map(line -> line.get(1)).mapToInt(x -> x).max().orElse(0) + 1;
            int maxCol = inputLines.stream().map(line -> line.get(0)).mapToInt(x -> x).max().orElse(0) + 1;
            map = Stream.generate(() -> Stream.generate(() -> ".")
                                            .limit(maxCol)
                                            .collect(Collectors.toCollection(ArrayList::new)))
                    .limit(maxLine)
                    .collect(Collectors.toCollection(ArrayList::new));
            inputLines.forEach(line -> map.get(line.get(1)).set(line.get(0), "#"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private ArrayList<ArrayList<String>> fold(List<String> instruction, ArrayList<ArrayList<String>> map) {
        if (instruction.get(0).endsWith("x")) {
            return foldVertical(Integer.parseInt(instruction.get(1)), map);
        } else {
            return foldHorizontal(Integer.parseInt(instruction.get(1)), map);
        }
    }

    private ArrayList<ArrayList<String>> foldHorizontal(int foldingLine, ArrayList<ArrayList<String>> map) {
        ArrayList<ArrayList<String>> newMap = new ArrayList<>();

        for (int lineIndex = 0; lineIndex < foldingLine; lineIndex++) {
            newMap.add(map.get(lineIndex));
        }

        for (int lineIndex = foldingLine + 1; lineIndex < map.size(); lineIndex++) {
            for (int colIndex = 0; colIndex < map.get(lineIndex).size(); colIndex++) {
                if ("#".equals(map.get(lineIndex).get(colIndex))) {
                    newMap.get(foldingLine - (lineIndex - foldingLine)).set(colIndex, "#");
                }
            }
        }

        return newMap;
    }

    private ArrayList<ArrayList<String>> foldVertical(int foldingCol, ArrayList<ArrayList<String>> map) {
        ArrayList<ArrayList<String>> newMap = new ArrayList<>();

        for (ArrayList<String> line : map) {
            ArrayList<String> newCol = new ArrayList<>();

            for (int colIndex = 0; colIndex < foldingCol; colIndex++) {
                newCol.add(line.get(colIndex));
            }

            newMap.add(newCol);
        }

        for (int lineIndex = 0; lineIndex < map.size(); lineIndex++) {
            for (int colIndex = foldingCol + 1; colIndex < map.get(lineIndex).size(); colIndex++) {
                if ("#".equals(map.get(lineIndex).get(colIndex))) {
                    newMap.get(lineIndex).set(foldingCol - (colIndex - foldingCol), "#");
                }
            }
        }

        return newMap;
    }

    private String formattingArrayForLog(ArrayList<ArrayList<String>> array) {
        return array.stream().map(boardLine -> boardLine.stream().map(String::valueOf).collect(Collectors.joining(" "))).collect(Collectors.joining("\n"));
    }
}