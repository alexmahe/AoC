package fr.aoc.session2023;

import fr.aoc.common.Utils;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;

@Slf4j
public class Day08 {

    private static final Pattern LETTER_PATTERN = Pattern.compile("\\w+");

    public static void main(String[] args) throws IOException {
        var today = new Day08();
        var input = Utils.readInputSplitOnNewLines("src/main/resources/2023/day08/input");
        var instructions = Arrays.stream(input.get(0).split("")).toList();
        var nodes = input.stream()
                .skip(2L)
                .map(nodeStr -> LETTER_PATTERN.matcher(nodeStr).results().map(MatchResult::group).toList())
                .toList();

        var steps = today.traverseDesert(instructions, nodes, "AAA");
        log.info("Number of steps required : {}", steps);

        var startingPositions = nodes.stream().filter(node -> node.get(0).endsWith("A")).map(node -> node.get(0)).toList();
        var solutionsLengths = startingPositions.stream()
                .map(startingPosition -> today.traverseDesert(instructions, nodes, startingPosition))
                .toList();
        var lcm = today.LCM(solutionsLengths.get(0), solutionsLengths.get(1));
        for (int lengthIndex = 2; lengthIndex < solutionsLengths.size(); lengthIndex++) {
            lcm = today.LCM(lcm, solutionsLengths.get(lengthIndex));
        }

        log.info("Starting positions : {}", startingPositions);
        log.info("Solutions lengths : {}", solutionsLengths);
        log.info("LCM : {}", lcm);
    }

    private long traverseDesert(List<String> instructions, List<List<String>> nodes, String startingPos) {
        var step = 0;
        while (!startingPos.endsWith("Z")) {
            var currentInstruction = instructions.get(step % instructions.size()).equals("R") ? 2 : 1;
            var tempCurrentPos = startingPos;
            startingPos = nodes.stream()
                    .filter(node -> node.get(0).equals(tempCurrentPos))
                    .findFirst().get().get(currentInstruction);
            step++;
        }

        return step;
    }

    private long LCM(long a, long b) {
        if (a == 0 || b == 0) return 0;
        var gcd = GCD(a, b);
        return (a * b) / gcd;
    }

    private long GCD(long a, long b) {
        if (a == 0 || b == 0) {
            return a + b;
        } else {
            var biggest = Math.max(a, b);
            var smallest = Math.min(a, b);
            return GCD(smallest, biggest % smallest);
        }
    }
}
