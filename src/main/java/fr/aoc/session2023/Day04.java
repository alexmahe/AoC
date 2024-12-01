package fr.aoc.session2023;

import fr.aoc.common.Utils;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static fr.aoc.common.Utils.NUMBER_PATTERN;

@Slf4j
public class Day04 {

    public static void main(String[] args) throws IOException {
        var today = new Day04();
        var input = Utils.readInputSplitOnNewLines("src/main/resources/2023/day04/input");

        var gamesSolved = input.stream()
                .map(today::parseGame)
                .toList();

        var scoreSum = gamesSolved.stream()
                .filter(score -> score > 0)
                .mapToDouble(score -> Math.pow(2, score - 1D))
                .sum();
        log.info("Score of games : {}", scoreSum);

        var numberOfGames = gamesSolved.size();
        var cardsCopies = new ArrayList<>(Stream.generate(() -> 1).limit(numberOfGames).toList());
        for (int gameIndex = 0; gameIndex < numberOfGames; gameIndex++) {
            var score = gamesSolved.get(gameIndex);
            if (score == 0) continue;
            for (int copyIndex = Math.min(numberOfGames, gameIndex + 1); copyIndex < Math.min(gameIndex + score + 1, numberOfGames); copyIndex++) {
                cardsCopies.set(copyIndex, cardsCopies.get(copyIndex) + cardsCopies.get(gameIndex));
            }
        }
        var sumOfCards = cardsCopies.stream()
                .mapToInt(Integer::intValue)
                .sum();
        log.info("Number of cards : {}", sumOfCards);
    }

    private long parseGame(String gameStr) {
        var split = gameStr.split(":")[1].split("\\|");
        return solveGame(getNumbers(split[1]), getNumbers(split[0]));
    }

    private long solveGame(List<Integer> revealedAnwsers, List<Integer> validAnswers) {
        return revealedAnwsers.stream()
                .filter(validAnswers::contains)
                .count();
    }

    private List<Integer> getNumbers(String strToParse) {
        return NUMBER_PATTERN.matcher(strToParse).results()
                .map(MatchResult::group)
                .map(Integer::parseInt)
                .toList();
    }
}
