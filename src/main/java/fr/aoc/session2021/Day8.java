package fr.aoc.session2021;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static fr.aoc.common.Utils.REGEX_NEW_LINE;

@Slf4j
public class Day8 {

    ArrayList<ArrayList<String>> digitsOutput;
    ArrayList<ArrayList<String>> digitsInput;

    public static void main(String[] args) {
        Day8 day8 = new Day8();
        day8.readInput("src/main/resources/2021/day8/input.txt");
        log.info("Somme : {}", day8.sumOfOutput());
    }

    private void readInput(String filePath) {
        try (FileInputStream fis = new FileInputStream(filePath)) {
            String inputStr = IOUtils.toString(fis, StandardCharsets.UTF_8);
            digitsInput = new ArrayList<>(getDigitInputOrOutput(inputStr, 0));
            digitsOutput = new ArrayList<>(getDigitInputOrOutput(inputStr, 1));
            int answerPart1 = digitsOutput.stream()
                    .map(digits -> digits.stream()
                            .filter(digit -> digit.length() == 2 || digit.length() == 3 || digit.length() == 4 || digit.length() == 7)
                            .toList())
                    .map(List::size)
                    .reduce(0, Integer::sum);
            log.info("Nombre de 1, 4, 7 et 8 : {}", answerPart1);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private double sumOfOutput() {
        double total = 0;
        for (int lineIndex = 0; lineIndex < digitsOutput.size(); lineIndex++) {
            double tmpTotal = 0;
            ArrayList<String> decoder = populateMap(digitsInput.get(lineIndex));

            for (int digitIndex = 0; digitIndex < digitsOutput.get(lineIndex).size(); digitIndex++) {
                tmpTotal += Math.pow(10d, 3d - digitIndex) * decodeDigit(digitsOutput.get(lineIndex).get(digitIndex), decoder);
            }

            total += tmpTotal;
        }

        return total;
    }

    private ArrayList<String> populateMap(ArrayList<String> digits) {
        Map<Integer, String> map = new HashMap<>();
        ArrayList<String> workingDigits = new ArrayList<>(digits);
        ArrayList<String> decoder = new ArrayList<>();

        map.put(1, workingDigits.stream().filter(digit -> digit.length() == 2).findFirst().get());
        workingDigits.remove(map.get(1));

        map.put(4, workingDigits.stream().filter(digit -> digit.length() == 4).findFirst().get());
        workingDigits.remove(map.get(4));

        map.put(7, workingDigits.stream().filter(digit -> digit.length() == 3).findFirst().get());
        workingDigits.remove(map.get(7));

        map.put(8, workingDigits.stream().filter(digit -> digit.length() == 7).findFirst().get());
        workingDigits.remove(map.get(8));

        map.put(3, workingDigits.stream().filter(digit -> digit.length() == 5).filter(digit -> containsWholeString(map.get(1), digit)).findFirst().get());
        workingDigits.remove(map.get(3));

        map.put(6, workingDigits.stream().filter(digit -> digit.length() == 6).filter(digit -> !containsWholeString(map.get(1), digit)).findFirst().get());
        workingDigits.remove(map.get(6));

        map.put(9, workingDigits.stream().filter(digit -> digit.length() == 6).filter(digit -> containsWholeString(map.get(4), digit)).findFirst().get());
        workingDigits.remove(map.get(9));

        map.put(0, workingDigits.stream().filter(digit -> digit.length() == 6).findFirst().get());
        workingDigits.remove(map.get(0));

        String lowerLeftSegment = map.get(8).replaceAll("(" + String.join("|", map.get(9).split("")) + ")", "");
        map.put(2, workingDigits.stream().filter(digit -> digit.contains(lowerLeftSegment)).findFirst().get());
        workingDigits.remove(map.get(2));

        map.put(5, workingDigits.get(0));

        for (int index = 0; index < digits.size(); index++) {
            decoder.add(map.get(index));
        }

        return decoder;
    }

    private double decodeDigit(String digit, ArrayList<String> decoder) {
        if (digit.length() == 2) return 1d;
        if (digit.length() == 3) return 7d;
        if (digit.length() == 4) return 4d;
        if (digit.length() == 7) return 8d;

        return decoder.stream()
                .filter(digitCode -> containsWholeString(digitCode, digit) && digitCode.length() == digit.length())
                .map(decoder::indexOf)
                .findFirst()
                .get();
    }

    private boolean containsWholeString(String contained, String container) {
        List<String> containedCharArray = Arrays.stream(contained.split("")).toList();

        for (int indexChar = 0; indexChar < containedCharArray.size(); indexChar++) {
            if (!container.contains(containedCharArray.get(indexChar))) {
                return false;
            }
        }

        return true;
    }

    private ArrayList<ArrayList<String>> getDigitInputOrOutput(String inputStr, int inputOrOutput) {
        return Arrays.stream(inputStr.split(REGEX_NEW_LINE))
                .map(digits -> Arrays.stream(digits.split("\\|")[inputOrOutput].split("\\s"))
                        .filter(element -> element != null && !element.isEmpty() && !element.trim().isEmpty())
                        .collect(Collectors.toCollection(ArrayList::new)))
                .collect(Collectors.toCollection(ArrayList::new));
    }

}
