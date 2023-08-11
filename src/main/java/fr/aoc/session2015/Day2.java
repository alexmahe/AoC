package fr.aoc.session2015;

import fr.aoc.common.LoggerFactory;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import static fr.aoc.common.Constant.REGEX_NEW_LINE;

public class Day2 {

    private static final Logger LOGGER = LoggerFactory.getLogger();

    public static void main(String[] args) throws IOException {
        var gifts = readInput();
        var wrapperNeeded = Arrays.stream(gifts)
                .mapToInt(giftDimension -> calcWrapper(giftDimension))
                .sum();
        var ribbonNeeded = Arrays.stream(gifts)
                .mapToInt(giftsDimensions -> calcRibbon(giftsDimensions))
                .sum();

        LOGGER.info("Wrapper needed : {}", wrapperNeeded);
        LOGGER.info("Ribbon needed : {}", ribbonNeeded);
    }

    private static int calcWrapper(String giftDimensions) {
        var dimensions = dimensionsToSortedArray(giftDimensions);
        var area = calcMultPermut(dimensions);

        return area + dimensions[0]*dimensions[1];
    }

    private static int calcRibbon(String giftDimensions) {
        var dimensions = dimensionsToSortedArray(giftDimensions);
        var smallestPerimeter = (dimensions[0] + dimensions[1]) * 2;
        var bow = Arrays.stream(dimensions)
                .reduce(1, (a, b) -> a * b);

        return smallestPerimeter + bow;
    }

    private static int calcMultPermut(int[] array) {
        var result = 0;

        for (int a = 0; a < array.length - 1; a++) {
            for (int b = a + 1; b < array.length; b++) {
                result += array[a] * array[b];
            }
        }

        return 2 * result;
    }

    private static int[] dimensionsToSortedArray(String giftDimensions) {
        return Arrays.stream(giftDimensions.split("x"))
                .mapToInt(Integer::parseInt)
                .sorted()
                .toArray();
    }

    private static String[] readInput() throws IOException {
        try (FileInputStream fis = new FileInputStream("src/main/resources/2015/Day2/input.txt")) {
            return IOUtils.toString(fis, StandardCharsets.UTF_8).split(REGEX_NEW_LINE);
        }
    }
}
