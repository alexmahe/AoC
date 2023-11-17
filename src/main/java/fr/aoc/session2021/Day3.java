package fr.aoc.session2021;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public class Day3 {

    public static void main(String[] args) {
        // GIVEN
        Day3 day3 = new Day3();
        List<List<Integer>> diagnostics = day3.readInput("src/main/resources/2021/day3/input.txt");
        String mostCommonBits = day3.calcMostCommonBits(diagnostics);
        Map<String, Integer> gAndERates = day3.calcGAndERates(mostCommonBits);

        // Partie 1
        log.info(mostCommonBits);
        log.info(gAndERates.toString());
        log.info(String.valueOf(gAndERates.get("gammaRate") * gAndERates.get("epsilonRate")));

        // Partie 2
        Map<String, Integer> oAndCO2Rates = day3.filterBitCriteria(diagnostics);
        log.info(oAndCO2Rates.toString());
        log.info(String.valueOf(oAndCO2Rates.get("oxygenRate") * oAndCO2Rates.get("co2Rate")));
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
    
    private Map<String, Integer> filterBitCriteria(List<List<Integer>> diagnostic) {
        List<List<Integer>> oxygenFilter = diagnostic;
        List<List<Integer>> co2Filter = diagnostic;
        
        for (int index = 0; index < diagnostic.get(0).size(); index++) {
            int finalIndex = index;

            if (oxygenFilter.size() > 1) {
                int oxygenMostCommonBit = Integer.parseInt(calcMostCommonBitForPos(oxygenFilter, index));
                oxygenFilter = oxygenFilter.stream()
                        .filter(element -> element.get(finalIndex) == oxygenMostCommonBit)
                        .toList();
            }

            if (co2Filter.size() > 1) {
                int co2MostCommonBit = Integer.parseInt(calcMostCommonBitForPos(co2Filter, index));
                co2Filter = co2Filter.stream()
                        .filter(element -> element.get(finalIndex) == 1 - co2MostCommonBit)
                        .toList();
            }
        }

        int oxygenRate = Integer.parseInt(
                            oxygenFilter.get(0).stream()
                                .map(String::valueOf)
                                .collect(Collectors.joining()),
                            2
        );
        int co2Rate = Integer.parseInt(
                            co2Filter.get(0).stream()
                                .map(String::valueOf)
                                .collect(Collectors.joining()),
                            2
        );

        log.info(oxygenFilter.toString());
        log.info(co2Filter.toString());
        
        return Map.of("oxygenRate", oxygenRate, "co2Rate", co2Rate);
    }

    private String calcMostCommonBitForPos(List<List<Integer>> diagnostic, int position) {
        int numberOfOnes = 0;
        int threshold = diagnostic.size() % 2 == 0 ? diagnostic.size() / 2 : diagnostic.size() / 2 + 1;

        for (List<Integer> diagnosticLine : diagnostic) {
            if (diagnosticLine.get(position) == 1) numberOfOnes++;
        }

        return numberOfOnes >= threshold ? "1" : "0";
    }

    private Map<String, Integer> calcGAndERates(String mostCommonBits) {
        String leastCommonBits = mostCommonBits.replace("1", "x")
                                    .replace("0", "1")
                                    .replace("x", "0");
        int gammaRate = Integer.parseInt(mostCommonBits, 2);
        int espilonRate = Integer.parseInt(leastCommonBits, 2);

        return Map.of("gammaRate", gammaRate, "epsilonRate", espilonRate);
    }
}
