package fr.aoc.session2015;

import fr.aoc.common.Utils;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.regex.Pattern;

@Slf4j
public class Day5 {
    private static final Pattern vowelPattern = Pattern.compile(".*[aeiou].*[aeiou].*[aeiou].*");
    private static final Pattern doubleLetterPattern = Pattern.compile(".*([a-zA-Z])\\1.*");
    private static final Pattern forbiddenPattern = Pattern.compile("^((?!(ab|cd|pq|xy)).)*$");
    private static final Pattern repeatingPairPattern = Pattern.compile(".*([a-zA-Z]{2}).*\\1.*");
    private static final Pattern repeatingSpacedPattern = Pattern.compile(".*([a-zA-Z]).\\1.*");

    public static void main(String[] args) throws IOException {
        var inputStrings = Utils.readInputSplitOnNewLines("src/main/resources/2015/Day5/input.txt");
        var nbNiceP1 = inputStrings.stream()
                .filter(str -> isNice(str, vowelPattern, doubleLetterPattern, forbiddenPattern))
                .count();
        var nbNiceP2 = inputStrings.stream()
                .filter(str -> isNice(str, repeatingPairPattern, repeatingSpacedPattern))
                .count();

        log.info("Number of nice strings P1 : {}", nbNiceP1);
        log.info("Number of nice strings P2 : {}", nbNiceP2);
    }

    private static boolean isNice(String str, Pattern... patterns) {
        for (Pattern pattern : patterns) {
            if (!pattern.matcher(str).find()) return false;
        }
        return true;
    }
}
