package fr.aoc.session2021;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

@Slf4j
public class Day1 {

    public static void main(String[] args) {
        // GIVEN
        Day1 day1 = new Day1();
        List<Integer> measurements = day1.readInput("src/main/resources/2021/day1/input.txt");

        // Partie 1
        log.info("Partie 1 : {}", day1.countDepthIncrease(measurements));

        // Partie 2
        log.info("Partie 2 : {}", day1.countDepthIncreaseWithSlidingWindow(measurements));
    }

    private List<Integer> readInput(String filePath) {
        List<Integer> measurement = null;

        try (FileInputStream fis = new FileInputStream(filePath)) {
            String inputStr = IOUtils.toString(fis, StandardCharsets.UTF_8);
            measurement = Arrays.stream(inputStr.split("\\D"))
                    .filter(element -> element != null && !element.isEmpty() && !element.trim().isEmpty())
                    .map(Integer::parseInt)
                    .toList();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return measurement;
    }

    private int countDepthIncrease(List<Integer> measurement) {
        int counter = 0;

        if (measurement == null || measurement.size() <= 1) {
            return 0;
        }

        for (int index = 1; index < measurement.size(); index++) {
            if (measurement.get(index) > measurement.get(index - 1)) {
                counter++;
            }
        }

        return counter;
    }

    private int countDepthIncreaseWithSlidingWindow(List<Integer> measurement) {
        int counter = 0;

        if (measurement == null || measurement.size() <= 4) {
            return 0;
        }

        for (int index = 3; index < measurement.size(); index++) {
            if (calcWindow(measurement, index) > calcWindow(measurement, index - 1)) {
                counter++;
            }
        }

        return counter;
    }

    private int calcWindow(List<Integer> measurement, int windowStartIndex) {
        return measurement.get(windowStartIndex) + measurement.get(windowStartIndex - 1) + measurement.get(windowStartIndex - 2);
    }

}
