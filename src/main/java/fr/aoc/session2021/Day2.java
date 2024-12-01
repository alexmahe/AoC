package fr.aoc.session2021;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static fr.aoc.common.Utils.REGEX_NEW_LINE;

@Slf4j
public class Day2 {

    public static void main(String[] args) {
        // GIVEN
        Day2 day2 = new Day2();
        List<String> directions = day2.readInput("src/main/resources/2021/day2/input.txt");

        // Partie 1
        log.info(day2.computeDirections(directions).toString());

        // Partie 2
        final Map<String, Integer> finalPositions = day2.computeDirectionsPart2(directions);
        log.info(finalPositions.toString());
        log.info(String.valueOf(finalPositions.get("horizontal") * finalPositions.get("depth")));
    }

    private List<String> readInput(String filePath) {
        List<String> directions = null;

        try (FileInputStream fis = new FileInputStream(filePath)) {
            String inputStr = IOUtils.toString(fis, StandardCharsets.UTF_8);
            directions = Arrays.stream(inputStr.split(REGEX_NEW_LINE))
                    .filter(element -> element != null && !element.isEmpty() && !element.trim().isEmpty())
                    .toList();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return directions;
    }

    private Map<String, Integer> computeDirections(List<String> directions) {
        int horizontal = 0;
        int depth = 0;

        for(String direction : directions) {
            String[] data = direction.split("\\s");
            int directionInt = Integer.parseInt(data[1]);
            if ("forward".equalsIgnoreCase(data[0])) {
                horizontal += directionInt;
            } else if ("up".equalsIgnoreCase(data[0])) {
                depth -= directionInt;
            } else if ("down".equalsIgnoreCase(data[0])) {
                depth += directionInt;
            }
        }

        return Map.of("horizontal", horizontal, "depth", depth);
    }

    private Map<String, Integer> computeDirectionsPart2(List<String> directions) {
        int horizontal = 0;
        int depth = 0;
        int aim = 0;

        for(String direction : directions) {
            String[] data = direction.split("\\s");
            int directionInt = Integer.parseInt(data[1]);
            if ("forward".equalsIgnoreCase(data[0])) {
                horizontal += directionInt;
                depth += aim * directionInt;
            } else if ("up".equalsIgnoreCase(data[0])) {
                aim -= directionInt;
            } else if ("down".equalsIgnoreCase(data[0])) {
                aim += directionInt;
            }
        }

        return Map.of("horizontal", horizontal, "depth", depth, "aim", aim);
    }

}
