package fr.aoc.session2015;

import fr.aoc.common.Utils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Slf4j
public class Day6 {

    private final static Set<Light> litLightsP1 = new HashSet<>();
    private final static Set<Light> litLightsP2 = new HashSet<>();

    public static void main(String[] args) throws IOException {
        var instructions = Utils.readInputSplitOnNewLines("src/main/resources/2015/Day6/input.txt");
        instructions.forEach(Day6::processInstruction);

        log.info("Number of lights lit : {}", litLightsP1.size());
    }

    private static void processInstruction(String instruction) {
        var coordInput = Arrays.stream(instruction.split("\\D"))
                .filter(StringUtils::isNotEmpty)
                .mapToInt(Integer::parseInt)
                .toArray();
        var action = Action.byInstructionStart(instruction);

        toggleLightsP1(coordInput, action);
    }

    private static void toggleLightsP1(int[] area, Action action) {
        for (int x = area[0]; x <= area[2]; x++) {
            for (int y = area[1]; y <= area[3]; y++) {
                final Light light = new Light(x, y, 0);
                switch (action) {
                    case ON -> litLightsP1.add(light);
                    case OFF -> litLightsP1.remove(light);
                    case TOGGLE -> {
                        if (litLightsP1.contains(light)) {
                            litLightsP1.remove(light);
                        } else {
                            litLightsP1.add(light);
                        }
                    }
                    default -> throw new UnsupportedOperationException();
                }
            }
        }
    }

    private static void handleLightP2(int area, Action action) {

    }

    private static Light getLightFromSet(int x, int y) {
//        if (litLightsP2.contains())
        return null;
    }

    private record Light(long x, long y, long brightness) {}

    @AllArgsConstructor
    private enum Action {
        ON("turn on", 1),
        OFF("turn off", -1),
        TOGGLE("toggle", 2);

        private final String instruction;
        private final int brightnessModifier;

        public static Action byInstructionStart(String instruction) {
            Action actionFound;

            if (instruction.toLowerCase().startsWith(ON.instruction)) {
                actionFound = Action.ON;
            } else if (instruction.toLowerCase().startsWith(OFF.instruction)) {
                actionFound = Action.OFF;
            } else if (instruction.toLowerCase().startsWith(TOGGLE.instruction)) {
                actionFound = Action.TOGGLE;
            } else {
                throw new UnsupportedOperationException();
            }

            return actionFound;
        }
    }

}
