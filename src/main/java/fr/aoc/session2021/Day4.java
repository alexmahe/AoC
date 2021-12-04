package fr.aoc.session2021;

import org.apache.commons.io.IOUtils;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day4 {

    private ArrayList<Integer> drawOrder;
    private final ArrayList<ArrayList<ArrayList<Integer>>> boards = new ArrayList<>();
    private final ArrayList<ArrayList<ArrayList<Boolean>>> markedBoards = new ArrayList<>();
    private ArrayList<Boolean> wonBoards = new ArrayList<>();
    private boolean firstVictory = false;
    private int victoryCounter = 0;

    public static void main(String[] args) {
        // GIVEN
        Day4 day4 = new Day4();
        day4.readInput("src/main/resources/2021/day4/input.txt");
        day4.readDrawnNumber();
    }

    private void readInput(String filePath) {
        List<String> inputStrList;

        try (FileInputStream fis = new FileInputStream(filePath)) {
            String inputStr = IOUtils.toString(fis, StandardCharsets.UTF_8);
            inputStrList = Arrays.stream(inputStr.split("(\\r\\n|\\r|\\n)")).toList();
            populateInputVars(inputStrList);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void populateInputVars(List<String> inputStrList) {
        ArrayList<ArrayList<Integer>> board = new ArrayList<>();
        ArrayList<ArrayList<Boolean>> markedBoard = new ArrayList<>();

        drawOrder = Arrays.stream(inputStrList.get(0).split(","))
                        .map(Integer::parseInt)
                        .collect(Collectors.toCollection(ArrayList::new));

        for (int line = 2; line < inputStrList.size(); line++) {
            if (inputStrList.get(line).isEmpty()) {
                boards.add(board);
                board = new ArrayList<>();

                markedBoards.add(markedBoard);
                markedBoard = new ArrayList<>();
            } else {
                ArrayList<Integer> newBoardLine = Arrays.stream(inputStrList.get(line).split("\\s")).filter(str -> !str.isEmpty()).map(Integer::parseInt).collect(Collectors.toCollection(ArrayList::new));
                markedBoard.add(Stream.generate(() -> Boolean.FALSE).limit(newBoardLine.size()).collect(Collectors.toCollection(ArrayList::new)));
                board.add(newBoardLine);
            }
        }
        boards.add(board);
        markedBoards.add(markedBoard);
        wonBoards = Stream.generate(() -> Boolean.FALSE).limit(boards.size()).collect(Collectors.toCollection(ArrayList::new));
    }

    private void readDrawnNumber() {
        for (int drawnNumber : drawOrder) {
            checkIfMarked(drawnNumber);
            if (checkVictory(drawnNumber)) break;
        }
    }

    private void checkIfMarked(int drawnNumber) {
        for (int boardNumber = 0; boardNumber < boards.size(); boardNumber++) {
            ArrayList<ArrayList<Integer>> board = boards.get(boardNumber);

            for (int boardLineNumber = 0; boardLineNumber < board.size(); boardLineNumber++) {
                ArrayList<Integer> boardLine = board.get(boardLineNumber);

                for (int boardColNumber = 0; boardColNumber < boardLine.size(); boardColNumber++) {
                    if (boardLine.get(boardColNumber) == drawnNumber) {
                        markedBoards.get(boardNumber).get(boardLineNumber).set(boardColNumber, true);
                    }
                }
            }
        }
    }

    private boolean checkVictory(int drawnNumber) {

        for (int boardNumber = 0; boardNumber < boards.size(); boardNumber++) {

            if (isBoardVictorious(markedBoards.get(boardNumber)) && Boolean.TRUE.equals(!wonBoards.get(boardNumber))) {
                wonBoards.set(boardNumber, true);
                victoryCounter++;

                if (!firstVictory) {
                    firstVictory = true;
                    System.out.printf("[checkVictory] First Victorious board : %n%s%n", formattingBoardForLog(boards.get(boardNumber)));
                    System.out.printf("[checkVictory] Marked version : %n%s%n", formattingMarkedBoardForLog(markedBoards.get(boardNumber)));
                    postVictoryScoreCalc(boardNumber, drawnNumber);
                }

                if (victoryCounter == boards.size()) {
                    System.out.printf("[checkVictory] Last Victorious board : %n%s%n", formattingBoardForLog(boards.get(boardNumber)));
                    System.out.printf("[checkVictory] Marked version : %n%s%n", formattingMarkedBoardForLog(markedBoards.get(boardNumber)));
                    postVictoryScoreCalc(boardNumber, drawnNumber);

                    return true;
                }

            }
        }

        return false;
    }

    private boolean isBoardVictorious(ArrayList<ArrayList<Boolean>> markedBoard) {
        boolean victory = false;

        for (int index = 0; index < markedBoard.size(); index++) {
            int finalIndex = index;

            victory = markedBoard.get(finalIndex).stream().allMatch(markedNumber -> markedNumber) || markedBoard.stream().allMatch(markedNumber -> markedNumber.get(finalIndex));

            if (victory) return true;
        }

        return false;
    }

    private void postVictoryScoreCalc(int boardNumber, int drawnNumber) {
        ArrayList<ArrayList<Integer>> board = boards.get(boardNumber);
        int sumOfUnmarkedNumbers = board.stream()
                                    .map(boardLine -> boardLine.stream()
                                            .filter(number -> !markedBoards.get(boardNumber).get(board.indexOf(boardLine)).get(boardLine.indexOf(number)))
                                            .reduce(0, (a, b) -> a + b))
                                    .reduce(0, (a, b) -> a + b);
        System.out.printf("[postVictoryScoreCalc] Sum of unmarked number : %d%n", sumOfUnmarkedNumbers);
        System.out.printf("[postVictoryScoreCalc] End score : %d%n", sumOfUnmarkedNumbers * drawnNumber);
    }

    private String formattingBoardForLog(ArrayList<ArrayList<Integer>> board) {
        return board.stream()
                .map(boardLine -> boardLine.stream().map(String::valueOf).collect(Collectors.joining(" ")))
                .collect(Collectors.joining("\n"));
    }

    private String formattingMarkedBoardForLog(ArrayList<ArrayList<Boolean>> board) {
        return board.stream()
                .map(boardLine -> boardLine.stream().map(String::valueOf).collect(Collectors.joining(" ")))
                .collect(Collectors.joining("\n"));
    }

}
