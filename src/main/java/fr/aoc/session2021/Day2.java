package fr.aoc.session2021;

import fr.aoc.common.LoggerFactory;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static fr.aoc.common.Constant.REGEX_NEW_LINE;

public class Day2 {

    private static final Logger LOGGER = LoggerFactory.getLogger();

    public static void main(String[] args) {
        // GIVEN
        Day2 day2 = new Day2();
        List<String> directions = day2.readInput("src/main/resources/2021/day2/input.txt");

        // Partie 1
        LOGGER.info(day2.computeDirections(directions).toString());

        // Partie 2
        final Map<String, Integer> finalPositions = day2.computeDirectionsPart2(directions);
        LOGGER.info(finalPositions.toString());
        LOGGER.info(String.valueOf(finalPositions.get("horizontal") * finalPositions.get("depth")));
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
