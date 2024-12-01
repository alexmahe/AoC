package fr.aoc.session2023;

import fr.aoc.common.Utils;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;
import java.util.regex.MatchResult;

import static fr.aoc.common.Utils.NUMBER_PATTERN;

@Slf4j
public class Day05 {

    private static List<List<RangeInfo>> rangeInfosForAllMaps;
    private static final ArrayList<SeedRange> seedRanges = new ArrayList<>();

    public static void main(String[] args) throws IOException {
        var today = new Day05();
        var input = Utils.readInputSplitOnEmptyLines("src/main/resources/2023/day05/input");
        var parsedData = input.stream()
                .map(string -> string.split("(\r\n|\r|\n)"))
                .map(strArray -> Arrays.stream(strArray)
                        .map(str -> NUMBER_PATTERN.matcher(str).results()
                                .map(MatchResult::group)
                                .map(Long::parseLong)
                                .toList()
                        ).filter(Predicate.not(List::isEmpty))
                        .toList()
                ).toList();
        var startingData = parsedData.get(0).get(0);
        rangeInfosForAllMaps = parsedData.subList(1, parsedData.size()).stream()
                .map(rangeInfoList -> rangeInfoList.stream()
                        .map(rangeInfo -> new RangeInfo(rangeInfo.get(0), rangeInfo.get(1), rangeInfo.get(2)))
                        .toList())
                .toList();

        var locations = startingData.stream()
                .map(today::getEndValue)
                .toList();
        var lowestLocation = locations.stream()
                .mapToLong(Long::longValue)
                .min().getAsLong();
        log.info("Lowest location : {}", lowestLocation);
        log.info("Locations : {}", locations);

        for (int seedIndex = 0; seedIndex < (startingData.size() / 2) + 1; seedIndex = seedIndex + 2) {
            seedRanges.add(new SeedRange(startingData.get(seedIndex), startingData.get(seedIndex + 1)));
        }
        var locationInSeed = false;
        var loc = -1;
        List<Long> seeds = null;
        while (!locationInSeed) {
            loc++;
            seeds = today.getStartValue(loc);
            locationInSeed = seeds.stream()
                    .anyMatch(today::seedInRange);
        }
        log.info("New lowest location : {} for seed {}", loc, seeds);
    }

    private List<Long> getStartValue(long source) {
        var result = List.of(source);
        for (int mapIndex = 0; mapIndex < rangeInfosForAllMaps.size(); mapIndex++) {
            int finalMapIndex = mapIndex;
            result = result.stream()
                    .map(tempSource -> getPreviousValues(tempSource, rangeInfosForAllMaps.get(rangeInfosForAllMaps.size() - finalMapIndex - 1)))
                    .flatMap(Collection::stream)
                    .toList();
        }
        return result;
    }

    private List<Long> getPreviousValues(long source, List<RangeInfo> previousMapRangeInfo) {
        var previousValues = previousMapRangeInfo.stream()
                .filter(rangeInfo -> rangeInfo.destRangeStart() <= source && source <= rangeInfo.destRangeStart() + rangeInfo.range())
                .map(rangeInfo -> rangeInfo.sourceRangeStart() + (source - rangeInfo.destRangeStart()))
                .toList();
        return previousValues.isEmpty() ? List.of(source) : previousValues;
    }

    private long getEndValue(long source) {
        var result = source;
        for (List<RangeInfo> map : rangeInfosForAllMaps) {
            result = getNextValue(result, map);
        }
        return result;
    }

    private long getNextValue(long source, List<RangeInfo> nextMapRangeInfo) {
        return nextMapRangeInfo.stream()
                .filter(rangeInfo -> rangeInfo.sourceRangeStart() <= source && source <= rangeInfo.sourceRangeStart() + rangeInfo.range())
                .map(rangeInfo -> rangeInfo.destRangeStart() + (source - rangeInfo.sourceRangeStart()))
                .findFirst().orElse(source);
    }

    private boolean seedInRange(long seed) {
        return seedRanges.stream()
                .anyMatch(range -> range.rangeStart() < seed && seed < (range.rangeStart() + range.range()));
    }

    private record RangeInfo(long destRangeStart, long sourceRangeStart, long range) {}
    private record SeedRange(long rangeStart, long range) {}
}
