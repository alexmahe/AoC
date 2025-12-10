package fr.aoc.session2025;

import fr.aoc.common.Utils;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static fr.aoc.common.Utils.REGEX_NEW_LINE;
import static fr.aoc.common.Utils.RESOURCE;

@Slf4j
public class Day05 {

    public static void main(String[] args) throws IOException {
        var today = new Day05();
        var input = Utils.readInputSplitOnEmptyLines(RESOURCE.formatted("2025", "day05", "input"));
        var ranges = Arrays.stream(input.get(0).split(REGEX_NEW_LINE))
                .map(range -> {
                    var bounds = range.split("-");
                    return new Range(Long.parseLong(bounds[0]), Long.parseLong(bounds[1]));
                }).toList();
        var ingredients = Arrays.stream(input.get(1).split(REGEX_NEW_LINE)).map(Long::parseLong).toList();

        var freshIngredients = ingredients.stream()
                .filter(ingredient -> ranges.stream().anyMatch(range -> range.contains(ingredient)))
                .count();
        log.info("Number of fresh ingredients: {}", freshIngredients);

        var mergedRanges = today.mergeAllOverlappingRanges(new ArrayList<>(ranges));
        var allRangesMerged = false;
        while (!allRangesMerged) {
            var udpatedRanges = today.mergeAllOverlappingRanges(new ArrayList<>(mergedRanges));
            if (udpatedRanges.size() == mergedRanges.size()) allRangesMerged = true;
            mergedRanges = udpatedRanges;
        }

        var nbFreshIds = mergedRanges.stream()
                .mapToLong(range -> range.end() - range.start() + 1)
                .sum();

        mergedRanges = mergedRanges.stream()
                .sorted()
                .toList();
        log.info("Numbner of fresh Ids: {}", nbFreshIds);
    }

    private List<Range> mergeAllOverlappingRanges(ArrayList<Range> ranges) {

        if (ranges.isEmpty()) return new ArrayList<>();
        if (ranges.size() == 1) return ranges;

        var rangeToStudy = ranges.get(0);
        var overlappingRanges = ranges.stream()
                .skip(1)
                .filter(rangeToStudy::isOverlapping)
                .toList();
        var newRangeList = new ArrayList<>(Collections.singleton(rangeToStudy.mergeRanges(overlappingRanges)));
        ranges.remove(rangeToStudy);
        ranges.removeAll(overlappingRanges);
        newRangeList.addAll(mergeAllOverlappingRanges(ranges));
        return newRangeList;
    };

    private record Range(
            long start,
            long end
    ) implements Comparable<Range> {
        private boolean contains(long value) {
            return value >= start && value <= end;
        }

        private boolean isOverlapping(Range other) {
            return (other.start() <= this.start && this.start <= other.end())
                    || (other.start() <= this.end && this.end <= other.end())
                    || (this.start <= other.start() && other.start() <= this.end)
                    || (this.start <= other.end() && other.end() <= this.end);
        }

        private Range mergeRanges(List<Range> others) {
            var min = others.stream().mapToLong(Range::start).min();
            var max = others.stream().mapToLong(Range::end).max();

            return new Range(Math.min(this.start(), min.orElse(Long.MAX_VALUE)), Math.max(this.end(), max.orElse(Long.MIN_VALUE)));
        }

        @Override
        public int compareTo(Range o) {
            return Long.compare(this.start, o.start());
        }
    }

}
