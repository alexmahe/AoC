package fr.aoc.session2024;

import fr.aoc.common.Utils;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;

import static fr.aoc.common.Utils.NUMBER_PATTERN;

@Slf4j
public class Day03 {

    public static final Pattern MUL_PATTERN = Pattern.compile("mul\\(\\d{1,3},\\d{1,3}\\)");
    public static final Pattern DOS_N_DONTS = Pattern.compile("(do\\(\\)).*?(don't\\(\\))");

    public static void main(String[] args) throws IOException {
        var today = new Day03();
        var input = Utils.readInputJoinOnNewLines("src/main/resources/2024/day03/input");

        var resultWholeInput = today.getSumOfMulForSection(input);
        log.info("Uncorrupted result for whole input = {}", resultWholeInput);

        var resultWithDosNDonts = DOS_N_DONTS.matcher("do()" + input + "don't()").results()
                .map(MatchResult::group)
                .map(today::getSumOfMulForSection)
                .mapToLong(Long::longValue)
                .sum();
        log.info("Uncorrupted result for rectified input = {}", resultWithDosNDonts);
    }

    private long getSumOfMulForSection(String input) {
        return MUL_PATTERN.matcher(input).results()
                .map(MatchResult::group)
                .map(mul -> NUMBER_PATTERN.matcher(mul).results()
                        .map(MatchResult::group)
                        .map(Long::parseLong)
                        .reduce(1L, (a, b) -> a * b))
                .mapToLong(Long::longValue)
                .sum();
    }

}
