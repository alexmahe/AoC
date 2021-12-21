package fr.aoc.session2021;

import org.apache.commons.io.IOUtils;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Day21 {

    public static void main(String[] args) {
        Day21 day21 = new Day21();
        ArrayList<Integer> playerStartingPositions = day21.readInput("src/main/resources/2021/day21/input.txt");
        System.out.printf("Answer part 1 : %s%n", day21.playDeterministic(playerStartingPositions));
    }

    private ArrayList<Integer> readInput(String filePath) {
        ArrayList<Integer> playerStartingPositions = new ArrayList<>();

        try (FileInputStream fis = new FileInputStream(filePath)) {
            String inputStr = IOUtils.toString(fis, StandardCharsets.UTF_8);
            playerStartingPositions = Arrays.stream(inputStr.split("(\r\n|\r|\n)"))
                    .filter(element -> element != null && !element.isEmpty() && !element.trim().isEmpty())
                    .map(element -> Character.toString(element.charAt(element.length() - 1)))
                    .map(Integer::parseInt)
                    .collect(Collectors.toCollection(ArrayList::new));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return playerStartingPositions;
    }

    private int playDeterministic(ArrayList<Integer> playerStartingPositions) {
        ArrayList<Integer> playerPositions = new ArrayList<>(playerStartingPositions);
        int dieRollNumber = 1, dieRollCounter = 0, player1Score = 0, player2Score = 0;

        while (true) {
            int playerNewPosition;

            if (dieRollCounter % 6 == 0) {
                playerNewPosition = calcNewPosition(playerPositions.get(0), calcForwardMove(dieRollNumber, 3));
                player1Score += playerNewPosition;
                playerPositions.set(0, playerNewPosition);
            } else {
                playerNewPosition = calcNewPosition(playerPositions.get(1), calcForwardMove(dieRollNumber, 3));
                player2Score += playerNewPosition;
                playerPositions.set(1, playerNewPosition);
            }

            if (dieRollNumber % 2 == 0) {
                System.out.printf("Die roll : %s%nPlayer 1 score : %s%nPlayer 1 position : %s%nPlayer 2 score : %s%nPlayer 2 position : %s%n%n%n",
                        dieRollNumber, player1Score, playerPositions.get(0), player2Score, playerPositions.get(1));
            }

            dieRollNumber = ((dieRollNumber + 3 - 1) % 100) + 1;
            dieRollCounter += 3;

            if (player1Score >= 1000 || player2Score >= 1000) {
                System.out.printf("Player 1 score : %s%nPlayer 2 score : %s%nNumber of rolls : %s%n", player1Score, player2Score, dieRollCounter);
                return dieRollCounter * Math.min(player1Score, player2Score);
            }
        }
    }

    private int calcForwardMove(int startingRoll, int numberOfRolls) {
        return IntStream.range(0, numberOfRolls)
                .map(iterate -> (startingRoll + iterate - 1) % 100 + 1)
                .sum() % 10;
    }

    private int calcNewPosition(int startingPosition, int advance) {
        int sum = startingPosition + advance;
        return sum > 10 ? sum - 10 : sum;
    }

}
