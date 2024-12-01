package fr.aoc.session2022;

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
public class Day8 {

    private List<List<Integer>> scenicScore;

    public static void main(String[] args) throws IOException {
        Day8 day8 = new Day8();
        var forest = day8.readInput("src/main/resources/2022/day8/input.txt");

        int answer1 = day8.processAnswers(forest);
        int answer2 = day8.scenicScore.stream().mapToInt(line -> line.stream().mapToInt(Integer::intValue).max().getAsInt()).max().getAsInt();

        log.info("Answer 1 : {}", answer1);
        log.info("Answer 2 : {}", answer2);
    }

    public List<List<Integer>> readInput(String filepath) throws IOException {
        try (FileInputStream fis = new FileInputStream(filepath)) {
            return Arrays.stream(IOUtils.toString(fis, StandardCharsets.UTF_8).split(REGEX_NEW_LINE))
                    .map(treeLine -> Arrays.stream(treeLine.split(""))
                            .mapToInt(Integer::parseInt)
                            .boxed()
                            .toList())
                    .toList();
        }
    }
    
    public int processAnswers(List<List<Integer>> forest) {
        int squareForestSize = forest.size();
        int counter = (squareForestSize + squareForestSize - 2) * 2;
        
        if (squareForestSize == 2) return counter;

        scenicScore = Stream.generate(
                        () -> Stream.generate(() -> -1)
                                .limit(squareForestSize)
                                .collect(Collectors.toCollection(ArrayList::new)))
                .limit(squareForestSize)
                .collect(Collectors.toCollection(ArrayList::new));

        for (int lineIndex = 1; lineIndex < squareForestSize - 1; lineIndex++) {
            for (int colIndex = 1; colIndex < squareForestSize - 1; colIndex++) {
                if (isVisible(forest, lineIndex, colIndex)) counter++;
            }
        }
        
        return counter;
    }

    public boolean isVisible(List<List<Integer>> forest, int line, int col) {
        int treeSize = forest.get(line).get(col);
        var west = forest.get(line).subList(0, col);
        var east = forest.get(line).subList(col + 1, forest.size());
        var north = forest.stream().map(treeLine -> treeLine.get(col)).toList().subList(0, line);
        var south = forest.stream().map(treeLine -> treeLine.get(col)).toList().subList(line + 1, forest.size());

        int score = calcScoreForLine(treeSize, west, true)
                * calcScoreForLine(treeSize, east, false)
                * calcScoreForLine(treeSize, north, true)
                * calcScoreForLine(treeSize, south, false);
        scenicScore.get(line).set(col, score);

        return west.stream().noneMatch(size -> size >= treeSize)
                || east.stream().noneMatch(size -> size >= treeSize)
                || north.stream().noneMatch(size -> size >= treeSize)
                || south.stream().noneMatch(size -> size >= treeSize);
    }

    private int calcScoreForLine(int treeSize, List<Integer> line, boolean isLastIndex) {
        if (!isLastIndex) {
            for (int index = 0; index < line.size(); index++) {
                if (line.get(index) >= treeSize) return index + 1;
            }
        } else {
            for (int index = line.size() - 1; 0 <= index; index--) {
                if (line.get(index) >= treeSize) return line.size() - index;
            }
        }

        return line.size();
    }
}
