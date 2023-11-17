package fr.aoc.session2015;

import fr.aoc.common.Utils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Arrays;

@Slf4j
public class Day1 {

    public static void main(String[] args) throws IOException {
        var instructions = Utils.readInputJoinOnNewLines("src/main/resources/2015/Day1/input.txt");
        var startingFloor = 0;
        var searchedFloor = -1;
        var finalFloor = startingFloor + computeMoveForAction(instructions, Action.UP) + computeMoveForAction(instructions, Action.DOWN);
        var position = findFloor(instructions, startingFloor, searchedFloor);

        log.info("Final floor : {}", finalFloor);
        log.info("Position of floor {} : {}", searchedFloor, position);
    }

    private static long computeMoveForAction(String instructions, Action action) {
        return action.op * Arrays.stream(instructions.split(""))
                .filter(action.sign::equals)
                .count();
    }

    private static long findFloor(String instructions, long startingFloor, long endFloor) {
        var position = 0;
        var instructionsArray = instructions.split("");
        for (String instruction : instructionsArray) {
            position++;
            Action action = Action.findBySign(instruction);

            if (action != null) {
                startingFloor += action.op;
                if (startingFloor == endFloor) return position;
            } else {
                throw new UnsupportedOperationException();
            }
        }

        return -1;
    }

    @AllArgsConstructor
    private enum Action {
        UP( "(", 1),
        DOWN( ")", -1);

        private final String sign;
        private final long op;

        public static Action findBySign(String sign) {
            return Arrays.stream(values())
                    .filter(action -> action.sign.equals(sign))
                    .findFirst().orElseThrow();
        }
    }

}
