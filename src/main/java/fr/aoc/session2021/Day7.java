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
import java.util.stream.Stream;

@Slf4j
public class Day7 {

    public static void main(String[] args) {
        Day7 day7 = new Day7();
        List<Integer> positions = day7.readInput("src/main/resources/2021/day7/input.txt");
        ArrayList<Integer> costList = Stream.generate(() -> 0).limit(Collections.max(positions)).collect(Collectors.toCollection(ArrayList::new));

        for (int index = 0; index < costList.size(); index++) {
            int finalIndex = index;
            costList.set(index, positions.stream()
                    .mapToInt(position -> Math.abs(position - finalIndex))
                    .sum());
        }
        log.info("Best solution part 1 : {}", Collections.min(costList));

        for (int index = 0; index < costList.size(); index++) {
            int finalIndex = index;
            costList.set(index, positions.stream()
                    .mapToInt(position -> (Math.abs(position - finalIndex) * (Math.abs(position - finalIndex) + 1)) / 2)
                    .sum());
        }
        log.info("Best solution part 2 : {}", Collections.min(costList));
    }

    private List<Integer> readInput(String filePath) {
        List<Integer> positions = null;

        try (FileInputStream fis = new FileInputStream(filePath)) {
            String inputStr = IOUtils.toString(fis, StandardCharsets.UTF_8);
            positions = Arrays.stream(inputStr.split("\\D"))
                    .filter(element -> element != null && !element.isEmpty() && !element.trim().isEmpty())
                    .map(Integer::parseInt)
                    .toList();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return positions;
    }
}
