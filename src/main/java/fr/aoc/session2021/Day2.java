package fr.aoc.session2021;

import org.apache.commons.io.IOUtils;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Day2 {

    public static void main(String[] args) {
        // GIVEN
        Day2 day2 = new Day2();
        List<String> directions = day2.readInput("src/main/resources/2021/day2/input.txt");

        // Partie 1
        System.out.println(day2.computeDirections(directions));

        // Partie 2
        final Map<String, Integer> finalPositions = day2.computeDirectionsPart2(directions);
        System.out.println(finalPositions);
        System.out.println(finalPositions.get("horizontal") * finalPositions.get("depth"));
    }

    private List<String> readInput(String filePath) {
        List<String> directions = null;

        try (FileInputStream fis = new FileInputStream(filePath)) {
            String inputStr = IOUtils.toString(fis, StandardCharsets.UTF_8);
            directions = Arrays.stream(inputStr.split("(\\r|\\n|\\r\\n)"))
                    .filter(element -> element != null && !element.isEmpty() && !element.trim().isEmpty())
                    .toList();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return directions;
    }

    private Map<String, Integer> computeDirections(List<String> directions) {
        Map<String, Integer> results = new HashMap<>(2);
        results.put("horizontal", 0);
        results.put("depth", 0);

        for(String direction : directions) {
            String[] data = direction.split("\\s");
            if ("forward".equalsIgnoreCase(data[0])) {
                results.put("horizontal", results.get("horizontal") + Integer.parseInt(data[1]));
            } else if ("up".equalsIgnoreCase(data[0])) {
                results.put("depth", results.get("depth") - Integer.parseInt(data[1]));
            } else if ("down".equalsIgnoreCase(data[0])) {
                results.put("depth", results.get("depth") + Integer.parseInt(data[1]));
            }
        }

        return results;
    }

    private Map<String, Integer> computeDirectionsPart2(List<String> directions) {
        Map<String, Integer> results = new HashMap<>(3);
        results.put("horizontal", 0);
        results.put("depth", 0);
        results.put("aim", 0);

        for(String direction : directions) {
            String[] data = direction.split("\\s");
            if ("forward".equalsIgnoreCase(data[0])) {
                results.put("horizontal", results.get("horizontal") + Integer.parseInt(data[1]));
                results.put("depth", results.get("depth") + results.get("aim") * Integer.parseInt(data[1]));
            } else if ("up".equalsIgnoreCase(data[0])) {
                results.put("aim", results.get("aim") - Integer.parseInt(data[1]));
            } else if ("down".equalsIgnoreCase(data[0])) {
                results.put("aim", results.get("aim") + Integer.parseInt(data[1]));
            }
        }

        return results;
    }

}
