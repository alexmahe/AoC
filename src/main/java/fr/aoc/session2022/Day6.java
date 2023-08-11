package fr.aoc.session2022;

import fr.aoc.common.LoggerFactory;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class Day6 {

    private static final Logger LOGGER = LoggerFactory.getLogger();

    public static void main(String[] args) {
        Day6 day6 = new Day6();

        String input = day6.readInput("src/main/resources/2022/day6/input.txt");
        int answer1 = day6.findFirstMarker(input, 4);
        int answer2 = day6.findFirstMarker(input, 14);

        LOGGER.info("Result answer1 : {}", answer1);
        LOGGER.info("Result answer2 : {}", answer2);
    }

    private String readInput(String filepath) {
        try (FileInputStream fis = new FileInputStream(filepath)) {
            return IOUtils.toString(fis, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private int findFirstMarker(String input, int markerSize) {
        for (int charIndex = 0; charIndex < input.length() - markerSize + 1; charIndex++) {
            Set<String> charSet = new HashSet<>(Arrays.asList(input.substring(charIndex, charIndex + markerSize).split("")));
            if (charSet.size() == markerSize) return charIndex + markerSize;
        }

        throw new UnsupportedOperationException();
    }

}
