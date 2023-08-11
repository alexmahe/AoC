package fr.aoc.session2022;

import fr.aoc.common.LoggerFactory;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

import static fr.aoc.common.Constant.REGEX_NEW_LINE;

public class Day4 {

    private static final Logger LOGGER = LoggerFactory.getLogger();

    public static void main(String[] args) throws IOException {
        Day4 day4 = new Day4();

        long answer1 = day4.readProcessInput("src/main/resources/2022/day4/input.txt", true);
        long answer2 = day4.readProcessInput("src/main/resources/2022/day4/input.txt", false);

        LOGGER.info("Score (answer 1) : {}", answer1);
        LOGGER.info("Score (answer 2) : {}", answer2);
    }

    private long readProcessInput(String filepath, boolean contained) throws IOException {
        try (FileInputStream fis = new FileInputStream(filepath)) {
            return Arrays.stream(IOUtils.toString(fis, StandardCharsets.UTF_8).split(REGEX_NEW_LINE))
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
