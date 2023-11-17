package fr.aoc.session2022;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

@Slf4j
public class Day11 {

    private List<Monkey> monkeys = new ArrayList<>();
    private long gcd = -1;

    public static void main(String[] args) throws IOException {
        Day11 day11 = new Day11();
        List<String> input = day11.readInput("src/main/resources/2022/day11/input.txt");

        day11.initMonkeyList(input);
        var answer1StartTime = System.currentTimeMillis();
        day11.playMultipleRounds(20, 3L);

        var answer1 = day11.monkeys.stream()
                .map(Monkey::getInspectCounter)
                .sorted(Collections.reverseOrder())
                .limit(2)
                .reduce(1L, Math::multiplyExact);
        var answer1EndTime = System.currentTimeMillis();

        day11.monkeys = new ArrayList<>();
        day11.initMonkeyList(input);
        var answer2StartTime = System.currentTimeMillis();
        day11.playMultipleRounds(10000, 1L);

        var answer2 = day11.monkeys.stream()
                .map(Monkey::getInspectCounter)
                .sorted(Collections.reverseOrder())
                .limit(2)
                .reduce(1L, Math::multiplyExact);
        var answer2EndTime = System.currentTimeMillis();


        log.info("Answer 1 : {}\nTemps écoulé : {}", answer1, answer1EndTime - answer1StartTime);
        log.info("Answer 2 : {}\nTemps écoulé : {}", answer2, answer2EndTime - answer2StartTime);
    }

    public List<String> readInput(String filepath) throws IOException {
        try (FileInputStream fis = new FileInputStream(filepath)) {
            return Arrays.stream(IOUtils.toString(fis, StandardCharsets.UTF_8).split("\\r\\n\\r\\n")).toList();
        }
    }

    public void initMonkeyList(List<String> input) {
        for (String monkeyDesc : input) {
            String[] lineDesc = monkeyDesc.split("\\r\\n\\s+");

            var monkeyId = getIntFromString(lineDesc[0]);
            var starting_items = new ArrayList<>(Arrays.stream(lineDesc[1].split("\\D"))
                    .filter(Predicate.not(String::isBlank))
                    .mapToLong(Long::parseLong)
                    .boxed().toList());
            var operation = lineDesc[2].split("new\\s=\\s")[1];
            var test = getIntFromString(lineDesc[3]);
            var launch_true = getIntFromString(lineDesc[4]);
            var launch_false = getIntFromString(lineDesc[5]);

            monkeys.add(new Monkey(monkeyId, starting_items, operation, test, launch_true, launch_false, 0));
        }

        gcd = monkeys.stream().mapToLong(Monkey::getTest).reduce(1L, Math::multiplyExact);
    }

    public void playMultipleRounds(int roundNumber, long worryParam) {
        for (int i = 0; i < roundNumber; i++) {
            playRound(worryParam);
        }
    }

    public void playRound(long worryParam) {
        for (Monkey monkey : monkeys) {
            for (long item : monkey.getStartingItems()) {
                long newValue = applyOp(monkey.getOperation(), item) / worryParam;
                monkeys.get(newValue % monkey.getTest() == 0 ? monkey.getLaunchTrue() : monkey.getLaunchFalse()).getStartingItems().add(newValue % gcd);
                monkey.incrementInspectCounter();
            }
            monkey.setStartingItems(new ArrayList<>());
        }
    }

    public long applyOp(String operation, long itemBaseValue) {
        String[] opSplit = operation.split("\\s");
        long secondMember = "old".equals(opSplit[2]) ? itemBaseValue : Long.parseLong(opSplit[2]);
        return switch (opSplit[1]) {
            case "+" -> itemBaseValue + secondMember;
            case "*" -> itemBaseValue * secondMember;
            default -> throw new UnsupportedOperationException();
        };
    }

    public int getIntFromString(String str) {
        return Arrays.stream(str.split("\\D"))
                .filter(Predicate.not(String::isBlank))
                .mapToInt(Integer::parseInt)
                .boxed()
                .findFirst().get();
    }

    @Data
    @AllArgsConstructor
    static class Monkey {

        private int id;
        private List<Long> startingItems;
        private String operation;
        private long test;
        private int launchTrue;
        private int launchFalse;
        private long inspectCounter;

        public void incrementInspectCounter() {
            this.inspectCounter++;
        }

    }

}
