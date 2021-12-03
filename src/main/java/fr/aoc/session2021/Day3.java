package fr.aoc.session2021;

import org.apache.commons.io.IOUtils;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class Day3 {

    public static void main(String[] args) {
        // GIVEN
        Day3 day3 = new Day3();
        List<List<Integer>> diagnostics = day3.readInput("src/main/resources/2021/day3/input.txt");
        String mostCommonBits = day3.calcMostCommonBits(diagnostics);
        Map<String, Integer> rates = day3.calcRates(mostCommonBits);

        // Partie 1
        System.out.println(mostCommonBits);
        System.out.println(rates);
        System.out.println(rates.get("gammaRate") * rates.get("epsilonRate"));
    }

    private List<List<Integer>> readInput(String filePath) {
        List<List<Integer>> diagnostic = null;

        try (FileInputStream fis = new FileInputStream(filePath)) {
            String inputStr = IOUtils.toString(fis, StandardCharsets.UTF_8);
            diagnostic = Arrays.stream(inputStr.split("\\D"))
                    .filter(element -> element != null && !element.isEmpty() && !element.trim().isEmpty())
                    .map(
                            diagnosticLine -> Arrays.stream(diagnosticLine.split(""))
                            .map(Integer::parseInt)
                            .toList()
                    )
                    .toList();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return diagnostic;
    }

    private String calcMostCommonBits(List<List<Integer>> diagnostic) {
        StringBuilder result = new StringBuilder();

        for (int index = 0; index < diagnostic.get(0).size(); index++) {
            result.append(calcMostCommonBitForPos(diagnostic, index));
        }

        return result.toString();
    }

    private String calcMostCommonBitForPos(List<List<Integer>> diagnostic, int position) {
        int numberOfOnes = 0;

        for (List<Integer> diagnosticLine : diagnostic) {
            if (diagnosticLine.get(position) == 0) numberOfOnes++;
        }

        return numberOfOnes > diagnostic.size() / 2 ? "1" : "0";
    }

    private Map<String, Integer> calcRates(String mostCommonBits) {
        String leastCommonBits = mostCommonBits.replace("1", "x")
                                    .replace("0", "1")
                                    .replace("x", "0");
        int gammaRate = Integer.parseInt(mostCommonBits, 2);
        int espilonRate = Integer.parseInt(leastCommonBits, 2);

        return Map.of("gammaRate", gammaRate, "epsilonRate", espilonRate);
    }
}
