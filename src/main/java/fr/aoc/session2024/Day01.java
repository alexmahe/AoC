package fr.aoc.session2024;

import fr.aoc.common.Utils;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class Day01 {

    public static void main(String[] args) throws IOException {
        var today = new Day01();
        var input = Utils.readInputSplitOnNewLines("src/main/resources/2024/day01/input").stream()
                .map(distances -> distances.split(" "))
                .map(Utils::removeEmptyStrsFromArray)
                .toList();
        var leftList = input.stream()
                .map(distances -> Long.parseLong(distances[0]))
                .sorted()
                .toList();
        var rightList = input.stream()
                .map(distances -> Long.parseLong(distances[1]))
                .sorted()
                .toList();

        var sum = 0L;
        for (int index = 0; index < leftList.size(); index++) {
            sum += Math.abs(leftList.get(index) - rightList.get(index));
        }

        log.info("Total distance between both lists = {}", sum);

        var occurencesLeft = today.toMapOfOccurences(leftList);
        var occurencesRight = today.toMapOfOccurences(rightList);
        var similarity = occurencesLeft.entrySet().stream()
                .map(entry -> entry.getKey() * entry.getValue() * occurencesRight.getOrDefault(entry.getKey(), 0L))
                .mapToLong(Long::longValue)
                .sum();

        log.info("Similarity score between both lists = {}", similarity);
    }

    private Map<Long, Long> toMapOfOccurences(List<Long> longList) {
        var occurences = new HashMap<Long, Long>();

        longList.forEach(number -> {
            occurences.merge(number, 1L, Long::sum);
        });

        return occurences;
    }
}
