package fr.aoc.session2022;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static fr.aoc.common.Utils.REGEX_NEW_LINE;
import static fr.aoc.session2022.Day2.Signs.PAPER;
import static fr.aoc.session2022.Day2.Signs.ROCK;
import static fr.aoc.session2022.Day2.Signs.SCISSORS;

@Slf4j
public class Day2 {

    private final Map<String, Signs> SIGNS_TO_RESULT = Map.of(
            "A", ROCK, "X", ROCK,
            "B", PAPER, "Y", PAPER,
            "C", SCISSORS, "Z", SCISSORS
    );

    public static void main(String[] args) throws IOException {
        Day2 day2 = new Day2();
        String input = day2.readInput("src/main/resources/2022/day2/input.txt");

        int answer1 = day2.processInputAnswer1(input);
        int answer2 = day2.processInputAnswer2(input);

        log.info("Score (answer 1) : {}", answer1);
        log.info("Score (answer 2) : {}", answer2);
    }

    private String readInput(String filepath) throws IOException {
        try (FileInputStream fis = new FileInputStream(filepath)) {
            return IOUtils.toString(fis, StandardCharsets.UTF_8);
        }
    }

    private int processInputAnswer1(String input) throws IOException {
        return Arrays.stream(input.split(REGEX_NEW_LINE))
                .mapToInt(round -> {
                    List<Signs> signs = getSignsForRound(round);
                    return 3 * calcScoreMult(signs.get(0), signs.get(1)) + signs.get(1).value;
                })
                .sum();
    }

    private int processInputAnswer2(String input) {
        return Arrays.stream(input.split(REGEX_NEW_LINE))
                .mapToInt(this::calcScoreForRoundPredict)
                .sum();
    }

    private int calcScoreForRoundPredict(String round) {
        var signs = getSignsForRound(round);

        // we swicht on the sign of the player
        // if we have ROCK, we need to lose so we return what the opponent win against
        // if we have PAPER, we need a draw so we return the same sign as the opponent
        // if we have SCISSORS, we need to win so we return what the opponent lose against
        return switch (signs.get(1)) {
            case ROCK -> signs.get(0).winAgainst;
            case PAPER -> 3 + signs.get(0).value;
            case SCISSORS -> 6 + signs.get(0).loseAgainst;
            default -> throw new UnsupportedOperationException("Cas non géré");
        };
    }

    private List<Signs> getSignsForRound(String round) {
        return Arrays.stream(round.split(" "))
                .map(SIGNS_TO_RESULT::get)
                .toList();
    }

    /**
     * Verifie si c'est une victoire, un nul ou une défaite
     * Renvoie 0 pour une défaite, 1 pour un nul et 2 pour une victoire
     */
    private int calcScoreMult(Signs opponentSign, Signs selfSign) {
        return switch (selfSign.compare(opponentSign)) {
            // loss
            case -1 -> 0;
            // draw
            case 0 -> 1;
            // win
            case 1 -> 2;
            default -> throw new UnsupportedOperationException("Cas non géré");
        };
    }

    @AllArgsConstructor
    public enum Signs {
        ROCK(1, 3, 2),
        PAPER(2, 1, 3),
        SCISSORS(3, 2, 1);

        private final int value;
        private final int winAgainst;
        private final int loseAgainst;

        public int compare(Signs sign) {
            // draw
            if (this.value == sign.value) return 0;
            // win
            else if (this.winAgainst == sign.value) return 1;
            // lose
            else return -1;
        }
    }
}
