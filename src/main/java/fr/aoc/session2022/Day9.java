package fr.aoc.session2022;

import fr.aoc.common.Utils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static fr.aoc.common.Utils.REGEX_NEW_LINE;

@Slf4j
public class Day9 {

    private final int squareSize = 1001;
    private List<List<String>> visited;
    private List<Knot> rope;

    public static void main(String[] args) throws IOException {
        Day9 day9 = new Day9();
        List<String> instructions = Utils.readInputSplitOnNewLines("src/main/resources/2022/day9/input.txt");

        day9.init(2);
        day9.processInput(instructions);
        day9.visited.get(day9.squareSize / 2).set(day9.squareSize / 2, "#");

        long answer1 = day9.visited.stream()
                .mapToLong(line -> line.stream().filter("#"::equals).count())
                .sum();
        log.info("Answer 1 : {}", answer1);

        day9.init(10);
        day9.processInput(instructions);
        day9.visited.get(day9.squareSize / 2).set(day9.squareSize / 2, "#");

        long answer2 = day9.visited.stream()
                .mapToLong(line -> line.stream().filter("#"::equals).count())
                .sum();
        log.info("Answer 2 : {}", answer2);
    }

    public void init(int ropeSize) {
        visited = Stream.generate(() -> Stream.generate(() -> ".")
                        .limit(squareSize)
                        .collect(Collectors.toCollection(ArrayList::new)))
                .limit(squareSize)
                .collect(Collectors.toCollection(ArrayList::new));

        rope = Stream.generate(() -> new Knot(squareSize / 2, squareSize / 2)).limit(ropeSize).collect(Collectors.toCollection(ArrayList::new));
        visited.get(squareSize / 2).set(squareSize / 2, "#");
    }

    public void processInput(List<String> instructions) {
        for (String instruction : instructions) {
            Directions direction = Directions.getValueFromSign(instruction.split(" ")[0]);
            int distance = Integer.parseInt(instruction.split(" ")[1]);

            for (int i = 0; i < distance; i++) {
                move(direction);
            }
        }
    }

    public void move(Directions direction) {
        rope.get(0).moveX(direction.moveX);
        rope.get(0).moveY(direction.moveY);

        for (int knotIndex = 1; knotIndex < rope.size(); knotIndex++) {
            moveTail(knotIndex);
        }
    }

    public void moveTail(int knot) {
        int xDiff = rope.get(knot - 1).getX() - rope.get(knot).getX();
        int yDiff = rope.get(knot - 1).getY() - rope.get(knot).getY();
        
        if (Math.abs(xDiff) >= 2 || Math.abs(yDiff) >= 2) {
            if (Math.abs(xDiff) > 0) rope.get(knot).moveX(xDiff / Math.abs(xDiff));
            if (Math.abs(yDiff) > 0) rope.get(knot).moveY(yDiff / Math.abs(yDiff));
        }

        if (knot == rope.size() - 1 && !"#".equals(visited.get(rope.get(knot).getX()).get(rope.get(knot).getY()))) {
            visited.get(rope.get(knot).getX()).set(rope.get(knot).getY(), "#");
        }
    }

    @Data
    @AllArgsConstructor
    private static class Knot {
        private int x;
        private int y;
        
        public void moveX(int xDiff) {
            x = x + xDiff;
        }

        public void moveY(int yDiff) {
            y = y + yDiff;
        }
    }

    @AllArgsConstructor
    public enum Directions {
        RIGHT("R", 0, 1),
        LEFT("L", 0, -1),
        UP("U", -1, 0),
        DOWN("D", 1, 0);

        private final String sign;
        private final int moveX;
        private final int moveY;

        public static Directions getValueFromSign(String sign) {
            return Arrays.stream(Directions.values())
                    .filter(dir -> dir.sign.equals(sign))
                    .findFirst()
                    .get();
        }
    }
}
