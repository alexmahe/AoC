package fr.aoc.session2021;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static fr.aoc.common.Utils.REGEX_NEW_LINE;

@Slf4j
public class Day18 {

    public static void main(String[] args) {
        Day18 day18 = new Day18();

        String filePath = "src/main/resources/2021/day18/input.txt";
        List<Number> numberList = day18.readInput(filePath);
        Number total = numberList.get(0);
        for (int numberIndex = 1; numberIndex < numberList.size(); numberIndex++) {
            total = day18.addNumbers(total, numberList.get(numberIndex));
        }

        log.info("Résultat de l'addition : {}", total);
        log.info("Magnitude du résultat : {}", total.getMagnitude());


        ArrayList<Integer> magnitudeList = new ArrayList<>();
        for (int indexNumber = 0; indexNumber < numberList.size(); indexNumber++) {
            for (int indexSecondNumber = indexNumber + 1; indexSecondNumber < numberList.size(); indexSecondNumber++) {
                numberList = day18.readInput(filePath);
                magnitudeList.add(day18.addNumbers(numberList.get(indexNumber), numberList.get(indexSecondNumber)).getMagnitude());
                numberList = day18.readInput(filePath);
                magnitudeList.add(day18.addNumbers(numberList.get(indexSecondNumber), numberList.get(indexNumber)).getMagnitude());
            }
        }

        log.info("Magnitude max : {}", Collections.max(magnitudeList));
    }

    private List<Number> readInput(String filePath) {
        List<Number> numbersList = null;

        try (FileInputStream fis = new FileInputStream(filePath)) {
            String numbersStr = IOUtils.toString(fis, StandardCharsets.UTF_8);
            numbersList = Arrays.stream(numbersStr.split(REGEX_NEW_LINE))
                    .filter(line -> line != null && !line.isEmpty() && !line.trim().isEmpty())
                    .map(this::initNumberFromStr)
                    .toList();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return numbersList;
    }

    private Number initNumberFromStr(String numberStr) {
        Number number = new Number();

        if ("[".equals(Character.toString(numberStr.charAt(0))) &&
                "]".equals(Character.toString(numberStr.charAt(numberStr.length() - 1)))) {
            String inside = numberStr.substring(1, numberStr.length() - 1);
            String rightPart;

            try {
                int leftInt = Integer.parseInt(inside.substring(0, inside.indexOf(",")));
                number.setLeftInt(leftInt);
                rightPart = inside.substring(inside.indexOf(",") + 1);
            } catch (NumberFormatException exception) {
                int endOfLeftNumber = findRelevantClosingBracket(inside);
                String leftNumberStr = inside.substring(0, endOfLeftNumber + 1);
                Number leftNumber = initNumberFromStr(leftNumberStr);

                leftNumber.setParent(number);
                number.setLeftNumb(leftNumber);
                rightPart = inside.substring(endOfLeftNumber + 2);
            }

            try {
                int rightInt = Integer.parseInt(rightPart.substring(0, Math.max(rightPart.indexOf("]"), 1)));
                number.setRightInt(rightInt);
            } catch (NumberFormatException exception) {
                int endOfRightNumber = findRelevantClosingBracket(rightPart);
                String rightNumberStr = rightPart.substring(0, endOfRightNumber + 1);
                Number rightNumber = initNumberFromStr(rightNumberStr);

                rightNumber.setParent(number);
                number.setRightNumb(rightNumber);
            }
        }

        return number;
    }

    private Number addNumbers(Number number1, Number number2) {
        Number addition = new Number(number1, number2, null);

        number1.setParent(addition);
        number2.setParent(addition);
        reduce(addition);

        return addition;
    }

    private void reduce(Number number) {
        boolean hasExplodedOrSplitted = true;
        while (hasExplodedOrSplitted) {
            if (explode(number)) continue;
            if (split(number)) continue;
            hasExplodedOrSplitted = false;
        }
    }

    private boolean explode(Number number) {
        if (number.getLeftInt() != -1 && number.getRightInt() != -1 && number.getDepth() == 4) {
            Number closestOnLeft = findClosestOnLeft(number);
            Number closestOnRight = findClosestOnRight(number);

            if (closestOnLeft != null) {
                if (closestOnLeft.isInChildren(number)) {
                    closestOnLeft.addToLeftInt(number.getLeftInt());
                } else {
                    closestOnLeft.addToRightInt(number.getLeftInt());
                }
            }

            if (closestOnRight != null) {
                if (closestOnRight.isInChildren(number)) {
                    closestOnRight.addToRightInt(number.getRightInt());
                } else {
                    closestOnRight.addToLeftInt(number.getRightInt());
                }
            }

            Number parent = number.getParent();
            if (parent.getLeftNumb() == number) {
                parent.setLeftInt(0);
                parent.setLeftNumb(null);
            } else if (parent.getRightNumb() == number) {
                parent.setRightInt(0);
                parent.setRightNumb(null);
            }

            return true;
        }

        if (number.getLeftNumb() != null) {
            if (explode(number.getLeftNumb())) return true;
        }

        if (number.getRightNumb() != null) return explode(number.getRightNumb());

        return false;
    }

    private boolean split(Number number) {
        if (number.getLeftInt() >= 10 && number.getLeftNumb() == null) {
            int newLeftInt = number.getLeftInt() / 2;
            Number newLeftNum = new Number(newLeftInt, number.getLeftInt() % 2 == 0 ? newLeftInt : newLeftInt + 1, number);
            number.setLeftNumb(newLeftNum);
            number.setLeftInt(-1);
            return true;
        }

        if (number.getLeftInt() == -1 && number.getLeftNumb() != null) {
            if (split(number.getLeftNumb())) return true;
        }

        if (number.getRightInt() >= 10 && number.getRightNumb() == null) {
            int newRightInt = number.getRightInt() / 2;
            Number newRightNum = new Number(newRightInt, number.getRightInt() % 2 == 0 ? newRightInt : newRightInt + 1, number);
            number.setRightNumb(newRightNum);
            number.setRightInt(-1);
            return true;
        }

        if (number.getRightInt() == -1 && number.getRightNumb() != null) {
            return split(number.getRightNumb());
        }

        return false;
    }

    private int findRelevantClosingBracket(String numberStr) {
        int openBrackets = 0;
        int charIndex = 0;

        while (charIndex < numberStr.length()) {
            if ('[' == numberStr.charAt(charIndex)) openBrackets++;
            if (']' == numberStr.charAt(charIndex)) openBrackets--;

            if (openBrackets == 0) return charIndex;

            charIndex++;
        }

        return -1;
    }

    private Number findClosestOnRight(Number number) {
        Number parent;

        if (number.getParent() != null) {
            parent = number.getParent();
        } else return null;

        if (parent.getRightInt() != -1) return parent;
        if (parent.getRightNumb() != number) return findLeftMostNumber(parent.getRightNumb());
        else return findClosestOnRight(parent);
    }

    private Number findClosestOnLeft(Number number) {
        Number parent;

        if (number.getParent() != null) {
            parent = number.getParent();
        } else return null;

        if (parent.getLeftInt() != -1) return parent;
        if (parent.getLeftNumb() != number) return findRightMostNumber(parent.getLeftNumb());
        else return findClosestOnLeft(parent);
    }

    private Number findRightMostNumber(Number number) {
        if (number.getRightInt() != -1) return number;
        else return findRightMostNumber(number.getRightNumb());
    }

    private Number findLeftMostNumber(Number number) {
        if (number.getLeftInt() != -1) return number;
        else return findLeftMostNumber(number.getLeftNumb());
    }

    @Getter @Setter
    @NoArgsConstructor
    private class Number {
        private Number parent = null;
        private Number leftNumb = null;
        private Number rightNumb = null;
        private int leftInt = -1;
        private int rightInt = -1;

        public Number(Number leftNumb, Number rightNumb, Number parent) {
            this.leftNumb = leftNumb;
            this.rightNumb = rightNumb;
            this.parent = parent;
        }

        public Number (int leftInt, Number rightNumb, Number parent) {
            this.leftInt = leftInt;
            this.rightNumb = rightNumb;
            this.parent = parent;
        }

        public Number (Number leftNumb, int rightInt, Number parent) {
            this.leftNumb = leftNumb;
            this.rightInt = rightInt;
            this.parent = parent;
        }

        public Number(int leftInt, int rightInt, Number parent) {
            this.leftInt = leftInt;
            this.rightInt = rightInt;
            this.parent = parent;
        }

        public int getDepth() {
            if (this.parent == null) return 0;
            return 1 + this.parent.getDepth();
        }

        public int getMagnitude() {
            int leftMagnitude;
            int rightMagnitude;

            if (this.leftInt != -1) leftMagnitude = 3 * this.leftInt;
            else leftMagnitude = 3 * leftNumb.getMagnitude();
            if (this.rightInt != -1) rightMagnitude = 2 * this.rightInt;
            else rightMagnitude = 2 * rightNumb.getMagnitude();

            return leftMagnitude + rightMagnitude;
        }

        public int addToLeftInt(int value) {
            return this.leftInt += value;
        }

        public int addToRightInt(int value) {
            return this.rightInt += value;
        }

        private boolean isInChildren(Number number) {
            if (this.leftInt != -1 && this.rightInt != -1) return false;
            if (this.leftNumb == number || this.rightNumb == number) return true;
            return (this.leftNumb != null && this.leftNumb.isInChildren(number)) ||
                    (this.rightNumb != null && this.rightNumb.isInChildren(number));
        }

        @Override
        public String toString() {
            String leftNumberStr;
            String rightNumberStr;

            if (this.leftInt != -1) leftNumberStr = Integer.toString(this.leftInt);
            else if (leftNumb != null) leftNumberStr = leftNumb.toString();
            else leftNumberStr = "null";
            if (this.rightInt != -1) rightNumberStr = Integer.toString(this.rightInt);
            else if (rightNumb != null) rightNumberStr = rightNumb.toString();
            else rightNumberStr = "null";

            return "[" + leftNumberStr + "," + rightNumberStr + "]";
        }
    }
}
