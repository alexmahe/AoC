package fr.aoc.session2021;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
public class Day17 {

    private List<Integer> xZone;
    private List<Integer> yZone;

    public static void main(String[] args) {
        long startingTime = System.currentTimeMillis();
        Day17 day17 = new Day17();
        day17.readInput("src/main/resources/2021/day17/input.txt");
        ArrayList<Integer> possibleHighestYStartingSpeed = day17.possibleHighestYStartingSpeed();
        ArrayList<Integer> allPossibleYStartingSpeed = day17.allPossibleYStartingSpeed();
        ArrayList<Integer> possibleXStartingSpeed = day17.possibleXStartingSpeed();

        int highestY = day17.findHighestPossibleYPos(possibleHighestYStartingSpeed);
        log.info("Highest point reached : {}", highestY);

        ArrayList<ArrayList<Integer>> allPossibleLaunch = day17.findAllPossibleLaunch(possibleXStartingSpeed, allPossibleYStartingSpeed);
        log.info("Number of possible launch : {}", allPossibleLaunch.size());
        log.info("Time elapsed : {}", System.currentTimeMillis() - startingTime);
    }

    private void readInput(String filePath) {
        try (FileInputStream fis = new FileInputStream(filePath)) {
            String[] positions = IOUtils.toString(fis, StandardCharsets.UTF_8).split(", ");

            String[] xPositions = positions[0].substring(positions[0].lastIndexOf("x") + 2).split("\\.\\.");
            xZone = Arrays.stream(xPositions).map(Integer::parseInt).toList();

            String[] yPositions = positions[1].substring(positions[1].lastIndexOf("y") + 2).split("\\.\\.");
            yZone = Arrays.stream(yPositions).map(Integer::parseInt).toList();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private ArrayList<Integer> possibleHighestYStartingSpeed() {
        ArrayList<Integer> possibleYStartingSpeed = new ArrayList<>();
        int startingSpeed = 2;

        while (-startingSpeed - 1 >= yZone.get(0)) {
            if (willLandInsideZoneForY(startingSpeed)) possibleYStartingSpeed.add(startingSpeed);
            startingSpeed++;
        }

        return possibleYStartingSpeed;
    }

    private ArrayList<Integer> allPossibleYStartingSpeed() {
        ArrayList<Integer> possibleYStartingSpeed = new ArrayList<>();
        int startingSpeed = yZone.get(0);
        boolean allFound = false;

        while (!allFound) {
            if (willLandInsideZoneForY(startingSpeed)) possibleYStartingSpeed.add(startingSpeed);
            if (startingSpeed > 1 && -startingSpeed - 1 <= yZone.get(0)) allFound = true;
            startingSpeed++;
        }

        return possibleYStartingSpeed;
    }

    private ArrayList<Integer> possibleXStartingSpeed() {
        ArrayList<Integer> possibleXStartingSpeed = new ArrayList<>();
        int startingSpeed = (int) (Math.sqrt(xZone.get(0)) / 2);

        while (startingSpeed <= xZone.get(1)) {
            if (willLandInsideZoneForX(startingSpeed)) possibleXStartingSpeed.add(startingSpeed);
            startingSpeed++;
        }

        return possibleXStartingSpeed;
    }

    private int findHighestPossibleYPos(ArrayList<Integer> startYSpeed) {
        int yIndex = startYSpeed.size() - 1;

        while (yIndex >= 0) {
            if (existXPossible(startYSpeed.get(yIndex))) {
                return calcHighestPoint(startYSpeed.get(yIndex));
            }
            yIndex--;
        }

        return 0;
    }

    private ArrayList<ArrayList<Integer>> findAllPossibleLaunch(ArrayList<Integer> startingXSpeed, ArrayList<Integer> startingYSpeed) {
        ArrayList<ArrayList<Integer>> possibleLaunch = new ArrayList<>();

        for (int xIndex = 0; xIndex < startingXSpeed.size(); xIndex++) {
            for (int yIndex = 0; yIndex < startingYSpeed.size(); yIndex++) {
                if (isPossibleForXY(startingXSpeed.get(xIndex), startingYSpeed.get(yIndex))) {
                    possibleLaunch.add(new ArrayList<>(Arrays.asList(startingXSpeed.get(xIndex), startingYSpeed.get(yIndex))));
                }
            }
        }

        return possibleLaunch;
    }

    private boolean existXPossible(int yStartingSpeed) {
        for (int xStartingSpeed = 0; xStartingSpeed <= xZone.get(1); xStartingSpeed++) {
            if (isPossibleForXY(xStartingSpeed, yStartingSpeed)) return true;
        }
        return false;
    }

    private boolean isPossibleForXY(int startingXSpeed, int startingYSpeed) {
        Probe probe = new Probe(startingXSpeed, startingYSpeed);
        while (probe.getXPos() < xZone.get(1) && probe.getYPos() > yZone.get(0)) {
            probe.proceedStep();
            if (isInsideZone(probe)) return true;
        }
        return false;
    }

    private boolean willLandInsideZoneForY(int yStartingSpeed) {
        int yPos = 0;
        int ySpeed = yStartingSpeed;

        while (yPos >= yZone.get(0)) {
            if (isInsideZone(yPos, yZone.get(0), yZone.get(1))) return true;
            yPos += ySpeed;
            ySpeed--;
        }

        return false;
    }

    private boolean willLandInsideZoneForX(int xStartingSpeed) {
        int xPos = 0;
        int xSpeed = xStartingSpeed;

        while (xPos <= xZone.get(1) && xSpeed > 0) {
            if (isInsideZone(xPos, xZone.get(0), xZone.get(1))) return true;
            xPos += xSpeed;
            xSpeed--;
        }

        return false;
    }

    private int calcHighestPoint(int yStartingSpeed) {
        return yStartingSpeed > 1 ? (yStartingSpeed * (yStartingSpeed + 1)) / 2 : -1;
    }

    private boolean isInsideZone(Probe probe) {
        return isInsideZone(probe.getXPos(), xZone.get(0), xZone.get(1)) &&
                isInsideZone(probe.getYPos(), yZone.get(0), yZone.get(1));
    }

    private boolean isInsideZone(int pos, int min, int max) {
        return min <= pos && pos <= max;
    }

    @Getter
    @Setter
    private class Probe {
        private int xPos = 0;
        private int yPos = 0;
        private int xStartingSpeed;
        private int yStartingSpeed;
        private int xSpeed;
        private int ySpeed;

        public Probe(int xSpeed, int ySpeed) {
            this.xStartingSpeed = xSpeed;
            this.yStartingSpeed = ySpeed;
            this.xSpeed = xSpeed;
            this.ySpeed = ySpeed;
        }

        private void proceedStep() {
            if (this.xSpeed != 0) {
                this.xPos += this.xSpeed;
                if (this.xSpeed > 0) this.xSpeed--;
                if (this.xSpeed < 0) this.xSpeed++;
            }

            this.yPos += this.ySpeed;
            this.ySpeed--;
        }
    }

}
