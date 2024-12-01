package fr.aoc.session2022;

import fr.aoc.common.Utils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
public class Day10 {

    private final List<Integer> checkpoints = List.of(20, 60, 100, 140, 180, 220);
    private final List<List<String>> CRT = new ArrayList<>();

    private List<String> currentLine = new ArrayList<>();
    private int value = 1;
    private int cycle = 0;
    private int signalStrength = 0;

    public static void main(String[] args) throws IOException {
        Day10 day10 = new Day10();

        var instructions = day10.processInput("src/main/resources/2022/day10/input");
        log.debug("Instructions récupérées : {}", instructions);

        instructions.forEach(day10::process);


        log.info("End signal strength : {}", day10.signalStrength);
        log.info("CRT :");
        day10.CRT.forEach(line -> log.info(String.join(" ", line)));
    }

    private List<Operation> processInput(String filepath) throws IOException {
        return Utils.readInputSplitOnNewLines(filepath).stream()
                .map(instruction -> {
                    var instructions = instruction.split(" ");
                    var operationType = OperationType.fromName(instructions[0]);
                    var value = instructions.length > 1 ? Integer.parseInt(instructions[1]) : 0;
                    return new Operation(operationType, value);
                })
                .toList();
    }

    private void process(Operation operation) {
        for (int i = 0; i < operation.type().getDuration(); i++) {
            cycle++;
            updateSignalStrength();
            updateLine();
        }

        updateValue(operation);
    }

    private void updateSignalStrength() {
        if (checkpoints.contains(cycle)) {
            var strength = cycle * value;
            log.info("Cycle {}, adding {} to signal strength {}.", cycle, strength, signalStrength);
            signalStrength += strength;
        }
    }

    private void updateValue(Operation operation) {
        if (operation.type() == OperationType.ADD) {
            log.info("Operation type {} finished, adding {} to value {}.", OperationType.ADD, operation.value(), value);
            value += operation.value();
        }
    }

    private void updateLine() {
        int position = (cycle - 1) % 40;


        if (value - 1 <= position && position <= value + 1) {
            currentLine.add("#");
        } else {
            currentLine.add(".");
        }

        if (cycle % 40 == 0) {
            CRT.add(currentLine);
            currentLine = new ArrayList<>();
        }
    }

    private record Operation (OperationType type, int value) {}

    @Getter
    @AllArgsConstructor
    private enum OperationType {
        NOOP("noop", 1),
        ADD("addx", 2);

        private final String operationName;
        private final int duration;

        public static OperationType fromName(String name) {
            return Arrays.stream(values())
                    .filter(operationType -> operationType.getOperationName().equalsIgnoreCase(name))
                    .findFirst().orElseThrow(IllegalArgumentException::new);
        }
    }
}
