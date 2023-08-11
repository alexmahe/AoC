package fr.aoc.session2022;

import org.apache.commons.io.IOUtils;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

public class Day4 {

    public static void main(String[] args) throws IOException {
        Day4 day4 = new Day4();

        long answer1 = day4.readProcessInput("src/main/resources/2022/day4/input.txt", true);
        long answer2 = day4.readProcessInput("src/main/resources/2022/day4/input.txt", false);

        System.out.printf("Score (answer 1) : %s%n", answer1);
        System.out.printf("Score (answer 2) : %s%n", answer2);
    }

    private long readProcessInput(String filepath, boolean contained) throws IOException {
        try (FileInputStream fis = new FileInputStream(filepath)) {
            return Arrays.stream(IOUtils.toString(fis, StandardCharsets.UTF_8).split("\\r\\n"))
                    .filter(sectionPair -> {
                        var sections = sectionPair.split(",");
                        var sectionsBounds = Arrays.stream(sections)
                                .map(section -> Arrays.stream(section.split("-"))
                                        .mapToInt(Integer::parseInt)
                                        .boxed()
                                        .sorted()
                                        .toList())
                                .toList();
                        return contained ? isSectionContainedInSection(sectionsBounds.get(0), sectionsBounds.get(1)) : isSectionsOverlapping(sectionsBounds.get(0), sectionsBounds.get(1));
                    })
                    .count();
        }
    }

    private boolean isSectionContainedInSection(List<Integer> sectionA, List<Integer> sectionB) {
        return (sectionA.get(0) <= sectionB.get(0) && sectionB.get(0) <= sectionA.get(1)) && (sectionA.get(0) <= sectionB.get(1) && sectionB.get(1) <= sectionA.get(1))
                || (sectionB.get(0) <= sectionA.get(0) && sectionA.get(0) <= sectionB.get(1)) && (sectionB.get(0) <= sectionA.get(1) && sectionA.get(1) <= sectionB.get(1));
    }

    private boolean isSectionsOverlapping(List<Integer> sectionA, List<Integer> sectionB) {
        return (sectionA.get(0) <= sectionB.get(0) && sectionB.get(0) <= sectionA.get(1))
                || (sectionA.get(0) <= sectionB.get(1) && sectionB.get(1) <= sectionA.get(1))
                || (sectionB.get(0) <= sectionA.get(0) && sectionA.get(0) <= sectionB.get(1))
                || (sectionB.get(0) <= sectionA.get(1) && sectionA.get(1) <= sectionB.get(1));
    }

}
