package fr.aoc.session2021;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class Day9 {

    List<List<Integer>> globalHeightmap;


    public static void main(String[] args) {
        Day9 day9 = new Day9();
        day9.globalHeightmap = day9.readInput("src/main/resources/2021/day9/input.txt");
        log.info("Part 1 answer : {}", day9.sumOfRisk());
        ArrayList<Integer> bassins = day9.calcSizeOfbassins();
        Collections.sort(bassins, Collections.reverseOrder());
        log.info("Bassins : {}", bassins);
        log.info("Part 2 answer : {}", bassins.get(0) * bassins.get(1) * bassins.get(2));
    }

    private List<List<Integer>> readInput(String filePath) {
        List<List<Integer>> heightmap = null;

        try (FileInputStream fis = new FileInputStream(filePath)) {
            String inputStr = IOUtils.toString(fis, StandardCharsets.UTF_8);
            heightmap = Arrays.stream(inputStr.split("\\D"))
                    .filter(line -> line != null && !line.isEmpty() && !line.trim().isEmpty())
                    .map(line -> Arrays.stream(line.split("")).map(Integer::parseInt).toList())
                    .toList();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return heightmap;
    }

    private ArrayList<Integer> calcSizeOfbassins() {
        ArrayList<Integer> bassinsSize = new ArrayList<>();

        for (int indexLine = 0; indexLine < globalHeightmap.size(); indexLine++) {
            for (int indexCol = 0; indexCol < globalHeightmap.get(indexLine).size(); indexCol++) {
                int bassinSize = 0;
                if(isLocalMinimum(indexLine, indexCol, globalHeightmap)) {
                    ArrayList<ArrayList<Integer>> workingHeightmap = globalHeightmap.stream().map(line -> new ArrayList<>(line)).collect(Collectors.toCollection(ArrayList::new));
                    bassinsSize.add(calcBassinSizeFromMin(indexLine, indexCol, workingHeightmap, 0));
                }
            }
        }

        return bassinsSize;
    }

    private int sumOfRisk() {
        int riskTotal = 0;

        for (int indexLine = 0; indexLine < globalHeightmap.size(); indexLine++) {
            for (int indexCol = 0; indexCol < globalHeightmap.get(indexLine).size(); indexCol++) {
                if(isLocalMinimum(indexLine, indexCol, globalHeightmap)) riskTotal += globalHeightmap.get(indexLine).get(indexCol) + 1;
            }
        }

        return riskTotal;
    }

    private boolean isLocalMinimum(int line, int col, List<List<Integer>> heightmap) {
        boolean minHorizontal = true;
        boolean minVertical = true;

        if (col == 0) minVertical = minVertical && (heightmap.get(line).get(col) < heightmap.get(line).get(col + 1));
        else if (col == heightmap.get(line).size() - 1) minVertical = minVertical && (heightmap.get(line).get(col) < heightmap.get(line).get(col - 1));
        else minVertical = minVertical && (heightmap.get(line).get(col) < heightmap.get(line).get(col - 1))
                                        && (heightmap.get(line).get(col) < heightmap.get(line).get(col + 1));

        if (line == 0) minHorizontal = minHorizontal && (heightmap.get(line).get(col) < heightmap.get(line + 1).get(col));
        else if (line == heightmap.size() - 1) minHorizontal = minHorizontal && (heightmap.get(line).get(col) < heightmap.get(line - 1).get(col));
        else minHorizontal = minHorizontal && (heightmap.get(line).get(col) < heightmap.get(line - 1).get(col))
                    && (heightmap.get(line).get(col) < heightmap.get(line + 1).get(col));

        return minHorizontal && minVertical;
    }

    private int calcBassinSizeFromMin(int line, int col, ArrayList<ArrayList<Integer>> heightmap, int size) {
        heightmap.get(line).set(col, 9);

        if (line > 0 && heightmap.get(line - 1).get(col) != 9 && globalHeightmap.get(line).get(col) < heightmap.get(line - 1).get(col)) {
            size += calcBassinSizeFromMin(line - 1, col, heightmap, 0);
        }
        if (col > 0 && heightmap.get(line).get(col - 1) != 9 && globalHeightmap.get(line).get(col) < heightmap.get(line).get(col - 1)) {
            size += calcBassinSizeFromMin(line, col - 1, heightmap, 0);
        }
        if (line < heightmap.size() - 1 && heightmap.get(line + 1).get(col) != 9 && globalHeightmap.get(line).get(col) < heightmap.get(line + 1).get(col)) {
            size += calcBassinSizeFromMin(line + 1, col, heightmap, 0);
        }
        if (col < heightmap.get(line).size() - 1 && heightmap.get(line).get(col + 1) != 9 && globalHeightmap.get(line).get(col) < heightmap.get(line).get(col + 1)) {
            size += calcBassinSizeFromMin(line, col + 1, heightmap, 0);
        }

        return size + 1;
    }

}
