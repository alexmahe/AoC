package fr.aoc.session2015;

import fr.aoc.common.LoggerFactory;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.regex.Pattern;

import static fr.aoc.common.Constant.REGEX_NEW_LINE;

public class Day5 {

    private static final Logger LOGGER = LoggerFactory.getLogger();
    private static final Pattern vowelPattern = Pattern.compile(".*[aeiou].*[aeiou].*[aeiou].*");
    private static final Pattern doubleLetterPattern = Pattern.compile(".*([a-zA-Z])\\1.*");
    private static final Pattern forbiddenPattern = Pattern.compile("^((?!(ab|cd|pq|xy)).)*$");
    private static final Pattern repeatingPairPattern = Pattern.compile(".*([a-zA-Z]{2}).*\\1.*");
    private static final Pattern repeatingSpacedPattern = Pattern.compile(".*([a-zA-Z]).\\1.*");

    public static void main(String[] args) throws IOException {
        var inputStrings = readInput();
        var nbNiceP1 = Arrays.stream(inputStrings)
                .filter(str -> isNice(str, vowelPattern, doubleLetterPattern, forbiddenPattern))
                .count();
        var nbNiceP2 = Arrays.stream(inputStrings)
                .filter(str -> isNice(str, repeatingPairPattern, repeatingSpacedPattern))
                .count();

        LOGGER.info("Number of nice strings P1 : {}", nbNiceP1);
        LOGGER.info("Number of nice strings P2 : {}", nbNiceP2);
    }

    private static boolean isNice(String str, Pattern... patterns) {
        for (Pattern pattern : patterns) {
            if (!pattern.matcher(str).find()) return false;
        }
        return true;
    }

    private static String[] readInput() throws IOException {
        try (FileInputStream fis = new FileInputStream("src/main/resources/2015/Day5/input.txt")) {
            return IOUtils.toString(fis, StandardCharsets.UTF_8).split(REGEX_NEW_LINE);
        }
    }
}
