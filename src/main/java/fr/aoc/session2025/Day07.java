package fr.aoc.session2025;

import fr.aoc.common.Utils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ClassPathUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

import static fr.aoc.common.Utils.RESOURCE;

@Slf4j
public class Day07 {

    public static void main(String[] args) throws IOException {
        var today = new Day07();
        var pattern = Pattern.compile("\\^");
        var input = Utils.readInputSplitOnNewLines(RESOURCE.formatted("2025", "day07", "input_test"));
        HashSet<Coord> splitterSet = input.stream()
            .map(line -> Pair.of(input.indexOf(line), pattern.matcher(line).results().map(MatchResult::start).toList()))
            .collect(
                HashSet::new, 
                (set, pair) -> set.addAll(pair.getRight().stream().map(y -> new Coord(pair.getLeft(), y)).toList()), 
                HashSet::addAll
            );
        var beamSet = new HashSet<Coord>();
        beamSet.add(new Coord(0, input.get(0).indexOf("S")));

        var beamsLists = IntStream.range(0, input.size() - 1)
            .mapToObj(ignored -> today.goThroughGridAndCountSplit(splitterSet, beamSet))
            .toList();
        
//        var splitCount = 0;
//        int bound = input.size() - 1;
//        for (int ignored = 0; ignored < bound; ignored++) {
//            int i = today.goThroughGridAndCountSplit(splitterSet, beamSet);
//            splitCount += i;
//        }

        var stop = "stop";
    }
    
    private HashSet<Coord> goThroughGridAndCountSplit(HashSet<Coord> splitters, HashSet<Coord> beams) {
        beams.forEach(Coord::incrRow);
        HashSet<Coord> splitBeams = beams.stream()
            .filter(splitters::contains)
            .map(Coord::split)
            .collect(HashSet::new, HashSet::addAll, HashSet::addAll);
        return splitBeams;
//        for (Coord beam : splitBeams) {
//            var stop = "stop";
//            beams.addAll(beam.split());
//            if (!beams.contains(beam)) {throw new RuntimeException();}
//            beams.remove(beam);
//        }
        
//        splitBeams.forEach(beams::remove);
//        splitBeams.forEach(splitBeam -> {
//            beams.addAll(splitBeam.split());
//        });
        
//        return splitBeams.size();
    }
    
    @AllArgsConstructor
    private static class Coord {
        private int x;
        private int y;
        
        private void incrRow() {
            x++;
        }
        
        private void incrCol() {
            y++;
        }
        
        private List<Coord> split() {
            return List.of(new Coord(x, y - 1), new Coord(x, y + 1));
        }

        @Override
        public String toString() {
            return "Coord{" +
                "x= " + x +
                ", y= " + y +
                '}';
        }

        @Override
        public boolean equals(Object o) {
            if (o == null || getClass() != o.getClass()) return false;
            Coord coord = (Coord) o;
            return x == coord.x && y == coord.y;
        }

        @Override
        public int hashCode() {
            return Objects.hash(x, y);
        }
    }
}
