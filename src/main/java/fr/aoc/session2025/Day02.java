package fr.aoc.session2025;

import fr.aoc.common.Utils;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;

import static fr.aoc.common.Utils.RESOURCE;

@Slf4j
public class Day02 {

    public static void main(String[] args) throws IOException {
        var ranges = Arrays.stream(Utils.readInputJoinOnNewLines(RESOURCE.formatted("2025", "day02", "input_test")).split(","))
                .map(range -> range.split("-"))
                .map(range -> new Range(range[0], range[1]))
                .toList();

        var invalidIdsPart1 = ranges.stream()
                .map(Range::calcInvalidIds)
                .toList();
        var sumPart1 = invalidIdsPart1.stream()
                .mapToLong(invalidIdsByRange -> invalidIdsByRange.stream().mapToLong(Long::longValue).sum())
                .sum();
        log.info("Sum 1: {}", sumPart1);

        var invalidIdsPart2 = ranges.stream()
                .map(Range::dumbCalcInvalidIds)
                .toList();
        var sumPart2 = invalidIdsPart2.stream()
                .mapToLong(invalidIdsByRange -> invalidIdsByRange.stream().mapToLong(Long::longValue).sum())
                .sum();
        log.info("Sum 2: {}", sumPart2);
    }

    private record Range(
            String lower,
            String upper
    ) {

        private List<Long> calcInvalidIds() {
            var invalidIds = new ArrayList<Long>();
            var lowerLong = Long.parseLong(lower);
            var upperLong = Long.parseLong(upper);
            var check = lower.substring(0, Math.max(lower.length()/2, 1))
                    .replaceAll("\\d", "0")
                    .replaceFirst("0", "1");
            var upperBoundReached = false;

            while (!upperBoundReached) {
                var longToCheck = Long.parseLong(check + check);

                if (lowerLong <= longToCheck && longToCheck <= upperLong) invalidIds.add(longToCheck);
                if (upperLong <= longToCheck) upperBoundReached = true;

                check = String.valueOf(Long.parseLong(check) + 1);
            }

            return invalidIds;
        }

        private List<Long> dumbCalcInvalidIds() {
            var lowerLong = Long.parseLong(lower);
            var upperLong = Long.parseLong(upper);

            return LongStream.range(lowerLong, upperLong + 1)
                    .filter(this::isAnyRepeatingPattern)
                    .boxed().toList();
        }

        private boolean isAnyRepeatingPattern(long number) {
            var numberAsStr = String.valueOf(number);

            for (int patternLength = 1; patternLength < numberAsStr.length(); patternLength++) {
                if (numberAsStr.length() % patternLength != 0) continue;
                int occurence = numberAsStr.length() / patternLength;
                var pattern = Pattern.compile("(%s){%s}".formatted(numberAsStr.substring(0, patternLength), occurence));
                if (pattern.matcher(numberAsStr).results().count() == 1) return true;
            }
            return false;
        }
    }

}
