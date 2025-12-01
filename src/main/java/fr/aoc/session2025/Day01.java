package fr.aoc.session2025;

import fr.aoc.common.Utils;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.List;

import static fr.aoc.common.Utils.RESOURCE;

@Slf4j
public class Day01 {

    public static void main(String[] args) throws IOException {
        var today = new Day01();
        var input = Utils.readInputSplitOnNewLines(RESOURCE.formatted("2025", "day01", "input")).stream()
                .map(instruction -> List.of(instruction.substring(0,1), instruction.substring(1)))
                .toList();
        var startingPosition = 50L;
        var dialSize = 100;

        var zeroCounter = today.countZeroes(input, startingPosition, dialSize);
        log.info("On passe {} fois sur 0", zeroCounter);
    }

    private int countZeroes(List<List<String>> instructions, long position, int dialSize) {
        var zeroCounter = 0;
        for (var instruction : instructions) {
            var moveSize = Long.parseLong(instruction.get(1));
            if (moveSize > dialSize) {
                var entireDial = moveSize / dialSize;
                zeroCounter += entireDial;
                moveSize = moveSize % dialSize;
            }

            var direction = "L".equalsIgnoreCase(instruction.get(0)) ? -1L : 1L;
            long newPosition = position + direction * moveSize;

            if ((position > 0 && newPosition <= 0) || newPosition >= dialSize) {
                zeroCounter++;
            }

            var newPositionModulo = newPosition % dialSize;
            position = newPositionModulo >= 0 ? newPositionModulo : dialSize + newPositionModulo;
        }
        return zeroCounter;
    }

}
