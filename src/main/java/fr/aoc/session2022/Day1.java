package fr.aoc.session2022;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Slf4j
public class Day1 {

    public static void main(String[] args) throws IOException {
        Day1 day1 = new Day1();
        var totalCalPerElves = day1.readProcessInput("src/main/resources/2022/day1/input.txt");
        var top3Sum = totalCalPerElves.stream().limit(3).reduce(0, Integer::sum);

        log.info("Elf with max cal (answer 1) : {}", Collections.max(totalCalPerElves));
        log.info("Top 3 elves with max cal (answer 2) : {}", top3Sum);
    }

    private List<Integer> readProcessInput(String filepath) throws IOException {
        try (FileInputStream fis = new FileInputStream(filepath)) {
            String inputStr = IOUtils.toString(fis, StandardCharsets.UTF_8);
            return Arrays.stream(inputStr.split("\\D\\r\\n"))
                    .map(elfSnack -> Arrays.stream(elfSnack.split("\\D"))
                            .filter(snack -> snack != null && !snack.isEmpty() && !snack.trim().isEmpty())
                            .mapToInt(Integer::parseInt).sum())
                    .sorted(Comparator.reverseOrder())
                    .toList();
        }
    }

}
