package fr.aoc.session2023;

import fr.aoc.common.Utils;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@Slf4j
public class Day02 {


    private static final String RED = "red";
    private static final int RED_LIMIT = 12;
    private static final String GREEN = "green";
    private static final int GREEN_LIMIT = 13;
    private static final String BLUE = "blue";
    private static final int BLUE_LIMIT = 14;

    public static void main(String[] args) throws IOException {
        var today = new Day02();
        var input = Utils.readInputSplitOnNewLines("src/main/resources/2023/day02/input");
        var games = input.stream().map(today::parseGame).toList();

        var sumOfValidIds = games.stream()
                .filter(today::isGameValid)
                .map(game -> games.indexOf(game) + 1)
                .mapToInt(Integer::intValue)
                .sum();
        log.info("La sommes des ids des games valides est : {}", sumOfValidIds);

        var sumOfPowers = games.stream()
                .map(today::powerOfGame)
                .mapToInt(Integer::intValue)
                .sum();
        log.info("La somme des puissances des games est : {}", sumOfPowers);
    }

    private Game parseGame(String line) {
        Pattern regex = Pattern.compile("((\\d+)\\s+(red|green|blue))");
        var game = new Game(new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
        regex.matcher(line).results().forEach(matchResult -> {
            switch (matchResult.group(3)) {
                case RED -> game.reds().add(Integer.parseInt(matchResult.group(2)));
                case GREEN -> game.greens().add(Integer.parseInt(matchResult.group(2)));
                case BLUE -> game.blues().add(Integer.parseInt(matchResult.group(2)));
            }
        });

        return game;
    }

    private boolean isGameValid(Game game) {
        return isColorValid(game.reds(), RED_LIMIT)
                && isColorValid(game.greens(), GREEN_LIMIT)
                && isColorValid(game.blues(), BLUE_LIMIT);
    }

    private boolean isColorValid(List<Integer> colorValues, int limit) {
        return colorValues.stream()
                .filter(value -> value > limit)
                .toList()
                .isEmpty();
    }

    private int powerOfGame(Game game) {
        return powerOfColor(game.reds())
                * powerOfColor(game.greens())
                * powerOfColor(game.blues());
    }

    private int powerOfColor(List<Integer> colorValues) {
        return colorValues.stream()
                .mapToInt(Integer::intValue)
                .max().getAsInt();
    }

    private record Game (List<Integer> reds, List<Integer> greens, List<Integer> blues) {}

}
