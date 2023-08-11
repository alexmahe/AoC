package fr.aoc.session2015;

import fr.aoc.common.LoggerFactory;
import lombok.AllArgsConstructor;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import static fr.aoc.common.Constant.REGEX_NEW_LINE;

public class Day1 {

    private static final Logger LOGGER = LoggerFactory.getLogger();

    public static void main(String[] args) throws IOException {
        var instructions = readInput();
        var startingFloor = 0;
        var searchedFloor = -1;
        var finalFloor = startingFloor + computeMoveForAction(instructions, Action.UP) + computeMoveForAction(instructions, Action.DOWN);
        var position = findFloor(instructions, startingFloor, searchedFloor);

        LOGGER.info("Final floor : {}", finalFloor);
        LOGGER.info("Position of floor {} : {}", searchedFloor, position);
    }

    private static String readInput() throws IOException {
        try (FileInputStream fis = new FileInputStream("src/main/resources/2015/Day1/input.txt")) {
            return IOUtils.toString(fis, StandardCharsets.UTF_8).replaceAll(REGEX_NEW_LINE, "");
        }
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
