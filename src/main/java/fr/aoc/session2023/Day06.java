package fr.aoc.session2023;

import fr.aoc.common.Utils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.List;
import java.util.stream.LongStream;

import static fr.aoc.common.Utils.NUMBER_PATTERN;

@Slf4j
public class Day06 {

    public static void main(String[] args) throws IOException {
        var input = Utils.readInputSplitOnNewLines("src/main/resources/2023/day06/input");
        var parseAnswer1 = input.stream()
                .map(line -> NUMBER_PATTERN.matcher(line).results().map(matchResult -> Long.parseLong(matchResult.group())).toList())
                .toList();
        var parseAnswer2 = input.stream()
                .map(StringUtils::deleteWhitespace)
                .map(line -> NUMBER_PATTERN.matcher(line).results().map(matchResult -> Long.parseLong(matchResult.group())).toList())
                .toList();

        var distForTimePressed = countWinningWays(parseAnswer1);
        var answer1 = calcErrorMargin(distForTimePressed);
        log.info("Number of ways to win {} for each time {}", distForTimePressed, parseAnswer1.get(1));
        log.info("Mult of that : {}", answer1);

        var distForTimePressedAnswer2 = countWinningWays(parseAnswer2);
        var answer2 = calcErrorMargin(distForTimePressedAnswer2);
        log.info("Number of ways to win {} for each time {}", distForTimePressedAnswer2, parseAnswer2.get(1));
        log.info("Mult of that : {}", answer2 );
    }

    private static List<Long> countWinningWays(List<List<Long>> inputAnswer2) {
        return inputAnswer2.get(0).stream()
                .map(totalTime -> LongStream.range(0, totalTime)
                        .map(timePressed -> (totalTime - timePressed) * timePressed)
                        .filter(distance -> distance > inputAnswer2.get(1).get(inputAnswer2.get(0).indexOf(totalTime)))
                        .count())
                .toList();
    }

    private static long calcErrorMargin(List<Long> distForTimePressed) {
        return distForTimePressed.stream()
                .mapToLong(Long::longValue)
                .reduce(1, (a, b) -> a * b);
    }

}
