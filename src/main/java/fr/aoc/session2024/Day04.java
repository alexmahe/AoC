package fr.aoc.session2024;

import fr.aoc.common.Utils;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
public class Day04 {

    private final String XMAS = "XMAS";
    private final String SAMX = "SAMX";
    private final String MAS = "MAS";
    private final String SAM = "SAM";
    private final Integer[][] directions = {{-1,0}, {-1, 1}, {0, 1}, {1, 1}, {1, 0}, {1, -1}, {0, -1}, {-1, -1}};
    private final Integer[][][] scheme = {
            {{-1, -1}, {0, 0}, {1, 1}}, // Diagonale NO-SE
            {{1, -1}, {0, 0}, {-1, 1}}  // Diagonale SO-NE
    };

    public static void main(String[] args) throws IOException {
        var today = new Day04();
        var input = Utils.readInputSplitOnNewLines("src/main/resources/2024/day04/input").stream()
                .map(lines -> Arrays.stream(lines.split("")).toList())
                .toList();

        var total = today.iterateThroughArary(input);
        log.info("XMAS trouvé {} fois dans l'input", total[0]);
        log.info("X-MAS trouvé {} fois dans l'input", total[1]);
    }

    private long[] iterateThroughArary(List<List<String>> data) {
        var matchs = new long[]{0L, 0L};
        for (int lineIndex = 0; lineIndex < data.size(); lineIndex++) {
            for (int colIndex = 0; colIndex < data.get(lineIndex).size(); colIndex++) {
                if (data.get(lineIndex).get(colIndex).equalsIgnoreCase("X")) {
                    matchs[0] += countXMASFordirections(data, new int[]{lineIndex, colIndex});
                }
                if (data.get(lineIndex).get(colIndex).equalsIgnoreCase("A")) {
                    matchs[1] += checkForScheme(data, new int[]{lineIndex, colIndex}) ? 1L : 0L;
                }
            }
        }
        return matchs;
    }

    private long countXMASFordirections(List<List<String>> data, int[] startingPoint) {
        var match = 0L;

        for (var direction : directions) {
            StringBuilder result = new StringBuilder();

            try {
                for (var index = 0; index < 4; index++) {
                    result.append(data.get(startingPoint[0] + direction[0] * index).get(startingPoint[1] + direction[1] * index));
                }
            } catch (IndexOutOfBoundsException ignored) {
                continue;
            }

            match += XMAS.equalsIgnoreCase(result.toString()) || SAMX.equalsIgnoreCase(result.toString()) ? 1L : 0L;
        }

        return match;
    }

    private boolean checkForScheme(List<List<String>> data, int[] startingPoint) {
        var results = new ArrayList<String>();

        for (var neighbors : scheme) {
            try {
                StringBuilder result = new StringBuilder();
                for (var neighbor : neighbors) {
                    result.append(data.get(startingPoint[0] + neighbor[0]).get(startingPoint[1] + neighbor[1]));
                }
                results.add(result.toString());
            } catch (IndexOutOfBoundsException ignored) {}
        }

        return !results.isEmpty() && results.stream()
                .allMatch(str -> MAS.equalsIgnoreCase(str) || SAM.equalsIgnoreCase(str));
    }

}
