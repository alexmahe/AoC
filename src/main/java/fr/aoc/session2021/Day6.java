package fr.aoc.session2021;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
public class Day6 {

    public static void main(String[] args) {
        Day6 day6 = new Day6();
        ArrayList<AtomicLong> fishCycles = day6.readInput("src/main/resources/2021/day6/input.txt");
        fishCycles = day6.processNDays(256, fishCycles);
        long fishTotal = fishCycles.stream().map(AtomicLong::get).reduce(0L, Long::sum);
        log.info("ending fish cycle : {}", fishCycles);
        log.info("Fish total : {}", fishTotal);
    }

    private ArrayList<AtomicLong> readInput(String filePath) {
        ArrayList<AtomicLong> fishCycles = Stream.generate(AtomicLong::new).limit(9).collect(Collectors.toCollection(ArrayList::new));
        ArrayList<Integer> intputArray;

        try (FileInputStream fis = new FileInputStream(filePath)) {
            String inputStr = IOUtils.toString(fis, StandardCharsets.UTF_8);
            intputArray = Arrays.stream(inputStr.split(","))
                    .filter(element -> element != null && !element.isEmpty() && !element.trim().isEmpty())
                    .map(Integer::parseInt)
                    .collect(Collectors.toCollection(ArrayList::new));

            for (int indexDays = 0; indexDays < 9; indexDays++) {
                fishCycles.get(indexDays).set(Collections.frequency(intputArray, indexDays));
            }
            log.info("starting fish cycle : {}", fishCycles);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return fishCycles;
    }

    private ArrayList<AtomicLong> processNDays(int days, ArrayList<AtomicLong> fishCycles) {
        for (int numberOfDays = 0; numberOfDays < days; numberOfDays++) {
            fishCycles = processDay(fishCycles);
        }

        return fishCycles;
    }

    private ArrayList<AtomicLong> processDay(ArrayList<AtomicLong> fishCycles) {
        ArrayList<AtomicLong> newFishCycles = Stream.generate(AtomicLong::new).limit(fishCycles.size()).collect(Collectors.toCollection(ArrayList::new));

        newFishCycles.get(6).addAndGet(fishCycles.get(0).get());
        newFishCycles.get(8).addAndGet(fishCycles.get(0).get());

        for (int fishIndex = 1; fishIndex < fishCycles.size(); fishIndex++) {
            newFishCycles.get(fishIndex - 1).addAndGet(fishCycles.get(fishIndex).get());
        }

        return newFishCycles;
    }

}
